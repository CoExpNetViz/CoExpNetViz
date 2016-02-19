package be.ugent.psb.coexpnetviz.gui;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 PSB/UGent
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

import be.ugent.psb.coexpnetviz.Context;
import be.ugent.psb.coexpnetviz.NotificationTask;
import be.ugent.psb.coexpnetviz.io.JobServer;
import be.ugent.psb.coexpnetviz.io.RunJobTask;
import be.ugent.psb.coexpnetviz.layout.FamLayout;
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

/**
 * Controls running an analysis
 * 
 * Similar to RunAnalysisController, but this controls the tasks being run for it, while the other one collects input from the GUI
 */
public class RunAnalysisTaskController implements Observer {
    
    private final Context application;
    private int step; // what stage of the analysis we're at
    private TaskIterator taskIterator;
    private RunJobTask runJobTask;
    private CyNetworkReader networkReader;
    private CyTableReader nodeTableReader;
    private CyTableReader edgeTableReader;

    public RunAnalysisTaskController(Context cyAppManager) {
        this.application = cyAppManager;
        step = 0;
        taskIterator = new TaskIterator();
        update(null, null); // fake update to initialise
        application.getTaskManager().execute(taskIterator);
    }
    
    @Override
	public void update(Observable o, Object notificationTask) {
		switch (step++) {
		case 0:
			// Run job on server, download response
	    	runJobTask = new RunJobTask(new JobServer(application), application.getCyModel().getJobDescription());
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
			getNetwork().getRow(getNetwork()).set(CyNetwork.NAME, application.getCyModel().getTitle());
	        application.getCyNetworkManager().addNetwork(getNetwork());
	        
	        // Apply tables to network
	        addApplyTableTask(nodeTableReader, CyNode.class, getNetwork());
	        addApplyTableTask(edgeTableReader, CyEdge.class, getNetwork());
	        
	        break;
	        
		case 4:
	        // Add network view
	        CyNetworkView networkView = application.getCyNetworkViewFactory().createNetworkView(getNetwork());
	        application.getCyNetworkViewManager().addNetworkView(networkView);
	        
	        // Apply network style
	        application.getVisualMappingManager().setVisualStyle(getStyle(Context.APP_NAME, getExtractedFile("cenv_style.xml")), networkView);

	        //add this data to the corestatus to pass it on to the next task
	        //which is applying the layout
	        application.getCyModel().getVisibleCevNetworks().add(getNetwork()); // TODO is this needed?
	        
	        // Apply layout
	    	FamLayout layout = (FamLayout) application.getCyLayoutAlgorithmManager().getLayout(FamLayout.NAME);
	    	taskIterator.append(layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "colour", "species"));
	        
	    	return; // return on the last step to skip adding another notification task
			
		default:
			assert false;
		}
		
		taskIterator.append(new NotificationTask(this));
	}
    
    private VisualStyle getStyle(String name) {
    	for (VisualStyle vs : application.getVisualMappingManager().getAllVisualStyles()) {
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
    		application.getLoadVizmapFileTaskFactory().loadStyles(stylePath.toFile());
        }
    	return getStyle(name);
    }
    
    /**
     * Load table from file
     */
    private CyTableReader addReadTableTask(Path tablePath, Class<? extends CyIdentifiable> type) {
    	CyTableReader tableReader = application.getCyTableReaderManager().getReader(tablePath.toUri(), null);
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
    	CyRootNetwork rootNetwork = application.getCyRootNetworkManager().getRootNetwork(network);
    	TaskIterator tasks = application.getImportDataTableTaskFactory().createTaskIterator(
    			table, false, false, Collections.singletonList(network), rootNetwork, joinColumn, type
    	);
    	taskIterator.append(tasks);
    }

	private CyNetworkReader addReadNetworkTask() {
		CyNetworkReader networkReader = application.getCyNetworkReaderManager().getReader(getExtractedFile("network.sif").toUri(), null);
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
