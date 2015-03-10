package be.samey.io;

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
