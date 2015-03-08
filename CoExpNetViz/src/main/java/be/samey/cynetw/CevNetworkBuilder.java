package be.samey.cynetw;

import be.samey.io.CevNetworkReader;
import be.samey.io.CevTableReader;
import be.samey.io.CevVizmapReader;
import be.samey.model.CoreStatus;
import be.samey.model.Model;
import be.samey.model.Services;
import java.io.IOException;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class CevNetworkBuilder /*implements Observer*/ {

    private final Model model;
    private final CoreStatus coreStatus;
    private final Services services;

    public CevNetworkBuilder(Model model) {
        this.model = model;
        this.coreStatus = model.getCoreStatus();
        this.services = model.getServices();
    }

    /**
     * Creates a new network. If there is no existing {@link CyRootNetwork}
     * network created by the app, a new root network is automatically created.
     * A counter of the amount of networks created since the last root network
     * was created is kept in {@link CoreStatus}
     *
     * @return a new {@link CySubNetwork}
     */
    private CySubNetwork createSubNetwork() {
        //initialize the network
        CySubNetwork subNetwork;
        //there are two situations in which a new root network must be created
        // 1) when the app creates a network for the first time
        // 2) when the user has destroyed every network in the root network
        if (coreStatus.getCyRootNetwork() == null || coreStatus.getCyRootNetwork().getSharedNetworkTable() == null) {
            //all networks were deleted, set count to zero
            coreStatus.resetNetworkCount();

            //here a subnetwork is created and then promoted to a root network
            subNetwork = (CySubNetwork) services.getCyNetworkFactory().createNetwork();
            //set name for the subnetwork
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, Model.APP_NAME + coreStatus.getNetworkCount());

            //promote the current network to a root network
            CyRootNetwork rootNetwork = services.getCyRootNetworkManager().getRootNetwork(subNetwork);
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
        for (VisualStyle vs : services.getVisualMappingManager().getAllVisualStyles()) {
            if (vs.getTitle().equals(Model.APP_NAME)) {
                return vs;
            }

        }
        return null;

    }

    /**
     * reads the network files and creates a view, add the networks nodeTable
     * and view to the coremodel.
     *
     */
    public void createNetworkView() {
        try {

            //read files
            CyNetwork cn = createSubNetwork();
            CevNetworkReader.readSIF(coreStatus.getSifPath(), cn);
            CyTable cevNodeTable = CevTableReader.readNOA(coreStatus.getNoaPath(), cn, model);
            CevTableReader.readEDA(coreStatus.getEdaPath(), cn, model);
            if (getCevStyle() == null) {
                // only add the visual style after the user has used this app at
                // least once to prevent cluttering the Style menu when the user
                // is not using this app
                CevVizmapReader.readVIZ(coreStatus.getVizPath(), model);
            }

            //add the network
            model.getServices().getCyNetworkManager().addNetwork(cn);
            CyNetworkView cnv = model.getServices().getCyNetworkViewFactory().createNetworkView(cn);

            //add this data to the corestatus to pass it on to the next task
            //which is applying the layout
            coreStatus.setLastNoaTable(cevNodeTable);
            coreStatus.setLastCnv(cnv);

            model.getServices().getCyNetworkViewManager().addNetworkView(cnv);
            model.getServices().getVisualMappingManager().setVisualStyle(getCevStyle(), cnv);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            //TODO: warn user somehow
        }

    }

}
