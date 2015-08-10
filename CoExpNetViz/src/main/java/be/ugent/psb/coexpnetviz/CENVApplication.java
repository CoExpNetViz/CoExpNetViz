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

import be.ugent.psb.coexpnetviz.gui.RunAnalysisTask;
import be.ugent.psb.coexpnetviz.gui.controller.GUIController;
import be.ugent.psb.coexpnetviz.io.JobServer;

import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.cytoscape.work.TaskIterator;

/**
 * Global application configuration, references to Cytoscape 'services' 
 * and references to any of our own single-instances 
 */
public class CENVApplication {

    private final CENVModel cyModel;
    private final CytoscapeServices cyServices;

    private GUIController guiController;
    private JobServer serverConn;

    public CENVApplication(CytoscapeServices cyServices) {
        this.cyModel = new CENVModel();
        this.cyServices = cyServices;

        cyModel.setSettingsPath(initSettingsPath());
    }

    public void runAnalysis() {
        TaskIterator ti = new TaskIterator();
        ti.append(new RunAnalysisTask(this));
        cyServices.getTaskManager().execute(ti); // asynchronous method
    }

    /**
     * Get a formatted current time string
     *
     * @return
     */
    public static String getTimeStamp() {
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
            localSettingsPath = cyConfPath.resolve(CENVModel.APP_NAME + "_settings");
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
        Path localSettingsPath = searchPath.resolve(CENVModel.APP_NAME + "_settings");
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
     * @return the cyServices
     */
    public CytoscapeServices getCytoscapeApplication() {
        return cyServices;
    }

    /**
     * @return the guiManager
     */
    public GUIController getGUIController() {
        return guiController;
    }

    public JobServer getServerConn() {
        if (serverConn == null) {
            serverConn = new JobServer(this);
        }
        return serverConn;
    }

	public void showGUI() {
        if (guiController == null) {
            //create the Gui
            guiController = new GUIController(this);
        }

        //pack and show the gui in a window
        guiController.showRootFrame();
	}

}
