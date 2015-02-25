package be.samey.cynetw;

import be.samey.io.CevNetworkReader;
import be.samey.io.CevTableReader;
import be.samey.io.CevVizmapReader;
import be.samey.model.CoreStatus;
import be.samey.model.Model;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class CevNetworkCreator {

    private Model model;
    private CoreStatus coreStatus;
    //services
    private CyNetworkFactory cyNetworkFactory;
    private CyRootNetworkManager cyRootNetworkManager;
    private VisualMappingManager visualMappingManager;

    public CevNetworkCreator(Model model) {
        this.model = model;
        this.coreStatus = model.getCoreStatus();
        //services
        this.cyNetworkFactory = model.getServices().getCyNetworkFactory();
        this.cyRootNetworkManager = model.getServices().getCyRootNetworkManager();
        this.visualMappingManager = model.getServices().getVisualMappingManager();
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
            //all networks were deleted, set count to zero
            coreStatus.resetNetworkCount();

            //here a subnetwork is created and then promoted to a root network
            subNetwork = (CySubNetwork) cyNetworkFactory.createNetwork();
            //set name for the subnetwork
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, Model.APP_NAME + coreStatus.getNetworkCount());

            //promote the current network to a root network
            CyRootNetwork rootNetwork = cyRootNetworkManager.getRootNetwork(subNetwork);
            //set name for root network
            rootNetwork.getRow(rootNetwork).set(CyRootNetwork.NAME, Model.APP_NAME);

            //update the model
            coreStatus.setCyRootNetwork(rootNetwork);
        } else {
            //subsequent networks are added to same root network
            subNetwork = coreStatus.getCyRootNetwork().addSubNetwork();
            //set name for the subnetwork
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, Model.APP_NAME + coreStatus.getNetworkCount());
        }

        coreStatus.incrementNetworkCount();
        return subNetwork;

    }

    private boolean isCevStyleCreated() {
        //only add the visual style after the user has used this app at least once
        // to prevent cluttering the Style menu when the user is not using this
        // app
        for (VisualStyle vs : visualMappingManager.getAllVisualStyles()) {
            if (vs.getTitle().equals(Model.APP_NAME)) {
                return true;
            }

        }
        return false;

    }

    //for debugging
    public void test() {
        try {
            Path sifPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/Test_Network_File.sif");
            Path noaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/nodeattr.txt");
            Path edaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/edgeattr.txt");
            Path vizPath = Paths.get("/home/sam/Documents/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/CevStyle.xml");

            CyNetwork cn = createSubNetwork();
            CevNetworkReader.readSIF(sifPath, cn);
            CevTableReader.readNOA(noaPath, cn, model);
            CevTableReader.readEDA(edaPath, cn, model);

            if (!isCevStyleCreated()) {
                CevVizmapReader.readVIZ(vizPath, model);
            }
            model.getServices().getCyNetworkManager().addNetwork(cn);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
