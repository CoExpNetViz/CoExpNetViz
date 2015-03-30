package be.samey.cynetw;

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

import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import be.samey.internal.CyServices;
import java.io.IOException;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author sam
 */
public class CevNetworkBuilder /*implements Observer*/ {
    
    private final CyAppManager cyAppManager;
    private final CyServices cyServices;
    private final CyModel cyModel;

    public CevNetworkBuilder(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();
        this.cyServices = cyAppManager.getCyServices();
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
        if (cyModel.getCyRootNetwork() == null || cyModel.getCyRootNetwork().getSharedNetworkTable() == null) {
            //all networks were deleted, set count to zero
            cyModel.resetNetworkCount();

            //here a subnetwork is created and then promoted to a root network
            subNetwork = (CySubNetwork) cyServices.getCyNetworkFactory().createNetwork();
            //set name for the subnetwork
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, CyModel.APP_NAME + cyModel.getNetworkCount());

            //promote the current network to a root network
            CyRootNetwork rootNetwork = cyServices.getCyRootNetworkManager().getRootNetwork(subNetwork);
            //set name for root network
            rootNetwork.getRow(rootNetwork).set(CyRootNetwork.NAME, CyModel.APP_NAME);

            //update the model
            cyModel.setCyRootNetwork(rootNetwork);
        } else {
            //subsequent networks are added to same root network
            subNetwork = cyModel.getCyRootNetwork().addSubNetwork();
            //set name for the subnetwork
            subNetwork.getRow(subNetwork).set(CyNetwork.NAME, CyModel.APP_NAME + cyModel.getNetworkCount());
        }
        
        cyModel.incrementNetworkCount();
        return subNetwork;
        
    }
    
    private VisualStyle getCevStyle() {
        for (VisualStyle vs : cyServices.getVisualMappingManager().getAllVisualStyles()) {
            if (vs.getTitle().equals(CyModel.APP_NAME)) {
                return vs;
            }
            
        }
        return null;
        
    }

    /**
     * reads the network files and creates a view. Applies Style. Adds the
     * networks nodeTable and view to the coremodel.
     *
     * @param tm
     */
    public void createNetworkView(TaskMonitor tm) throws Exception {
        tm.setStatusMessage("creating network");
        tm.setProgress(0.5);
        try {

            //read files
            CyNetwork cn = createSubNetwork();
            cyAppManager.getCevNetworkreader().readSIF(cyModel.getSifPath(), cn);
            CyTable cevNodeTable = cyAppManager.getCevTablereader().readNOA(cyModel.getNoaPath(), cn);
            cyAppManager.getCevTablereader().readEDA(cyModel.getEdaPath(), cn);
            if (getCevStyle() == null) {
                // only add the visual style after the user has used this app at
                // least once to prevent cluttering the Style menu when the user
                // is not using this app
                cyAppManager.getCevVizmapReader().readVIZ();
            }

            //add the network
            cyAppManager.getCyServices().getCyNetworkManager().addNetwork(cn);
            CyNetworkView cnv = cyAppManager.getCyServices().getCyNetworkViewFactory().createNetworkView(cn);

            //add this data to the corestatus to pass it on to the next task
            //which is applying the layout
            cyModel.setLastNoaTable(cevNodeTable);
            cyModel.setLastCnv(cnv);
            
            cyAppManager.getCyServices().getCyNetworkViewManager().addNetworkView(cnv);
            cyAppManager.getCyServices().getVisualMappingManager().setVisualStyle(getCevStyle(), cnv);
            
        } catch (IOException ex) {
            //when executed in a Task, this message will be shown to the user
            // (see documentation for org.cytoscape.work.Task)
            throw new Exception(String.format("An error ocurred while reading the network files%n%s%n", ex));
        }
        
    }
    
}
