/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class CoreStatus {

    //some general information that is useful for many parts of the app
    //all subnetworks can be retrieved with rootnetwork.getSubNetworkList()
    private CyRootNetwork cyRootNetwork;

    //Keeps track of which subNetworks are visible in the 'networks' tab of the
    // main cytoscape window. Additionally, keeps track of which imported style
    // belongs to which network.
    Map<CyNetwork, Set<VisualStyle>> subNetworkStylesMap = new HashMap<CyNetwork, Set<VisualStyle>>();

    //keeps track of how many networks have been created since the last root
    // network was created
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

    public Map<CyNetwork, Set<VisualStyle>> getSubNetworkStylesMap() {
        return subNetworkStylesMap;
    }
}
