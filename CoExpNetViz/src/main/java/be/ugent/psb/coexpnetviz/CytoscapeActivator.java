package be.ugent.psb.coexpnetviz;

import be.ugent.psb.coexpnetviz.gui.CENVModel;
import be.ugent.psb.coexpnetviz.gui.CENVNodeViewContextMenuFactory;

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

import be.ugent.psb.coexpnetviz.gui.controller.MenuAction;
import be.ugent.psb.coexpnetviz.layout.FamLayout;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.events.VisualStyleSetListener;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import static org.cytoscape.work.ServiceProperties.*;

/**
 * Activates CENV plugin in Cytoscape.
 *
 * @author Sam De Meyer
 */
public class CytoscapeActivator extends AbstractCyActivator {

	/**
	 * Entry point for the CoExpNetViz plugin for Cytoscape 3. Integrates our
	 * classes with those of Cytoscape, by adding Cytoscape menu items, attaching
	 * listeners to Cytoscape events, ...
	 */
    @Override
    public void start(BundleContext context) throws Exception {

    	//get CytoScape services
        //TODO: I recently discovered the existence of the CySwingAdapter class
        //      this is a Cytoscape api class that does exactly what is done
        //      below: wrapping instances of all Cytoscape model classes into
        //      one object that can easily be passed around.
        //
        //      Conclusion: CyServices below can be replaced by CySwingAppAdapter,
        //      this will clear a lot of code
        CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
        CyNetworkFactory cyNetworkFactory = getService(context, CyNetworkFactory.class);
        CyNetworkManager cyNetworkManager = getService(context, CyNetworkManager.class);
        CyRootNetworkManager cyRootNetworkManager = getService(context, CyRootNetworkManager.class);
        CyTableFactory cyTableFactory = getService(context, CyTableFactory.class);
        CyNetworkTableManager CyNetworkTableManager = getService(context, CyNetworkTableManager.class);
        LoadVizmapFileTaskFactory loadVizmapFileTaskFactory = getService(context, LoadVizmapFileTaskFactory.class);
        VisualMappingManager visualMappingManager = getService(context, VisualMappingManager.class);
        CyNetworkViewFactory cyNetworkViewFactory = getService(context, CyNetworkViewFactory.class);
        CyNetworkViewManager cyNetworkViewManager = getService(context, CyNetworkViewManager.class);
        CyLayoutAlgorithmManager cyLayoutAlgorithmManager = getService(context, CyLayoutAlgorithmManager.class);
        TaskManager taskManager = getService(context, TaskManager.class);
        UndoSupport undoSupport = getService(context, UndoSupport.class);
        OpenBrowser openBrowser = getService(context, OpenBrowser.class);

        //create the cyServices, keeps references to all cytoscape core model classes
        CytoscapeServices cyServices = new CytoscapeServices();
        cyServices.setCyApplicationManager(cyApplicationManager);
        cyServices.setCyNetworkFactory(cyNetworkFactory);
        cyServices.setCyNetworkManager(cyNetworkManager);
        cyServices.setCyRootNetworkManager(cyRootNetworkManager);
        cyServices.setCyTableFactory(cyTableFactory);
        cyServices.setCyNetworkTableManager(CyNetworkTableManager);
        cyServices.setLoadVizmapFileTaskFactory(loadVizmapFileTaskFactory);
        cyServices.setVisualMappingManager(visualMappingManager);
        cyServices.setCyNetworkViewFactory(cyNetworkViewFactory);
        cyServices.setCyNetworkViewManager(cyNetworkViewManager);
        cyServices.setCyLayoutAlgorithmManager(cyLayoutAlgorithmManager);
        cyServices.setTaskManager(taskManager);
        cyServices.setUndoSupport(undoSupport);
        cyServices.setOpenBrowser(openBrowser);

        //create the cyModel, keeps track of application state
        CENVApplication cyAppManager = new CENVApplication(cyServices);

        // Listen to Cytoscape events
        registerService(context, new NetworkEventListener(cyAppManager), NetworkAboutToBeDestroyedListener.class, new Properties());

        // Add our menu action in OSGi services
        registerAllServices(context, new MenuAction(cyApplicationManager, CENVModel.APP_NAME, cyAppManager), new Properties());

        // Add our layout
        FamLayout cgal = new FamLayout(undoSupport);
        Properties cgalProperties = new Properties();
        cgalProperties.setProperty(PREFERRED_MENU, "Apps." + CENVModel.APP_NAME);
        cgalProperties.setProperty("preferredTaskManager", "menu");
        cgalProperties.setProperty(TITLE, cgal.toString());
        registerService(context, cgal, CyLayoutAlgorithm.class, cgalProperties);

        //register contex menu action
        CyNodeViewContextMenuFactory myNodeViewContextMenuFactory = new CENVNodeViewContextMenuFactory(cyAppManager);
        Properties myNodeViewContextMenuFactoryProps = new Properties();
        myNodeViewContextMenuFactoryProps.put("preferredMenu", "Apps");
        registerAllServices(context, myNodeViewContextMenuFactory, myNodeViewContextMenuFactoryProps);

        //for debugging: print message if the app started succesfully
        System.out.println(CENVModel.APP_NAME + " started succesfully");
    }
}
