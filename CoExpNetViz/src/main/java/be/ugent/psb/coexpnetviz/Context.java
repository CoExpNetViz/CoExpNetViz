package be.ugent.psb.coexpnetviz;

import be.ugent.psb.coexpnetviz.gui.CENVModel;

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

import be.ugent.psb.coexpnetviz.gui.controller.JobInputFrameController;
import be.ugent.psb.coexpnetviz.io.JobServer;

import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.task.edit.ImportDataTableTaskFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;

/**
 * CoExpNetViz app context, provides refereences to what would otherwise be globals.
 */
public class Context {

	public static final String APP_NAME = "CoExpNetViz";
	
    private final CENVModel cyModel;

    private JobInputFrameController guiController;
    
    // Cytoscape 'services'. Note we don't use all of these
    private CyApplicationManager cyApplicationManager;
    private CyNetworkReaderManager cyNetworkReaderManager;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkManager cyNetworkManager;
    private CyRootNetworkManager cyRootNetworkManager;
    private CyTableFactory cyTableFactory;
    private CyNetworkTableManager cyNetworkTableManager;
    private LoadVizmapFileTaskFactory loadVizmapFileTaskFactory;
    private VisualMappingManager visualMappingManager;
    private CyNetworkViewFactory cyNetworkViewFactory;
    private CyNetworkViewManager cyNetworkViewManager;
    private CyLayoutAlgorithmManager cyLayoutAlgorithmManager;
    private ImportDataTableTaskFactory importDataTableTaskFactory;
    private SynchronousTaskManager synchronousTaskManager;
    private TaskManager taskManager;
    private UndoSupport undoSupport;
    private OpenBrowser openBrowser;
    private CyTableReaderManager cyTableReaderManager;

    public Context() {
        this.cyModel = new CENVModel();
        cyModel.setSettingsPath(initSettingsPath());
    }

    /**
     * Get a formatted current time string
     *
     * @return
     */
    public static String getTimeStamp() { // TODO throw into an Util class
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd-hh:mm:ss");
        return sdf.format(now);
    }

    /**
     * Get a reference to the Cytoscape desktop window.
     * 
     * Handy for use as a parent for dialog windows
     *
     * @return
     */
    public static Frame getCytoscapeRootFrame() {
        //TODO: cleaner to use CySwingApplication instead
        Frame[] frames = Frame.getFrames();
        Frame csFrame = null;
        for (Frame frame : frames) {
            String className = frame.getClass().toString();
            if (className.endsWith("CytoscapeDesktop")) {
                csFrame = frame;
            }
        }
        return csFrame;
    }

    private Path initSettingsPath() {
        Path cyHomePath;
        Path localSettingsPath;

        //attempt 1: try to find CytoscapeConfiguration folder in user.dir
        cyHomePath = Paths.get(System.getProperty("user.dir"));
        localSettingsPath = getCyConfFolder(cyHomePath);
        if (localSettingsPath != null) {
            return localSettingsPath;
        }

        //attempt 2: try to find CytoscapeConfiguration folder in user.home
        cyHomePath = Paths.get(System.getProperty("user.home"));
        localSettingsPath = getCyConfFolder(cyHomePath);
        if (localSettingsPath != null) {
            return localSettingsPath;
        }

        //attampt 3: Try to get a settings folder in the user home directory
        cyHomePath = Paths.get(System.getProperty("user.home"));
        localSettingsPath = getHomeSettingsFolder(cyHomePath);
        if (localSettingsPath != null) {
            return localSettingsPath;
        }

        //TODO: handle this better
        return null;
    }

    private Path getCyConfFolder(Path searchPath) {
        Path cyConfPath = searchPath.resolve("CytoscapeConfiguration");
        Path localSettingsPath;

        //try to get a settings directory in the cytoscape config folder
        if (Files.isDirectory(cyConfPath) && Files.isWritable(cyConfPath)) {
            localSettingsPath = cyConfPath.resolve(APP_NAME + "_settings");
            //settins folder doesn't exists, so try to make it
            if (!Files.exists(localSettingsPath)) {
                try {
                    Files.createDirectory(localSettingsPath);
                    return localSettingsPath;
                } catch (IOException ex) {
                    System.out.println(ex);
                    //TODO:warn user somehow
                }
            } else if (Files.isDirectory(localSettingsPath) && Files.isWritable(localSettingsPath)) {
                //settings folder exists, check if I can write there
                return localSettingsPath;
            }
        }
        return null;
    }

    private Path getHomeSettingsFolder(Path searchPath) {
        Path localSettingsPath = searchPath.resolve(APP_NAME + "_settings");
        if (!Files.exists(localSettingsPath)) {
            //settins folder doesn't exists, so try to make it
            try {
                Files.createDirectory(localSettingsPath);
                return localSettingsPath;
            } catch (IOException ex) {
                System.out.println(ex);
                //TODO:warn user somehow
            }
        } else if (Files.isDirectory(localSettingsPath) && Files.isWritable(localSettingsPath)) {
            //settings folder exists, check if I can write there
            return localSettingsPath;
        }
        return null;
    }

