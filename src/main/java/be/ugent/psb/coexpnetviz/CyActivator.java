/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2021 PSB/UGent
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

package be.ugent.psb.coexpnetviz;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

/**
 * Activates CENV plugin in Cytoscape.
 */
public class CyActivator extends AbstractCyActivator {

	private Context context;
	
	/**
	 * Entry point for the CoExpNetViz plugin for Cytoscape 3. Integrates our
	 * classes with those of Cytoscape, by adding Cytoscape menu items, attaching
	 * listeners to Cytoscape events, ...
	 */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
    	context = new Context(
			getService(bundleContext, CyNetworkManager.class),
			getService(bundleContext, CyNetworkFactory.class),
			getService(bundleContext, CyNetworkViewManager.class),
			getService(bundleContext, CyNetworkViewFactory.class),
			getService(bundleContext, CySessionManager.class),
			getService(bundleContext, UndoSupport.class),
			getService(bundleContext, VisualStyleFactory.class),
			getService(bundleContext, VisualMappingManager.class),
			getService(bundleContext, VisualMappingFunctionFactory.class, "(mapping.type=continuous)"),
			getService(bundleContext, VisualMappingFunctionFactory.class, "(mapping.type=discrete)"),
			getService(bundleContext, VisualMappingFunctionFactory.class, "(mapping.type=passthrough)"),
			bundleContext.getBundle().getVersion()
    	);
    	
    	registerCreateNetwork(bundleContext);
        registerLayoutAlgorithm(bundleContext);
    }

	private void registerCreateNetwork(BundleContext bundleContext) {
		// Add a menu item for the create-network task
		Properties props = new Properties();
		props.setProperty(PREFERRED_MENU, "Apps");
		props.setProperty(TITLE, "Create co-expression network");
		
		// and a command
		props.setProperty(COMMAND_NAMESPACE, Context.NAMESPACE);
		props.setProperty(COMMAND, "create_network");
		props.setProperty(COMMAND_DESCRIPTION, "Create a co-expression network");
		
		// finally, actually register it
		registerService(bundleContext, new CreateNetworkTaskFactory(context), TaskFactory.class, props);
	}
	
	private void registerLayoutAlgorithm(BundleContext bundleContext) {
		LayoutAlgorithm layoutAlgorithm = new LayoutAlgorithm(context.getUndoSupport());
		context.setLayoutAlgorithm(layoutAlgorithm);
		
		// Add menu item
		Properties props = new Properties();
		props.setProperty(PREFERRED_MENU, Context.APP_MENU);
		props.setProperty(TITLE, Context.APP_NAME);
		
		// and a command
		props.setProperty(COMMAND_NAMESPACE, Context.NAMESPACE);
		props.setProperty(COMMAND, "apply_layout");
		props.setProperty(COMMAND_DESCRIPTION, "Apply CoExpNetViz layout to a CoExpNetViz network");
		
		registerService(bundleContext, layoutAlgorithm, CyLayoutAlgorithm.class, props);
	}
}
