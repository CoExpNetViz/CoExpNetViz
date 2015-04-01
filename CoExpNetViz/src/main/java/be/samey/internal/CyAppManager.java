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
import java.util.Arrays;
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
    }

    public void runAnalysis(){

        System.out.println("---Debug output---");
        System.out.println("baits: " + cyModel.getBaits());
        System.out.println("names: " + Arrays.toString(cyModel.getSpeciesNames()));
        System.out.println("files: " + Arrays.toString(cyModel.getSpeciesPaths()));
        System.out.println("neg c: " + cyModel.getNCutoff());
        System.out.println("pos c: " + cyModel.getPCutoff());
        System.out.println("out d: " + cyModel.getSaveFilePath());


        TaskIterator ti = new TaskIterator();
        ti.append(new RunAnalysisTask(this));

        //execute() forks a new thread and returns immediatly
        cyServices.getTaskManager().execute(ti);

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
        if (cevNetworkReader == null){
            cevNetworkReader = new CevNetworkReader(this);
        }
        return cevNetworkReader;
    }

    public CevTableReader getCevTablereader() {
        if (cevTableReader == null){
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
