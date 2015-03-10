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

    /**
     * Adds nodes and edges from a .sif file to a new network. The .sif file
     * must have the standard Cytoscape format for network files.
     *
     * @param sifPath path to the network (.sif) file
     * @param network the network to which nodes and edges in the network file
     * will be added
     * @throws IOException if the network file could not be accessed
     */
    public static void readSIF(Path sifPath, CyNetwork network) throws IOException {
        //open file
        Charset charset = Charset.forName("UTF-8");
        BufferedReader reader = Files.newBufferedReader(sifPath, charset);

        try {
            //this map keeps track of which nodes were added and which nodes were not
            Map<String, CyNode> nMap = new HashMap<String, CyNode>();
            String line;
            String[] lineSplit;
            String firstNodeName;
            String linkType;
            String[] connectedNodeNames;
            CyNode firstNode;
            CyNode connectedNode;
            //iterate over the file
            while ((line = reader.readLine()) != null) {
                //parse line
                lineSplit = line.split("\\s");
                firstNodeName = lineSplit[0];
                linkType = lineSplit[1];
                connectedNodeNames = Arrays.copyOfRange(lineSplit, 2, lineSplit.length);

                //add first node to node map if needed
                if (!nMap.containsKey(firstNodeName)) {
                //the node is not in the map, which means it is also not in the network
                    //thus create the node
                    firstNode = network.addNode();
                    network.getRow(firstNode).set(CyNetwork.NAME, firstNodeName);
                    nMap.put(firstNodeName, firstNode);
                }
                firstNode = nMap.get(firstNodeName);

                //iterate nodes linked to first node
                for (String connectedNodeName : connectedNodeNames) {
                    //add other nodes to node map if needed
                    if (!nMap.containsKey(connectedNodeName)) {
                    //the node is not in the map, which means it is also not in the network
                        //thus create the node
                        connectedNode = network.addNode();
                        network.getRow(connectedNode).set(CyNetwork.NAME, connectedNodeName);
                        nMap.put(connectedNodeName, connectedNode);
                    }
                    connectedNode = nMap.get(connectedNodeName);
                    //make the edge
                    CyEdge edge = network.addEdge(firstNode, connectedNode, true);
                    network.getRow(edge).set(CyNetwork.NAME, String.format("%s (%s) %s", firstNodeName, linkType, connectedNodeName));
                    network.getRow(edge).set(CyEdge.INTERACTION, linkType);
                }

            }

        } finally {
            reader.close();
        }
    }

}
