package be.ugent.psb.coexpnetviz.gui;

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

import java.io.IOException;
import java.nio.file.Path;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;

import be.ugent.psb.coexpnetviz.CENVApplication;
import be.ugent.psb.coexpnetviz.CytoscapeServices;
import be.ugent.psb.coexpnetviz.io.NetworkReader;
import be.ugent.psb.coexpnetviz.io.VizmapReader;

/**
 * Contains Cytoscape network functionality.
 *
 * @author sam
 */
public class CENVNetworkBuilder {

    private final CENVApplication application;
    private final CENVModel cyModel;

    public CENVNetworkBuilder(CENVApplication cyAppManager) {
        this.application = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();
    }

    private VisualStyle getStyle(String name) {
    	for (VisualStyle vs : application.getCytoscapeApplication().getVisualMappingManager().getAllVisualStyles()) {
            if (vs.getTitle().equals(name)) {
                return vs;
            }
        }
        return null;
    }
    
    /**
     * Get style, fall back to file if not found
     * 
     * @param stylePath Path to vizmap file to get, it must contain a style with name equals `name`
     */
    private VisualStyle getStyle(String name, Path stylePath) {
    	// Add the visual style no sooner than when it's needed and add it only once, To prevent cluttering the Style menu.
    	if (getStyle(name) == null) {
            new VizmapReader(application).readVIZ(stylePath);
        }
    	return getStyle(name);
    }

    /**
     * reads the network files and creates a view. Applies Style. Adds the
     * networks nodeTable and view to the coremodel.
     *
     * @throws java.lang.Exception
     * @param networkDir Directory with the sif and attr files
     */
    public void createNetworkView(Path networkDir) throws Exception {
    	CyNetwork network;
    	try {
            // Read network
            NetworkReader networkReader = new NetworkReader(application);
            networkReader.readSIF(networkDir.resolve("network.sif"));
            cyModel.setLastNoaTable(networkReader.readNodeAttributes(networkDir.resolve("network.node.attr"))); // Note: lastNoaTable is simply passed on to the next task TODO
            networkReader.readEdgeAttributes(networkDir.resolve("network.edge.attr"));
            network = networkReader.getNetwork();
        } catch (IOException ex) {
            //when executed in a Task, this message will be shown to the user
            // (see documentation for org.cytoscape.work.Task)
            throw new Exception(String.format("An error ocurred while reading the network files%n%s%n", ex), ex);
        }

        // Add the network
        application.getCytoscapeApplication().getCyNetworkManager().addNetwork(network);
        
        // Add view
        CyNetworkView networkView = application.getCytoscapeApplication().getCyNetworkViewFactory().createNetworkView(network);
        application.getCytoscapeApplication().getCyNetworkViewManager().addNetworkView(networkView);
        application.getCytoscapeApplication().getVisualMappingManager().setVisualStyle(getStyle(CENVModel.APP_NAME, networkDir.resolve("cenv_style.xml")), networkView);

        //add this data to the corestatus to pass it on to the next task
        //which is applying the layout
        cyModel.setLastCnv(networkView);
        cyModel.getVisibleCevNetworks().add(network);
    }

}
