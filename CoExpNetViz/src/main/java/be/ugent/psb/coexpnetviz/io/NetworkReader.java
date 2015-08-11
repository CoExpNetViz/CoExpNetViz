package be.ugent.psb.coexpnetviz.io;

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
import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.TaskIterator;

import be.ugent.psb.coexpnetviz.CENVApplication;

/**
 * Reader of networks, can read up to one network
 */
public class NetworkReader {
	
	private CENVApplication application;
	private TableReader tableReader;
	private CyNetwork network;

    /**
     * 
     * @param application
     * @param network Network to read into
     */
	public NetworkReader(CENVApplication application) {
        this.application = application;
		this.tableReader = new TableReader(application);
        this.network = null;
    }
	
	public CyNetwork getNetwork() {
		return network;
	}

    /**
     * Adds nodes and edges from a .sif file to a new network. The .sif file
     * must have the standard Cytoscape format for network files.
     *
     * @param sifPath path to the network (.sif) file
     * @param network the network to which nodes and edges in the network file
     * will be added
     * @throws IOException if the network file could not be accessed
     */
    public void readSIF(Path sifPath) throws IOException {
        CyNetworkReader networkReader = application.getCyNetworkReaderManager().getReader(sifPath.toUri(), null);
        application.getSynchronousTaskManager().execute(new TaskIterator(networkReader));
        assert networkReader.getNetworks().length == 1;
        this.network = networkReader.getNetworks()[0];
    }
    
    public CyTable readNodeAttributes(Path nodeAttributesFile) throws IOException {
		return tableReader.readAttributes(nodeAttributesFile, CyNode.class, network);
	}

	public CyTable readEdgeAttributes(Path edgeAttributesFile) throws IOException {
		return tableReader.readAttributes(edgeAttributesFile, CyEdge.class, network);
	}

}
