package be.samey.cynetw;

import be.samey.io.CevNetworkReader;
import be.samey.io.CevTableReader;
import be.samey.io.CevVizmapReader;
import be.samey.model.CoreStatus;
import be.samey.model.Model;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

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

    private VisualStyle getCevStyle() {
        for (VisualStyle vs : visualMappingManager.getAllVisualStyles()) {
            if (vs.getTitle().equals(Model.APP_NAME)) {
                return vs;
            }

        }
        return null;

    }

    //for debugging
    public void runAnalysis() {
        try {
            //for debugging, prints the baits, files, and cutoffs specified by the user
            //in the finished app this info is sent to the server
            System.out.println("---Debug output---");
            System.out.println("baits: " + coreStatus.getBaits());
            System.out.println("names: " + Arrays.toString(coreStatus.getNames()));
            System.out.println("files: " + Arrays.toString(coreStatus.getFilePaths()));
            System.out.println("neg c: " + coreStatus.getNCutoff());
            System.out.println("pos c: " + coreStatus.getPCutoff());
            System.out.println("out d: " + coreStatus.getOutPath());
            //TODO: run the app on the server from here
            //in the finished app this info should come from files downloaded from the server
            Path sifPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/network1.sif");
            Path noaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/network1.node.attr");
            Path edaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/network1.edge.attr");
            Path vizPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network/CevStyle.xml");

            //read files
            CyNetwork cn = createSubNetwork();
            CevNetworkReader.readSIF(sifPath, cn);
            CevTableReader.readNOA(noaPath, cn, model);
            CevTableReader.readEDA(edaPath, cn, model);
            if (getCevStyle() == null) {
                // only add the visual style after the user has used this app at
                // least once to prevent cluttering the Style menu when the user
                // is not using this app
                CevVizmapReader.readVIZ(vizPath, model);
            }

            //add the network and make it visible
            model.getServices().getCyNetworkManager().addNetwork(cn);
            CyNetworkView cnv = model.getServices().getCyNetworkViewFactory().createNetworkView(cn);
            model.getServices().getCyNetworkViewManager().addNetworkView(cnv);
            getCevStyle().apply(cnv);

            CyLayoutAlgorithm layout = model.getServices().getCyLayoutAlgorithmManager().getLayout("attributes-layout");
            TaskManager<?, ?> tm = model.getServices().getTaskManager();
            TaskIterator it = layout.createTaskIterator(cnv, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "Correlation_to_baits");
            tm.execute(it);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
