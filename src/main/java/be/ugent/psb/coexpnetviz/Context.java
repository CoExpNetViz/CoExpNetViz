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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskMonitor.Level;
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
	public static final String NAMESPACE = "coexpnetviz";
	public final String APP_VERSION;
	
	// The OSGi services we consume
	private CyNetworkManager networkManager;
	private CyNetworkFactory networkFactory;
	private CyNetworkViewManager networkViewManager;
	private CyNetworkViewFactory networkViewFactory;
	private PropsReader propsReader;
	private CySessionManager sessionManager;
	private UndoSupport undoSupport;
	private VisualStyleFactory visualStyleFactory;
	private VisualMappingManager visualMappingManager;
	private VisualMappingFunctionFactory continuousMappingFactory;
	private VisualMappingFunctionFactory discreteMappingFactory;
	private VisualMappingFunctionFactory passthroughMappingFactory;
	
	private LayoutAlgorithm layoutAlgorithm;
	
	private ObjectMapper jsonMapper;
	private boolean condaUpToDate = false;

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
		
		this.propsReader = new PropsReader();
		layoutAlgorithm = new LayoutAlgorithm(undoSupport);
		
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
	
	public PropsReader getPropsReader() {
		return propsReader;
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
	
	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	public boolean isCondaUpToDate() {
		return condaUpToDate;
	}
	
	public void setCondaUpToDate() {
		condaUpToDate = true;
	}
	
	/*
	 * Path to conda executable, can be relative in which case it will be searched for
	 * on the system PATH
	 */
	public Path getCondaPath() throws UserException {
		String condaPathString = getPropsReader().getProperties().getProperty(NAMESPACE + ".condaPath");
		Path condaPath = Paths.get(condaPathString);
		
		// Windows uses conda.bat, linux uses conda; added some more to be on the safe side.
		String[] extensions = new String[] {"", "bat", "exe", "sh"};
		
		if (!condaPath.isAbsolute()) {
			// Find conda on the system path so we can make sure it exists, so we can show a friendly
			// error message later if we do not find conda anywhere before trying to run it.
			for (String extension : extensions) {
				condaPath = findExecutableOnSystemPath(condaPathString, extension);
				if (condaPath != null) {
					return condaPath;
				}
			}
			
			// Try the default conda install locations if the user did not set a different value for the
			// condaPath property yet
			boolean isDefault = condaPathString == "conda";
			if (isDefault) {
				Path homeDir = SystemUtils.getUserHome().toPath();
				String[] condaDirNames = new String[] {"Anaconda3", "Miniconda3"};
				for (String condaDirName : condaDirNames) {
					if (!SystemUtils.IS_OS_WINDOWS) {
						condaDirName = condaDirName.toLowerCase();
					}
					Path condaBin = homeDir.resolve(condaDirName).resolve("condabin");
					
					for (String extension : extensions) {
						if (extension != "") {
							extension = "." + extension;
						}
						condaPath = condaBin.resolve("conda" + extension);
						if (Files.isExecutable(condaPath)) {
							return condaPath;
						}
					}
				}
			}
		}
		
		if (condaPath == null || !Files.isExecutable(condaPath)) {
			// The lack of exec permission is so unlikely that I lump it together in the same error message 
			throw new UserException(
				"Could not find conda (or, unlikely, you do not have permission to execute it). " +
				"Please set the coexpnetviz.condaPath property to the path of your conda " +
				"executable. The property can be found in the Cytoscape menu: Edit > Preferences > Properties; " +
				"select coexpnetviz in the dropdown box. To find the path on Windows, search for " +
				"'Anaconda prompt' in the start menu; in the prompt, type 'where conda' (without quotes); " +
				"probably either of those paths will do. To find the path on linux/mac you could try " +
				"running 'which conda' or `whereis conda` in a terminal."
			);
		}
		
		return condaPath;
	}

	private Path findExecutableOnSystemPath(String executable, String extension) {
		if (extension == "") {
			return findExecutableOnSystemPath(executable);
		}
		
		// Try with a different extension
		if (FilenameUtils.isExtension(executable, extension)) {
			return null;
		}
		
		String basePath = FilenameUtils.removeExtension(executable);
		return findExecutableOnSystemPath(basePath + "." + extension);
	}
	
	public Path findExecutableOnSystemPath(String executable) {
		for (String dir : System.getenv("PATH").split(File.pathSeparator)) {
	    	Path path = Paths.get(dir, executable);
	        if (Files.isExecutable(path)) {
	        	return path.toAbsolutePath();
	        }
	    }
	    
	    // Did not find it
		return null;
	}
	
	public static void showTaskMessage(TaskMonitor monitor, Level level, String msg) {
		/* TODO discern between GUI/CLI. CLI only understands <br>, GUI only understands \n.
		 * Or report upstream, maybe they should fix it. Then again, showMessage(INFO) seems
		 * to work fine with either \n or <br> in CLI. Maybe it only happens when it displays
		 * an uncaught task exception?
		 */
		monitor.showMessage(level, msg.replaceAll("\n", "<br>\n"));
	}
	
}
