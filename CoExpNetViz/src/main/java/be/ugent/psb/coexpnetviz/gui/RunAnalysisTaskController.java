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

import be.ugent.psb.coexpnetviz.CENVApplication;
import be.ugent.psb.coexpnetviz.io.JobServer;
import be.ugent.psb.coexpnetviz.io.NetworkReader;
import be.ugent.psb.coexpnetviz.io.RunJobTask;
import be.ugent.psb.coexpnetviz.io.VizmapReader;
import be.ugent.psb.coexpnetviz.layout.FamLayout;
import be.ugent.psb.coexpnetviz.layout.FamLayoutTask;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * Controls running an analysis
 * 
 * Similar to RunAnalysisController, but this controls the tasks being run for it, while the other one collects input from the GUI
 */
public class RunAnalysisTaskController extends AbstractTask implements Observer {
    
    private final CENVApplication application;
    private int stage; // what stage of the analysis we're at
    private TaskIterator taskIterator;
    private RunJobTask runJobTask;

    public RunAnalysisTaskController(CENVApplication cyAppManager) {
        this.application = cyAppManager;
        stage = 0;
        taskIterator = new TaskIterator();
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
            new VizmapReader(application).readVIZ(stylePath);
        }
    	return getStyle(name);
    }

    /**
     * reads the network files and creates a view. Applies Style. Adds the
     * networks nodeTable and view to the coremodel.
     *
     * @throws java.lang.Exception
     * @param networkDir Directory with the sif and attr files
     */
    public CyNetworkView createNetworkView(Path networkDir) throws Exception {
    	CyNetwork network;
    	try {
            // Read network
            NetworkReader networkReader = new NetworkReader(application);
            networkReader.readSIF(networkDir.resolve("network.sif"));
            networkReader.readNodeAttributes(networkDir.resolve("network.node.attr"));
            networkReader.readEdgeAttributes(networkDir.resolve("network.edge.attr"));
            network = networkReader.getNetwork();
        } catch (IOException ex) {
            //when executed in a Task, this message will be shown to the user
            // (see documentation for org.cytoscape.work.Task)
            throw new Exception(String.format("An error ocurred while reading the network files%n%s%n", ex), ex);
        }

        // Add the network
    	network.getRow(network).set(CyNetwork.NAME, application.getCyModel().getTitle());
        application.getCyNetworkManager().addNetwork(network);
        
        // Add view
        CyNetworkView networkView = application.getCyNetworkViewFactory().createNetworkView(network);
        application.getCyNetworkViewManager().addNetworkView(networkView);
        application.getVisualMappingManager().setVisualStyle(getStyle(CENVApplication.APP_NAME, networkDir.resolve("cenv_style.xml")), networkView);

        //add this data to the corestatus to pass it on to the next task
        //which is applying the layout
        application.getCyModel().getVisibleCevNetworks().add(network);
        
        return networkView;
    }

	@Override
	public void update(Observable o, Object notificationTask) {
		Task[] newTasks = 
		switch (stage) {
		case 0:
			// Run job on server, download response
	    	RunJobTask runJobTask = new RunJobTask(new JobServer(application), application.getCyModel().getJobDescription());
	    	taskIterator.append(runJobTask);
	    	break;
	    	
		case 1:
	        return createNetworkView(runJobTask.getUnpackedResult()); // TODO tasks in here are never cancelled
	        
		case 2:
		}
		stage++;
		
		taskIterator.append(notificationTask);
		
        
        
        
        applyLayout(tm, networkView);
        tm.setStatusMessage("Applying layout");
    	FamLayout layout = (FamLayout) application.getCyLayoutAlgorithmManager().getLayout(FamLayout.NAME);
        TaskIterator tasks = layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "colour", "species");
        while (tasks.hasNext()) {
        	runSubTask(tasks.next(), tm);
        }
	}

}
