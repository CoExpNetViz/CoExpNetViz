/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.model;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;

/**
 *
 * @author sam
 */
public class Services {

    private CyApplicationManager cyApplicationManager;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkManager cyNetworkManager;
    private CyRootNetworkManager cyRootNetworkManager;
    private CyTableFactory cyTableFactory;
    private CyNetworkTableManager cyNetworkTableManager;
    private LoadVizmapFileTaskFactory loadVizmapFileTaskFactory;
    private VisualMappingManager visualMappingManager;

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
}
