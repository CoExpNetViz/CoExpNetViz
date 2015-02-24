package be.samey.cynetw;

import be.samey.model.CoreStatus;
import be.samey.model.Model;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;

/**
 *
 * @author sam
 */
public class CevNetworkCreator {

    private Model model;
    private CoreStatus coreStatus;
    //services
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkManager cyNetworkManager;
    private CyRootNetworkManager cyRootNetworkManager;
    private CyTableFactory cyTableFactory;
    private CyNetworkTableManager cyNetworkTableManager;

    public CevNetworkCreator(Model model) {
        this.model = model;
        this.coreStatus = model.getCoreStatus();
        //services
        this.cyNetworkFactory = model.getServices().getCyNetworkFactory();
        this.cyNetworkManager = model.getServices().getCyNetworkManager();
        this.cyRootNetworkManager = model.getServices().getCyRootNetworkManager();
        this.cyTableFactory = model.getServices().getCyTableFactory();
        this.cyNetworkTableManager = model.getServices().getCyNetworkTableManager();
    }

    /**
     * Creates a new network. If there is no existing {@link CyRootNetwork}
     * network created by the app, a new root network is automatically created.
     * A counter of the amount of networks created since the last root network
     * was created is kept in {@link CoreStatus}
     *
     * @return a new {@link CySubNetwork}
     */
    public CySubNetwork createSubNetwork() {
        //initialize the network
        CySubNetwork subNetwork;
        //there are two situations in which a new root network must be created
        // 1) when the app creates a network for the first time
        // 2) when the user has destroyed every network in the root network
        if (coreStatus.getCyRootNetwork() == null || coreStatus.getCyRootNetwork().getSharedNetworkTable() == null) {
            //here a subnetwork is created and then promoted to a root network
            subNetwork = (CySubNetwork) cyNetworkFactory.createNetwork();
            //promote the current network to a root network, add the root network to the model
            coreStatus.setCyRootNetwork(cyRootNetworkManager.getRootNetwork(subNetwork));
            coreStatus.resetNetworkCount();
            //set name for the subnetwork
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, Model.APP_NAME + coreStatus.getNetworkCount());
        } else {
            //subsequent networks are added to same root network
            subNetwork = coreStatus.getCyRootNetwork().addSubNetwork();
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, Model.APP_NAME + coreStatus.getNetworkCount());
        }

        coreStatus.incrementNetworkCount();
        return subNetwork;

    }

}
