package be.samey.cynetw;

import be.samey.io.CevNetworkReader;
import be.samey.io.CevTableReader;
import be.samey.io.CevVizmapReader;
import be.samey.model.CoreStatus;
import be.samey.model.Model;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.AbstractLayoutTask;
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
public class CevNetworkBuilder implements Observer {

    private Model model;
    private CoreStatus coreStatus;
    //services
    private CyNetworkFactory cyNetworkFactory;
    private CyRootNetworkManager cyRootNetworkManager;
    private VisualMappingManager visualMappingManager;

    public CevNetworkBuilder(Model model) {
        this.model = model;
        this.coreStatus = model.getCoreStatus();
        coreStatus.addObserver(this);
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
            model.getServices().getCyNetworkViewManager().addNetworkView(cnv);
            getCevStyle().apply(cnv);

            //add this data to the corestatus to pass it on to the next task
            //which is applying the layout
            coreStatus.setLastNoaTable(cevNodeTable);
            coreStatus.setLastCnv(cnv);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            //TODO: warn user somehow
        }

    }

    /**
     * applies the layout to the last view
     */
    public void applyLayout() {

        CevGroupAttributesLayout layout = (CevGroupAttributesLayout) model.getServices().
            getCyLayoutAlgorithmManager().getLayout("cev-attributes-layout");
        TaskManager<?, ?> tm = model.getServices().getTaskManager();
        ArrayList<CyColumn> columnList = (ArrayList) coreStatus.getLastNoaTable().getColumns();
        String groupColumnName = columnList.get(4 + CoreStatus.GROUP_COLUMN).getName();
        String speciesColumnName = columnList.get(4 + CoreStatus.SPECIES_COLUMN).getName();
        TaskIterator ti = layout.createTaskIterator(coreStatus.getLastCnv(),
            layout.createLayoutContext(),
            CyLayoutAlgorithm.ALL_NODE_VIEWS,
            groupColumnName,
            speciesColumnName);
        tm.execute(ti);

//        AbstractLayoutTask alt = new CevGroupAttributesLayoutTask("CevGroupAttributes",
//            coreStatus.getLastCnv(),
//            CyLayoutAlgorithm.ALL_NODE_VIEWS,
//            new CevGroupAttributesLayoutContext(),
//            groupColumnName,
//            null);
    }

    @Override
    public void update(Observable o, Object o1) {
        applyLayout();
    }

}
