package be.samey.internal;

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
import be.samey.cynetw.CevNodeViewContextMenuFactory;
import be.samey.layout.FamLayout;
import be.samey.cynetw.NetworkEventListener;
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
 * This is the entry point for the CoExpNetViz plugin for Cytoscape 3. Here the
 * necessary services are called and bundled into the {@link Model} to be used
 * throughout the app. The services this app offers are created here and
 * registered within the {@link BundleContext}.
 *
 * @author Sam De Meyer
 */
public class CyActivator extends AbstractCyActivator {

    @Override
    public void start(BundleContext context) throws Exception {

        //TODO: I recently discovered the existence of the CySwingAdapter class
        //      this is a Cytoscape api class that does exactly what is done
        //      below: wrapping instances of all Cytoscape model classes into
        //      one object that can easily be passed around.
        //
        //      Conclusion: CyServices below can be replaced by CySwingAdapter,
        //      this will clear a lot of code
        //get CytoScape services
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
        CyServices cyServices = new CyServices();

        //create the cyModel, keeps track of application state
        CyModel cyModel = new CyModel();

        //create the CyAppManager
        CyAppManager cyAppManager = new CyAppManager(cyModel, cyServices);

        //create the network event listener
        NetworkEventListener networkEventListener = new NetworkEventListener(cyAppManager);

        //create the menu action (menu entry for the app)
        MenuAction action = new MenuAction(cyApplicationManager, CyModel.APP_NAME, cyAppManager);

        //add the CytoScape service references to cyServices
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

        //register network event services
        registerService(context, networkEventListener, NetworkAboutToBeDestroyedListener.class, new Properties());
        registerService(context, networkEventListener, NetworkAddedListener.class, new Properties());
        registerService(context, networkEventListener, NetworkViewAddedListener.class, new Properties());
        registerService(context, networkEventListener, VisualStyleSetListener.class, new Properties());

        //register menu action in OSGi services
        registerAllServices(context, action, new Properties());

        //register cev layout service
        FamLayout cgal = new FamLayout(undoSupport);
        Properties cgalProperties = new Properties();
        cgalProperties.setProperty(PREFERRED_MENU, "Apps." + CyModel.APP_NAME);
        cgalProperties.setProperty("preferredTaskManager", "menu");
        cgalProperties.setProperty(TITLE, cgal.toString());
        registerService(context, cgal, CyLayoutAlgorithm.class, cgalProperties);

        //register contex menu action
        CyNodeViewContextMenuFactory myNodeViewContextMenuFactory = new CevNodeViewContextMenuFactory(cyAppManager);
        Properties myNodeViewContextMenuFactoryProps = new Properties();
        myNodeViewContextMenuFactoryProps.put("preferredMenu", "Apps");
        registerAllServices(context, myNodeViewContextMenuFactory, myNodeViewContextMenuFactoryProps);

        //for debugging: print message if the app started succesfully
        System.out.println(CyModel.APP_NAME + " started succesfully");
    }
}
