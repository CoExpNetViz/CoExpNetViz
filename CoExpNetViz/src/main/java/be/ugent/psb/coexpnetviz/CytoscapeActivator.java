package be.ugent.psb.coexpnetviz;

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

import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.edit.ImportDataTableTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import be.ugent.psb.coexpnetviz.gui.MenuAction;
import be.ugent.psb.coexpnetviz.layout.FamLayout;

// With declarative services we wouldn't need glue code like this http://www.eclipsezone.com/eclipse/forums/t97690.rhtml
/**
 * Activates CENV plugin in Cytoscape.
 */
public class CytoscapeActivator extends AbstractCyActivator {

	private Context context;
	
	/**
	 * Entry point for the CoExpNetViz plugin for Cytoscape 3. Integrates our
	 * classes with those of Cytoscape, by adding Cytoscape menu items, attaching
	 * listeners to Cytoscape events, ...
	 */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
    	try{
	    	context = new Context(
				getService(bundleContext, UndoSupport.class),
				getService(bundleContext, TaskManager.class),
				getService(bundleContext, CyNetworkManager.class),
				getService(bundleContext, CyNetworkViewManager.class),
				getService(bundleContext, VisualMappingManager.class),
				getService(bundleContext, LoadVizmapFileTaskFactory.class),
				getService(bundleContext, CyTableReaderManager.class),
				getService(bundleContext, CyRootNetworkManager.class),
				getService(bundleContext, ImportDataTableTaskFactory.class),
				getService(bundleContext, CyNetworkReaderManager.class),
				getService(bundleContext, CyLayoutAlgorithmManager.class),
				getService(bundleContext, CyNetworkViewFactory.class),
				getService(bundleContext, CyNetworkTableManager.class),
				getService(bundleContext, OpenBrowser.class),
				getService(bundleContext, CyApplicationManager.class),
				getService(bundleContext, CyApplicationConfiguration.class),
				getService(bundleContext, CySwingApplication.class)		
	    	);
	
	        // Add our menu action in OSGi services
	        registerAllServices(bundleContext, new MenuAction(context), new Properties());
	
	        // Add our layout
	        FamLayout cgal = new FamLayout(context.getUndoSupport());
	        Properties cgalProperties = new Properties();
	        cgalProperties.setProperty(PREFERRED_MENU, "Apps." + Context.APP_NAME);
	        cgalProperties.setProperty("preferredTaskManager", "menu");
	        cgalProperties.setProperty(TITLE, cgal.toString());
	        registerService(bundleContext, cgal, CyLayoutAlgorithm.class, cgalProperties);
	
	        //register contex menu action
	        // TODO fix problems with CENVNodeViewContextMenuFactory, then reenable
	        /*CyNodeViewContextMenuFactory myNodeViewContextMenuFactory = new CENVNodeViewContextMenuFactory(application);
	        Properties myNodeViewContextMenuFactoryProps = new Properties();
	        myNodeViewContextMenuFactoryProps.put("preferredMenu", "Apps");
	        registerAllServices(context, myNodeViewContextMenuFactory, myNodeViewContextMenuFactoryProps);*/
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    }
    
    @Override
    public void shutDown() {
    	context.saveConfiguration();
    	context = null;
    	
    	super.shutDown();
    }
}
