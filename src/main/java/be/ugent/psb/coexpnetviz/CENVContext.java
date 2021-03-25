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

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * CoExpNetViz app context, provides references to what would otherwise be globals.
 */
public class CENVContext {

	public static final String APP_NAME = "CoExpNetViz";
	public static final String APP_MENU = "Apps." + APP_NAME;
	public static final String NAMESPACE = "coexpnetviz";
	
	// The OSGi services we consume
	private UndoSupport undoSupport;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewManager cyNetworkViewManager;
	private VisualMappingManager visualMappingManager;
	private VisualStyleFactory visualStyleFactory;
	private VisualMappingFunctionFactory continuousMappingFactory;
	private VisualMappingFunctionFactory discreteMappingFactory;
	private VisualMappingFunctionFactory passthroughMappingFactory;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkFactory cyNetworkFactory;
	private CySessionManager cySessionManager;
	
	private LayoutAlgorithm layoutAlgorithm;
	
	private ObjectMapper jsonMapper;

	public CENVContext(UndoSupport undoSupport, CyNetworkManager cyNetworkManager,
			CyNetworkViewManager cyNetworkViewManager, VisualMappingManager visualMappingManager,
			VisualStyleFactory visualStyleFactory, VisualMappingFunctionFactory continuousMappingFactory,
			VisualMappingFunctionFactory discreteMappingFactory, VisualMappingFunctionFactory passthroughMappingFactory,
			CyNetworkViewFactory cyNetworkViewFactory, CyNetworkFactory cyNetworkFactory,
			CySessionManager cySessionManager) {
		super();
		this.undoSupport = undoSupport;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.visualMappingManager = visualMappingManager;
		this.visualStyleFactory = visualStyleFactory;
		this.continuousMappingFactory = continuousMappingFactory;
		this.discreteMappingFactory = discreteMappingFactory;
		this.passthroughMappingFactory = passthroughMappingFactory;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cySessionManager = cySessionManager;
		
		jsonMapper = JsonMapper
				.builder()
				// Parse NaN, Inf, ... as floats
				.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
				.build();
	}

	public UndoSupport getUndoSupport() {
		return undoSupport;
	}

	public CyNetworkManager getCyNetworkManager() {
		return cyNetworkManager;
	}

	public CyNetworkViewManager getCyNetworkViewManager() {
		return cyNetworkViewManager;
	}

	public VisualMappingManager getVisualMappingManager() {
		return visualMappingManager;
	}
	
	public CyNetworkViewFactory getCyNetworkViewFactory() {
		return cyNetworkViewFactory;
	}

	public CyNetworkFactory getCyNetworkFactory() {
		return cyNetworkFactory;
	}
	
	public VisualStyleFactory getVisualStyleFactory() {
		return visualStyleFactory;
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
	
	public CySessionManager getCySessionManager() {
		return cySessionManager;
	}
	
}
