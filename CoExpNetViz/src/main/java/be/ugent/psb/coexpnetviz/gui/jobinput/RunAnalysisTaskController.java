package be.ugent.psb.coexpnetviz.gui.jobinput;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2016 PSB/UGent
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.nio.file.Path;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskIterator;

import be.ugent.psb.coexpnetviz.Context;
import be.ugent.psb.coexpnetviz.io.JobDescription;
import be.ugent.psb.coexpnetviz.io.JobServer;
import be.ugent.psb.coexpnetviz.io.RunJobTask;
import be.ugent.psb.coexpnetviz.layout.FamLayout;
import be.ugent.psb.util.cytoscape.NotificationTask;

/**
 * Creates and controls tasks to run a job on the CoExpNetViz server and show the resulting network.
 */
public class RunAnalysisTaskController implements Observer {
    
    private final Context context;
    private JobDescription jobDescription;
    private String networkName;
    private int step;  // what stage of the analysis we're at
    private TaskIterator taskIterator;
    private RunJobTask runJobTask;
    private CyNetworkReader networkReader;
    private CyTableReader nodeTableReader;
    private CyTableReader edgeTableReader;
    
    public RunAnalysisTaskController(Context context, JobDescription jobDescription, String networkName) {
        this.context = context;
        this.jobDescription = jobDescription;
        this.networkName = networkName;
        step = 0;
        taskIterator = new TaskIterator();
        update(null, null); // send fake update event to self to initialise the first step
        context.getTaskManager().execute(taskIterator);
    }
    
    @Override
	public void update(Observable o, Object notificationTask) {
    	/*
    	 * Note: the controller has a taskIterator to which we add tasks as we finish 
    	 * previous tasks in the iterator. Sometimes we need to wait for them to complete,
    	 * for that we use a NotificationTask that calls this method once it's run,
    	 * after which we can add the tasks of the next step to the still running task iterator.
    	 */
		switch (step++) {
		case 0:
			// Run job on server, download response
	    	runJobTask = new RunJobTask(new JobServer(), jobDescription);
	    	taskIterator.append(runJobTask);
	    	break;
	    	
		case 1:
			// Load network
			networkReader = addReadNetworkTask();
	        break;
	        
		case 2:
			// Load tables and apply them to the network
	        nodeTableReader = addReadTableTask(getExtractedFile("network.node.attr"), CyNode.class);
	        edgeTableReader = addReadTableTask(getExtractedFile("network.edge.attr"), CyEdge.class);
	        
	        break;
	        
		case 3:
			// Add the network
			getNetwork().getRow(getNetwork()).set(CyNetwork.NAME, networkName);
	        context.getCyNetworkManager().addNetwork(getNetwork());
	        
	        // Apply tables to network
	        addApplyTableTask(nodeTableReader, CyNode.class, getNetwork());
	        addApplyTableTask(edgeTableReader, CyEdge.class, getNetwork());
	        
	        break;
	        
		case 4:
	        // Add network view
	        CyNetworkView networkView = context.getCyNetworkViewFactory().createNetworkView(getNetwork());
	        context.getCyNetworkViewManager().addNetworkView(networkView);
	        
	        // Apply network style
	        context.getVisualMappingManager().setVisualStyle(getStyle(Context.APP_NAME, getExtractedFile("cenv_style.xml")), networkView);
	        
	        // Apply layout
	    	FamLayout layout = (FamLayout) context.getCyLayoutAlgorithmManager().getLayout(FamLayout.NAME);
	    	taskIterator.append(layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "colour", "species"));
	        
	    	return; // return on the last step to skip adding another notification task
			
		default:
			assert false;
		}
		
		taskIterator.append(new NotificationTask(this));
	}
    
    private VisualStyle getStyle(String name) {
    	for (VisualStyle vs : context.getVisualMappingManager().getAllVisualStyles()) {
            if (vs.getTitle().equals(name)) {
                return vs;
            }
        }
        return null;
    }
    
    /**
     * Get style, fall back to file if not found
     * 
     * @param stylePath Path to vizmap file to get, it must contain a style with name equals `name`
     */
    private VisualStyle getStyle(String name, Path stylePath) {
    	// Add the visual style no sooner than when it's needed and add it only once, To prevent cluttering the Style menu.
    	if (getStyle(name) == null) {
    		// Load vizmap file
    		context.getLoadVizmapFileTaskFactory().loadStyles(stylePath.toFile());
        }
    	return getStyle(name);
    }
    
    /**
     * Load table from file
     */
    private CyTableReader addReadTableTask(Path tablePath, Class<? extends CyIdentifiable> type) {
    	CyTableReader tableReader = context.getCyTableReaderManager().getReader(tablePath.toUri(), null);
    	taskIterator.append(tableReader);
    	return tableReader;
    }
    
    /**
     * Apply table to a network
     */
    private void addApplyTableTask(CyTableReader tableReader, Class<? extends CyIdentifiable> type, CyNetwork network) {
    	assert(tableReader.getTables().length == 1);
    	CyColumn joinColumn = network.getTable(type, CyNetwork.LOCAL_ATTRS).getPrimaryKey();
    	CyTable table = tableReader.getTables()[0];
    	CyRootNetwork rootNetwork = context.getCyRootNetworkManager().getRootNetwork(network);
    	TaskIterator tasks = context.getImportDataTableTaskFactory().createTaskIterator(
    			table, false, false, Collections.singletonList(network), rootNetwork, joinColumn, type
    	);
    	taskIterator.append(tasks);
    }

	private CyNetworkReader addReadNetworkTask() {
		CyNetworkReader networkReader = context.getCyNetworkReaderManager().getReader(getExtractedFile("network.sif").toUri(), null);
		taskIterator.append(networkReader);
		return networkReader;
	}

	private Path getExtractedFile(String fileName) {
		return runJobTask.getUnpackedResult().resolve(fileName);
	}

	private CyNetwork getNetwork() {
		assert networkReader.getNetworks().length == 1;
		CyNetwork network = networkReader.getNetworks()[0];
		return network;
	}

}
