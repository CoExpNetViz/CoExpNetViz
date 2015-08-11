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
 *
 * @author sam
 */
public class RunAnalysisTask extends AbstractTask {

    private static final double PROG_CONN_COMPLETE = 0.8;
    private static final double PROG_NETW_COMPLETE = 0.9;
    
    private final CENVApplication application;
    private Task subTask;

    public RunAnalysisTask(CENVApplication cyAppManager) {
        this.application = cyAppManager;
        this.subTask = null;
    }
    
    private void pollCancelled() throws InterruptedException {
    	if (cancelled)
        	throw new InterruptedException("Task cancelled");
    }
    
    @Override
    public void cancel() {
    	if (this.subTask != null)
    		this.subTask.cancel(); // TODO this might throw null pointer due to race condition
    	super.cancel();
    }
    
    private void runSubTask(Task subTask, TaskMonitor tm) throws Exception {
    	this.subTask = subTask;
    	subTask.run(tm);
    	this.subTask = null;
    }

    /**
     * Does all work and shows the resulting network to the user in the
     * appropriate layout. (Exceptions thrown during execution are automatically
     * handled by cytoscape and shown in the gui)
     *
     * @param tm
     * @throws Exception
     */
    @Override
    public void run(TaskMonitor tm) throws Exception {
        tm.setTitle("Running CoExpNetViz");

    	tm.setProgress(-1.0); // start progress as indefinite
        Path networkDirectory = runOnServer(tm);
        pollCancelled();
        tm.setProgress(PROG_CONN_COMPLETE);
        CyNetworkView networkView = createNetwork(tm, networkDirectory);
        pollCancelled();
        tm.setProgress(PROG_NETW_COMPLETE);
        applyLayout(tm, networkView);
    }

    Path runOnServer(TaskMonitor tm) throws Exception {
        tm.setStatusMessage("Running CoExpNetViz");
        RunJobTask task = new RunJobTask(new JobServer(application), application.getCyModel().getJobDescription());
        runSubTask(task, tm);
        return task.getUnpackedResult();
    }

    CyNetworkView createNetwork(TaskMonitor tm, Path networkDirectory) throws Exception {
        tm.setStatusMessage("Creating network");
        return createNetworkView(networkDirectory); // TODO tasks in here are never cancelled
    }

    void applyLayout(TaskMonitor tm, CyNetworkView networkView) throws Exception {
    	tm.setStatusMessage("Applying layout");
    	FamLayout layout = (FamLayout) application.getCyLayoutAlgorithmManager().getLayout(FamLayout.NAME);
        TaskIterator tasks = layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "colour", "species");
        while (tasks.hasNext()) {
        	runSubTask(tasks.next(), tm);
        }
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

}
