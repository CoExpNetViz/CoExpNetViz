package be.ugent.psb.coexpnetviz;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.task.edit.ImportDataTableTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import be.ugent.psb.coexpnetviz.layout.CENVLayoutAlgorithm;

/**
 * CoExpNetViz app context, provides references to what would otherwise be globals.
 */
public class CENVContext {

	public static final String APP_NAME = "CoExpNetViz";
	
	// Table column namespace
	public static final String NAMESPACE = "coexpnetviz";
	
	// The OSGi services we consume
	private UndoSupport undoSupport;
	private TaskManager<?,?> taskManager;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewManager cyNetworkViewManager;
	private VisualMappingManager visualMappingManager;
	private VisualStyleFactory visualStyleFactory;
	private VisualMappingFunctionFactory continuousMappingFactory;
	private VisualMappingFunctionFactory discreteMappingFactory;
	private VisualMappingFunctionFactory passthroughMappingFactory;
	private LoadVizmapFileTaskFactory loadVizmapFileTaskFactory;
	private CyTableReaderManager cyTableReaderManager;
	private CyRootNetworkManager cyRootNetworkManager;
	private ImportDataTableTaskFactory importDataTableTaskFactory;
	private CyNetworkReaderManager cyNetworkReaderManager;
	private CyLayoutAlgorithmManager cyLayoutAlgorithmManager;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkTableManager cyNetworkTableManager;
	private CyApplicationManager cyApplicationManager;
	private CyApplicationConfiguration cyApplicationConfiguration;
	private CySwingApplication cySwingApplication;
	private CyNetworkFactory cyNetworkFactory;
	private CySessionManager cySessionManager;
	
	private CENVLayoutAlgorithm layoutAlgorithm;
	
	private ObjectMapper jsonMapper;

	public CENVContext(UndoSupport undoSupport, TaskManager<?, ?> taskManager, CyNetworkManager cyNetworkManager,
			CyNetworkViewManager cyNetworkViewManager, VisualMappingManager visualMappingManager,
			VisualStyleFactory visualStyleFactory, VisualMappingFunctionFactory continuousMappingFactory,
			VisualMappingFunctionFactory discreteMappingFactory, VisualMappingFunctionFactory passthroughMappingFactory,
			LoadVizmapFileTaskFactory loadVizmapFileTaskFactory, CyTableReaderManager cyTableReaderManager,
			CyRootNetworkManager cyRootNetworkManager, ImportDataTableTaskFactory importDataTableTaskFactory,
			CyNetworkReaderManager cyNetworkReaderManager, CyLayoutAlgorithmManager cyLayoutAlgorithmManager,
			CyNetworkViewFactory cyNetworkViewFactory, CyNetworkTableManager cyNetworkTableManager,
			CyApplicationManager cyApplicationManager,
			CyApplicationConfiguration cyApplicationConfiguration, CySwingApplication cySwingApplication,
			CyNetworkFactory cyNetworkFactory, CySessionManager cySessionManager) {
		super();
		this.undoSupport = undoSupport;
		this.taskManager = taskManager;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.visualMappingManager = visualMappingManager;
		this.visualStyleFactory = visualStyleFactory;
		this.continuousMappingFactory = continuousMappingFactory;
		this.discreteMappingFactory = discreteMappingFactory;
		this.passthroughMappingFactory = passthroughMappingFactory;
		this.loadVizmapFileTaskFactory = loadVizmapFileTaskFactory;
		this.cyTableReaderManager = cyTableReaderManager;
		this.cyRootNetworkManager = cyRootNetworkManager;
		this.importDataTableTaskFactory = importDataTableTaskFactory;
		this.cyNetworkReaderManager = cyNetworkReaderManager;
		this.cyLayoutAlgorithmManager = cyLayoutAlgorithmManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkTableManager = cyNetworkTableManager;
		this.cyApplicationManager = cyApplicationManager;
		this.cyApplicationConfiguration = cyApplicationConfiguration;
		this.cySwingApplication = cySwingApplication;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cySessionManager = cySessionManager;
		
		jsonMapper = JsonMapper
				.builder()
				// Parse NaN, Inf, ... as floats
				.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
				.build();
	}

	/**
     * Get a formatted current time string
     */
    public static String getTimeStamp() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd-hh:mm:ss");
        return sdf.format(now);
    }

	public UndoSupport getUndoSupport() {
		return undoSupport;
	}

	public TaskManager<?,?> getTaskManager() {
		return taskManager;
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

	public LoadVizmapFileTaskFactory getLoadVizmapFileTaskFactory() {
		return loadVizmapFileTaskFactory;
	}

	public CyTableReaderManager getCyTableReaderManager() {
		return cyTableReaderManager;
	}

	public CyRootNetworkManager getCyRootNetworkManager() {
		return cyRootNetworkManager;
	}

	public ImportDataTableTaskFactory getImportDataTableTaskFactory() {
		return importDataTableTaskFactory;
	}

	public CyNetworkReaderManager getCyNetworkReaderManager() {
		return cyNetworkReaderManager;
	}

	public CyLayoutAlgorithmManager getCyLayoutAlgorithmManager() {
		return cyLayoutAlgorithmManager;
	}

	public CyNetworkViewFactory getCyNetworkViewFactory() {
		return cyNetworkViewFactory;
	}

	public CyNetworkTableManager getCyNetworkTableManager() {
		return cyNetworkTableManager;
	}

	public CyApplicationManager getCyApplicationManager() {
		return cyApplicationManager;
	}

	public CyApplicationConfiguration getCyApplicationConfiguration() {
		return cyApplicationConfiguration;
	}

	public CySwingApplication getCySwingApplication() {
		return cySwingApplication;
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
	
	public CENVLayoutAlgorithm getLayoutAlgorithm() {
		return layoutAlgorithm;
	}
	
	public void setLayoutAlgorithm(CENVLayoutAlgorithm layoutAlgorithm) {
		this.layoutAlgorithm = layoutAlgorithm;
	}
	
	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}
	
	public CySessionManager getCySessionManager() {
		return cySessionManager;
	}
	
}