    /**
     * @return the cyModel
     */
    public CENVModel getCyModel() {
        return cyModel;
    }

    /**
     * @return the guiManager
     */
    public JobInputFrameController getGUIController() {
        return guiController;
    }

	public void showGUI() {
        if (guiController == null) {
            //create the Gui
            guiController = new JobInputFrameController(this);
        }

        //pack and show the gui in a window
        guiController.show();
	}

	public void setCyApplicationManager(CyApplicationManager cyApplicationManager) {
        this.cyApplicationManager = cyApplicationManager;
    }

    public CyApplicationManager getCyApplicationManager() {
        return cyApplicationManager;
    }

    public void setCyNetworkFactory(CyNetworkFactory cyNetworkFactory) {
        this.cyNetworkFactory = cyNetworkFactory;
    }

    public CyNetworkFactory getCyNetworkFactory() {
        return cyNetworkFactory;
    }

    public void setCyNetworkManager(CyNetworkManager cyNetworkManager) {
        this.cyNetworkManager = cyNetworkManager;
    }

    public CyNetworkManager getCyNetworkManager() {
        return cyNetworkManager;
    }

    public void setCyRootNetworkManager(CyRootNetworkManager cyRootNetworkManager) {
        this.cyRootNetworkManager = cyRootNetworkManager;
    }

    public CyRootNetworkManager getCyRootNetworkManager() {
        return cyRootNetworkManager;
    }

    public void setCyTableFactory(CyTableFactory cyTableFactory) {
        this.cyTableFactory = cyTableFactory;
    }

    public CyTableFactory getCyTableFactory() {
        return cyTableFactory;
    }

    public void setCyNetworkTableManager(CyNetworkTableManager cyNetworkTableManager) {
        this.cyNetworkTableManager = cyNetworkTableManager;
    }

    public CyNetworkTableManager getCyNetworkTableManager() {
        return cyNetworkTableManager;
    }

    public void setLoadVizmapFileTaskFactory(LoadVizmapFileTaskFactory loadVizmapFileTaskFactory) {
        this.loadVizmapFileTaskFactory = loadVizmapFileTaskFactory;
    }

    public LoadVizmapFileTaskFactory getLoadVizmapFileTaskFactory() {
        return loadVizmapFileTaskFactory;
    }

    public void setVisualMappingManager(VisualMappingManager visualMappingManager) {
        this.visualMappingManager = visualMappingManager;
    }

    public VisualMappingManager getVisualMappingManager() {
        return visualMappingManager;
    }

    public void setCyNetworkViewFactory(CyNetworkViewFactory cyNetworkViewFactory) {
        this.cyNetworkViewFactory = cyNetworkViewFactory;
    }

    public CyNetworkViewFactory getCyNetworkViewFactory() {
        return cyNetworkViewFactory;
    }

    public void setCyNetworkViewManager(CyNetworkViewManager cyNetworkViewManager) {
        this.cyNetworkViewManager = cyNetworkViewManager;
    }

    public CyNetworkViewManager getCyNetworkViewManager() {
        return cyNetworkViewManager;
    }

    public void setCyLayoutAlgorithmManager(CyLayoutAlgorithmManager cyLayoutAlgorithmManager) {
        this.cyLayoutAlgorithmManager = cyLayoutAlgorithmManager;
    }

    public CyLayoutAlgorithmManager getCyLayoutAlgorithmManager() {
        return cyLayoutAlgorithmManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setUndoSupport(UndoSupport UndoSupport) {
        this.undoSupport = UndoSupport;

    }

    public UndoSupport getUndoSupport() {
        return undoSupport;
    }

    public void setOpenBrowser(OpenBrowser openBrowser) {
        this.openBrowser = openBrowser;
    }

    public OpenBrowser getOpenBrowser() {
        return openBrowser;
    }
    
    public CyTableReaderManager getCyTableReaderManager() {
		return cyTableReaderManager;
	}
    
    public void setCyTableReaderManager(CyTableReaderManager cyTableReaderManager) {
		this.cyTableReaderManager = cyTableReaderManager;
	}

	public SynchronousTaskManager getSynchronousTaskManager() {
		return synchronousTaskManager;
	}

	public void setSynchronousTaskManager(SynchronousTaskManager synchronousTaskManager) {
		this.synchronousTaskManager = synchronousTaskManager;
	}

	public CyNetworkReaderManager getCyNetworkReaderManager() {
		return cyNetworkReaderManager;
	}

	public void setCyNetworkReaderManager(CyNetworkReaderManager cyNetworkReaderManager) {
		this.cyNetworkReaderManager = cyNetworkReaderManager;
	}

	public ImportDataTableTaskFactory getImportDataTableTaskFactory() {
		return importDataTableTaskFactory;
	}

	public void setImportDataTableTaskFactory(ImportDataTableTaskFactory importDataTableTaskFactory) {
		this.importDataTableTaskFactory = importDataTableTaskFactory;
	}

}
