package be.ugent.psb.coexpnetviz.internal;

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

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;

/**
 * this class creates an easy way to share the cytoscape services with any class
 * in this application.
 * 
 * @author sam
 */
public class CyServices {

    private CyApplicationManager cyApplicationManager;
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
    private TaskManager taskManager;
    private UndoSupport undoSupport;
    private OpenBrowser openBrowser;

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

}
