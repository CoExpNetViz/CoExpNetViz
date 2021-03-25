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

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.Version;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * CoExpNetViz app context, provides references to what would otherwise be globals.
 */
public class Context {

	public static final String APP_NAME = "CoExpNetViz";
	public static final String APP_MENU = "Apps." + APP_NAME;
	public static final String NAMESPACE = "coexpnetviz";
	public final String APP_VERSION;
	
	// The OSGi services we consume
	private CyNetworkManager networkManager;
	private CyNetworkFactory networkFactory;
	private CyNetworkViewManager networkViewManager;
	private CyNetworkViewFactory networkViewFactory;
	private CySessionManager sessionManager;
	private UndoSupport undoSupport;
	private VisualStyleFactory visualStyleFactory;
	private VisualMappingManager visualMappingManager;
	private VisualMappingFunctionFactory continuousMappingFactory;
	private VisualMappingFunctionFactory discreteMappingFactory;
	private VisualMappingFunctionFactory passthroughMappingFactory;
	
	private LayoutAlgorithm layoutAlgorithm;
	
	private ObjectMapper jsonMapper;

	public Context(CyNetworkManager networkManager, CyNetworkFactory networkFactory,
			CyNetworkViewManager networkViewManager, CyNetworkViewFactory networkViewFactory,
			CySessionManager sessionManager, UndoSupport undoSupport, VisualStyleFactory visualStyleFactory,
			VisualMappingManager visualMappingManager, VisualMappingFunctionFactory continuousMappingFactory,
			VisualMappingFunctionFactory discreteMappingFactory,
			VisualMappingFunctionFactory passthroughMappingFactory, Version version) {
		super();
		this.networkManager = networkManager;
		this.networkFactory = networkFactory;
		this.networkViewManager = networkViewManager;
		this.networkViewFactory = networkViewFactory;
		this.sessionManager = sessionManager;
		this.undoSupport = undoSupport;
		this.visualStyleFactory = visualStyleFactory;
		this.visualMappingManager = visualMappingManager;
		this.continuousMappingFactory = continuousMappingFactory;
		this.discreteMappingFactory = discreteMappingFactory;
		this.passthroughMappingFactory = passthroughMappingFactory;
		APP_VERSION = version.toString();
		
		jsonMapper = JsonMapper
				.builder()
				// Parse NaN, Inf, ... as floats
				.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
				.build();
	}

	public CyNetworkManager getNetworkManager() {
		return networkManager;
	}

	public CyNetworkFactory getNetworkFactory() {
		return networkFactory;
	}

	public CyNetworkViewManager getNetworkViewManager() {
		return networkViewManager;
	}

	public CyNetworkViewFactory getNetworkViewFactory() {
		return networkViewFactory;
	}

	public CySessionManager getSessionManager() {
		return sessionManager;
	}

	public UndoSupport getUndoSupport() {
		return undoSupport;
	}

	public VisualStyleFactory getVisualStyleFactory() {
		return visualStyleFactory;
	}

	public VisualMappingManager getVisualMappingManager() {
		return visualMappingManager;
	}

	public VisualMappingFunctionFactory getContinuousMappingFactory() {
		return continuousMappingFactory;
	}

	public VisualMappingFunctionFactory getDiscreteMappingFactory() {
		return discreteMappingFactory;
	}

	public VisualMappingFunctionFactory getPassthroughMappingFactory() {
		return passthroughMappingFactory;
	}
	
	public LayoutAlgorithm getLayoutAlgorithm() {
		return layoutAlgorithm;
	}
	
	public void setLayoutAlgorithm(LayoutAlgorithm layoutAlgorithm) {
		this.layoutAlgorithm = layoutAlgorithm;
	}

	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}
	
}
