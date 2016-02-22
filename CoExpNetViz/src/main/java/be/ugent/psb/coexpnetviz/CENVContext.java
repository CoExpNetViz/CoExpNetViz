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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.task.edit.ImportDataTableTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;

/**
 * CoExpNetViz app context, provides refereences to what would otherwise be globals.
 */
public class CENVContext {

	public static final String APP_NAME = "CoExpNetViz";
	
	private Configuration configuration; 
    
	// The OSGi services we consume
	private UndoSupport undoSupport;
	private TaskManager<?,?> taskManager;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewManager cyNetworkViewManager;
	private VisualMappingManager visualMappingManager;
	private LoadVizmapFileTaskFactory loadVizmapFileTaskFactory;
	private CyTableReaderManager cyTableReaderManager;
	private CyRootNetworkManager cyRootNetworkManager;
	private ImportDataTableTaskFactory importDataTableTaskFactory;
	private CyNetworkReaderManager cyNetworkReaderManager;
	private CyLayoutAlgorithmManager cyLayoutAlgorithmManager;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkTableManager cyNetworkTableManager;
	private OpenBrowser openBrowser;
	private CyApplicationManager cyApplicationManager;
	private CyApplicationConfiguration cyApplicationConfiguration;
	private CySwingApplication cySwingApplication;

	public CENVContext(UndoSupport undoSupport, TaskManager<?,?> taskManager, CyNetworkManager cyNetworkManager,
			CyNetworkViewManager cyNetworkViewManager, VisualMappingManager visualMappingManager,
			LoadVizmapFileTaskFactory loadVizmapFileTaskFactory, CyTableReaderManager cyTableReaderManager,
			CyRootNetworkManager cyRootNetworkManager, ImportDataTableTaskFactory importDataTableTaskFactory,
			CyNetworkReaderManager cyNetworkReaderManager, CyLayoutAlgorithmManager cyLayoutAlgorithmManager,
			CyNetworkViewFactory cyNetworkViewFactory, CyNetworkTableManager cyNetworkTableManager,
			OpenBrowser openBrowser, CyApplicationManager cyApplicationManager,
			CyApplicationConfiguration cyApplicationConfiguration, CySwingApplication cySwingApplication) {
		this.undoSupport = undoSupport;
		this.taskManager = taskManager;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.visualMappingManager = visualMappingManager;
		this.loadVizmapFileTaskFactory = loadVizmapFileTaskFactory;
		this.cyTableReaderManager = cyTableReaderManager;
		this.cyRootNetworkManager = cyRootNetworkManager;
		this.importDataTableTaskFactory = importDataTableTaskFactory;
		this.cyNetworkReaderManager = cyNetworkReaderManager;
		this.cyLayoutAlgorithmManager = cyLayoutAlgorithmManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkTableManager = cyNetworkTableManager;
		this.openBrowser = openBrowser;
		this.cyApplicationManager = cyApplicationManager;
		this.cyApplicationConfiguration = cyApplicationConfiguration;
		this.cySwingApplication = cySwingApplication;
		
		loadConfiguration();
	}

	/**
     * Get a formatted current time string
     */
    public static String getTimeStamp() { // TODO throw into an Util class
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd-hh:mm:ss");
        return sdf.format(now);
    }

    public Configuration getConfiguration() {
		return configuration;
	}

	private Path getConfigurationFilePath() {
    	Path path = cyApplicationConfiguration.getAppConfigurationDirectoryLocation(getClass()).toPath();
    	path = path.getParent().resolve(APP_NAME); // Make version independent so that configuration is carried on to next versions. We do have to be careful to remain backwards compatible in config format due to this.
		return path.resolve("settings.xml");
    }
    
    private void loadConfiguration() {
		Path configurationFile = getConfigurationFilePath();
		if (Files.exists(configurationFile)) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
			    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			    configuration = (Configuration)unmarshaller.unmarshal(getConfigurationFilePath().toFile());
			}
			catch (JAXBException e) {
				System.err.println("Failed to load configuration");
				e.printStackTrace();
				try {
					
					Files.copy(configurationFile, configurationFile.getParent().resolve(configurationFile.getFileName() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					// Get a new hard drive, OS or JVM, please
					e1.printStackTrace();
				}
				configuration = new Configuration();
			}
		}
		else {
			configuration = new Configuration();
		}
    }

    public void saveConfiguration() {
    	Path configurationFile = getConfigurationFilePath();
		try {
			Files.createDirectories(configurationFile.getParent());
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
		    Marshaller marshaller = jaxbContext.createMarshaller();
		    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		    marshaller.marshal(configuration, getConfigurationFilePath().toFile());
		}
		catch (IOException e) {
			System.err.println("Failed to save configuration");
			e.printStackTrace();
		}
		catch (JAXBException e) {
			System.err.println("Failed to save configuration");
			e.printStackTrace();
		}
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

	public OpenBrowser getOpenBrowser() {
		return openBrowser;
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

}
