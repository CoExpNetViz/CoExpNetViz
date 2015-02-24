/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.model;

import org.cytoscape.model.subnetwork.CyRootNetwork;

/**
 *
 * @author sam
 */
public class CoreStatus {

    private Model model;

    public CoreStatus(Model model) {
        this.model = model;
    }

    //some general information that is useful for many parts of the app
    //all subnetworks can be retrieved with rootnetwork.getSubNetworkList()
    private CyRootNetwork cyRootNetwork;
    //keeps track of how many networks have been created since the root network
    //was created
    private int networkCount = 0;

    public CyRootNetwork getCyRootNetwork() {
        return cyRootNetwork;
    }

    public void setCyRootNetwork(CyRootNetwork cyRootNetwork) {
        this.cyRootNetwork = cyRootNetwork;
    }

    public int getNetworkCount() {
        return networkCount;
    }

    public void resetNetworkCount() {
        networkCount = 0;
    }

    public void incrementNetworkCount() {
        networkCount++;
    }

}
