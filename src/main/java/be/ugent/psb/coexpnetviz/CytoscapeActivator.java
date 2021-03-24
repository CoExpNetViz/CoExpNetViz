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
import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;

import java.util.Properties;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.edit.ImportDataTableTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.psb.coexpnetviz.gui.NodeViewContextMenuFactory;
import be.ugent.psb.coexpnetviz.layout.CENVLayoutAlgorithm;

// With declarative services we wouldn't need glue code like this http://www.eclipsezone.com/eclipse/forums/t97690.rhtml
/**
 * Activates CENV plugin in Cytoscape.
 */
public class CytoscapeActivator extends AbstractCyActivator {

	private CENVContext context;
	
	private static final String APP_MENU = "Apps." + CENVContext.APP_NAME;
	private static final String CMD_NAMESPACE = "coexpnetviz";
	
	/**
	 * Entry point for the CoExpNetViz plugin for Cytoscape 3. Integrates our
	 * classes with those of Cytoscape, by adding Cytoscape menu items, attaching
	 * listeners to Cytoscape events, ...
	 */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
    	try {
	    	context = new CENVContext(
				getService(bundleContext, UndoSupport.class),
				getService(bundleContext, TaskManager.class),
				getService(bundleContext, CyNetworkManager.class),
				getService(bundleContext, CyNetworkViewManager.class),
				getService(bundleContext, VisualMappingManager.class),
				getService(bundleContext, VisualStyleFactory.class),
				getService(bundleContext, VisualMappingFunctionFactory.class, "(mapping.type=continuous)"),
				getService(bundleContext, VisualMappingFunctionFactory.class, "(mapping.type=discrete)"),
				getService(bundleContext, VisualMappingFunctionFactory.class, "(mapping.type=passthrough)"),
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
				getService(bundleContext, CySwingApplication.class),
				getService(bundleContext, CyNetworkFactory.class),
				getService(bundleContext, CySessionManager.class)
	    	);
	    	
	    	registerCreateNetwork(bundleContext);
	        
	        // Register our layout algorithm, also add it to the menu
	        CENVLayoutAlgorithm cgal = new CENVLayoutAlgorithm(context.getUndoSupport());
	        context.setLayoutAlgorithm(cgal);
	        Properties cgalProperties = new Properties();
	        cgalProperties.setProperty(PREFERRED_MENU, APP_MENU);
	        cgalProperties.setProperty("preferredTaskManager", "menu");
	        cgalProperties.setProperty(TITLE, cgal.toString());
	        registerService(bundleContext, cgal, CyLayoutAlgorithm.class, cgalProperties);
	
	        // Add node context menu action
	        CyNodeViewContextMenuFactory myNodeViewContextMenuFactory = new NodeViewContextMenuFactory(context);
	        Properties myNodeViewContextMenuFactoryProps = new Properties();
	        myNodeViewContextMenuFactoryProps.setProperty(PREFERRED_MENU, APP_MENU);
	        registerService(bundleContext, myNodeViewContextMenuFactory, CyNodeViewContextMenuFactory.class, myNodeViewContextMenuFactoryProps);
    	}
    	catch (Throwable e) {
    		e.printStackTrace();
    		throw e;
    	}
    }

	private void registerCreateNetwork(BundleContext bundleContext) {
		// Add a menu item for the create-network task
		Properties props = new Properties();
		props.setProperty(PREFERRED_MENU, APP_MENU);
		props.setProperty(TITLE, "Create co-expression network");
		
		// and a command
		props.setProperty(COMMAND_NAMESPACE, CMD_NAMESPACE);
		props.setProperty(COMMAND, "create_network");
		props.setProperty(COMMAND_DESCRIPTION, "Create a co-expression network");
		
		// finally, actually register it
		registerService(bundleContext, new CENVTaskFactory(context), TaskFactory.class, props);
	}
}
