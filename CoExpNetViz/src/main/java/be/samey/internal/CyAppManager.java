package be.samey.internal;

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
import be.samey.cynetw.CevNetworkBuilder;
import be.samey.cynetw.RunAnalysisTask;
import be.samey.gui.GuiManager;
import be.samey.io.CevNetworkReader;
import be.samey.io.CevTableReader;
import be.samey.io.CevVizmapReader;
import be.samey.io.ServerConn;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.cytoscape.work.TaskIterator;

/**
 *
 * @author sam
 */
public class CyAppManager {

    private final CyModel cyModel;
    private final CyServices cyServices;

    private GuiManager guiManager;

    private CevNetworkBuilder cevNetworkBuilder;
    private CevNetworkReader cevNetworkReader;
    private CevTableReader cevTableReader;
    private CevVizmapReader cevVizmapReader;
    private ServerConn serverConn;

    public CyAppManager(CyModel cyModel, CyServices cyServices) {
        this.cyModel = cyModel;
        this.cyServices = cyServices;

        cyModel.setSettingsPath(initSettingsPath());
//        System.out.println(cyModel.getSettingsPath());
    }

    public void runAnalysis() {

        System.out.println("---Debug output---");
        System.out.println("title: " + cyModel.getTitle());
        System.out.println("baits: " + cyModel.getBaits());
        System.out.println("names: " + Arrays.toString(cyModel.getSpeciesNames()));
        System.out.println("files: " + Arrays.toString(cyModel.getSpeciesPaths()));
        System.out.println("neg c: " + cyModel.getNCutoff());
        System.out.println("pos c: " + cyModel.getPCutoff());
        System.out.println("out d: " + cyModel.getSaveFilePath());
        System.out.println("orths: " + Arrays.toString(cyModel.getOrthGroupPaths()));

        TaskIterator ti = new TaskIterator();
        ti.append(new RunAnalysisTask(this));

        //execute() forks a new thread and returns immediatly
        cyServices.getTaskManager().execute(ti);

    }

    /**
     * Convenience method to quickly get a formatted current time string
     *
     * @return
     */
    public static String getTimeStamp() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd-hh:mm:ss");
        return sdf.format(now);
    }

    /**
     * Convenience method to get a reference to the Cytoscape desktop window.
     * Handy for use as a parent for dialog windows
     * 
     * @return 
     */
    public static Frame getCytoscapeRootFrame() {
        //TODO: cleaner to use CySwingApplication instead
        Frame[] frames = Frame.getFrames();
        Frame csFrame = null;
        for (Frame frame : frames){
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
        if (localSettingsPath != null){
            return localSettingsPath;
        }

        //attempt 2: try to find CytoscapeConfiguration folder in user.home
        cyHomePath = Paths.get(System.getProperty("user.home"));
        localSettingsPath = getCyConfFolder(cyHomePath);
        if (localSettingsPath != null){
            return localSettingsPath;
        }

        //attampt 3: Try to get a settings folder in the user home directory
        cyHomePath = Paths.get(System.getProperty("user.home"));
        localSettingsPath = getHomeSettingsFolder(cyHomePath);
        if (localSettingsPath != null){
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
            localSettingsPath = cyConfPath.resolve(CyModel.APP_NAME + "_settings");
            //settins folder doesn't exists, so try to make it
            if (!Files.exists(localSettingsPath)) {
                try {
                    Files.createDirectory(localSettingsPath);
                    return localSettingsPath;
                }catch (IOException ex) {
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
        Path localSettingsPath = searchPath.resolve(CyModel.APP_NAME + "_settings");
        if (!Files.exists(localSettingsPath)) {
            //settins folder doesn't exists, so try to make it
            try {
                Files.createDirectory(localSettingsPath);
                return localSettingsPath;
            }catch (IOException ex) {
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
    public CyModel getCyModel() {
        return cyModel;
    }

    /**
     * @return the cyServices
     */
    public CyServices getCyServices() {
        return cyServices;
    }

    /**
     * @return the guiManager
     */
    public GuiManager getGuiManager() {
        return guiManager;
    }

    /**
     * @param guiManager the guiManager to set
     */
    public void setGuiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public CevNetworkBuilder getCevNetworkBuilder() {
        if (cevNetworkBuilder == null) {
            cevNetworkBuilder = new CevNetworkBuilder(this);
        }
        return cevNetworkBuilder;
    }

    public CevNetworkReader getCevNetworkreader() {
        if (cevNetworkReader == null) {
            cevNetworkReader = new CevNetworkReader(this);
        }
        return cevNetworkReader;
    }

    public CevTableReader getCevTablereader() {
        if (cevTableReader == null) {
            cevTableReader = new CevTableReader(this);
        }
        return cevTableReader;
    }

    public CevVizmapReader getCevVizmapReader() {
        if (cevVizmapReader == null) {
            cevVizmapReader = new CevVizmapReader(this);
        }
        return cevVizmapReader;
    }

    public ServerConn getServerConn() {
        if (serverConn == null) {
            serverConn = new ServerConn(this);
        }
        return serverConn;
    }

}
