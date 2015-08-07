package be.samey.io;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 *
 * @author sam
 */
public class CevNetworkReader {

    private final CyAppManager cyAppManager;

    public CevNetworkReader(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
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
    public void readSIF(Path sifPath, CyNetwork network) throws IOException {
        //open file
        Charset charset = Charset.forName("UTF-8");
        BufferedReader reader = Files.newBufferedReader(sifPath, charset);

        try {
        	// Gets node by name and creates it if it doesn't exist
        	Nodes nodes = new Nodes(network);
        	
            //iterate over the file
            String line;
            while ((line = reader.readLine()) != null) {
                //parse line
            	String[] lineSplit = line.split("\\s+");
            	String nodeName = lineSplit[0];
            	String linkType = lineSplit[1];
            	String[] neighbourNames = Arrays.copyOfRange(lineSplit, 2, lineSplit.length);

                //add first node to node map if needed
            	CyNode node = nodes.get(nodeName);

                // Add edge to each neighbour
                for (String neighbourName : neighbourNames) {
                    //make the edge
                    CyEdge edge = network.addEdge(node, nodes.get(neighbourName), true);
                    network.getRow(edge).set(CyNetwork.NAME, String.format("%s (%s) %s", nodeName, linkType, neighbourName));
                    network.getRow(edge).set(CyEdge.INTERACTION, linkType);
                }
            }
        } finally {
            reader.close();
        }
    }
    
    /// Gets node by name and creates it if it doesn't exist
    class Nodes
    {
		private Map<String, CyNode> nodes; // names -> nodes
		private CyNetwork network;
		
		public Nodes(CyNetwork network) {
			nodes = new HashMap<String, CyNode>();
			this.network = network;
		}
		
		public CyNode get(String name) {
			if (!nodes.containsKey(name)) {
	        	//the node is not in the map, which means it is also not in the network
	            //thus create the node
	            CyNode node = network.addNode();
	            network.getRow(node).set(CyNetwork.NAME, name);
	            nodes.put(name, node);
	        }
	        return nodes.get(name);
		}
	}

}
