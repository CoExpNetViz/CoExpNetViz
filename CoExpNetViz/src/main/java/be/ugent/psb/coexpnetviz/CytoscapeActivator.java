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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.edit.ImportDataTableTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.events.VisualStyleSetListener;
import org.cytoscape.work.SynchronousTaskManager;
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
    	CENVContext application = new CENVContext();
        
    	try {
    		System.out.println(context.getBundle().loadClass("org.apache.pivot.wtk.Window")); // <-- this works. Pivot should simply use this!!
    	} catch (ClassNotFoundException e) {
			System.out.println("Nope nope nope 1");
		}
    	
    	// Get CytoScape services
        application.setCyApplicationManager(getService(context, CyApplicationManager.class));
        application.setCyNetworkReaderManager(getService(context, CyNetworkReaderManager.class));
        application.setCyNetworkFactory(getService(context, CyNetworkFactory.class));
        application.setCyNetworkManager(getService(context, CyNetworkManager.class));
        application.setCyRootNetworkManager(getService(context, CyRootNetworkManager.class));
        application.setCyTableFactory(getService(context, CyTableFactory.class));
        application.setCyTableReaderManager(getService(context, CyTableReaderManager.class));
        application.setImportDataTableTaskFactory(getService(context, ImportDataTableTaskFactory.class));
        application.setCyNetworkTableManager(getService(context, CyNetworkTableManager.class));
        application.setLoadVizmapFileTaskFactory(getService(context, LoadVizmapFileTaskFactory.class));
        application.setVisualMappingManager(getService(context, VisualMappingManager.class));
        application.setCyNetworkViewFactory(getService(context, CyNetworkViewFactory.class));
        application.setCyNetworkViewManager(getService(context, CyNetworkViewManager.class));
        application.setCyLayoutAlgorithmManager(getService(context, CyLayoutAlgorithmManager.class));
        application.setSynchronousTaskManager(getService(context, SynchronousTaskManager.class));
        application.setTaskManager(getService(context, TaskManager.class));
        application.setUndoSupport(getService(context, UndoSupport.class));
        application.setOpenBrowser(getService(context, OpenBrowser.class));

        // Listen to Cytoscape events
        registerService(context, new NetworkEventListener(application), NetworkAboutToBeDestroyedListener.class, new Properties());

        // Add our menu action in OSGi services
        registerAllServices(context, new MenuAction(application.getCyApplicationManager(), CENVContext.APP_NAME, application), new Properties());

        // Add our layout
        FamLayout cgal = new FamLayout(application.getUndoSupport());
        Properties cgalProperties = new Properties();
        cgalProperties.setProperty(PREFERRED_MENU, "Apps." + CENVContext.APP_NAME);
        cgalProperties.setProperty("preferredTaskManager", "menu");
        cgalProperties.setProperty(TITLE, cgal.toString());
        registerService(context, cgal, CyLayoutAlgorithm.class, cgalProperties);

        //register contex menu action
        // TODO fix problems with CENVNodeViewContextMenuFactory, then reenable
        /*CyNodeViewContextMenuFactory myNodeViewContextMenuFactory = new CENVNodeViewContextMenuFactory(application);
        Properties myNodeViewContextMenuFactoryProps = new Properties();
        myNodeViewContextMenuFactoryProps.put("preferredMenu", "Apps");
        registerAllServices(context, myNodeViewContextMenuFactory, myNodeViewContextMenuFactoryProps);*/

        //for debugging: print message if the app started succesfully
        System.out.println(CENVContext.APP_NAME + " started succesfully");
    }
}
