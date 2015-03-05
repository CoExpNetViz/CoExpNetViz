package be.samey.io;

import be.samey.model.Model;
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
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

/**
 *
 * @author sam
 */
public class CevTableReader {

    /**
     * Adds the node attributes from a node attribute file to a given network.
     * The node attribute file must be tab delimited, the first column must
     * contain the node names. The file must also contain a tab delimited header
     * line, the header is used to assign column names. Data types are assigned
     * automatically: if a column entry is a latin number the data type is
     * {@link double}, if not the data type is {@link String}.
     * <p>
     * I don't know yet what will happen if column has mixed data types, but it
     * will probably be bad.
     *
     * @param noaPath path to node attribute file
     * @param network network to add attributes to
     * @param model the {@link  Model} for the app
     * @throws IOException it the attribute file could not be accessed
     */
    public static void readNOA(Path noaPath, CyNetwork network, Model model) throws IOException {

        //get the required service for this method
        CyNetworkTableManager cyNetworkTableManager = model.getServices().getCyNetworkTableManager();

        //read the file in a TableMap
        TableMap tableMap = makeTableMap(noaPath);

        //add columns to network node table 
        CyTable cevNodeTable = cyNetworkTableManager.getTable(network, CyNode.class, CyNetwork.LOCAL_ATTRS);
        for (int i = 0; i < tableMap.header.length; i++) {
            String attrColumnName = tableMap.header[i];
            if (cevNodeTable.getColumn(attrColumnName) == null) {
                cevNodeTable.createColumn(attrColumnName, tableMap.attrTypes[i], false);
            }
        }

        //add attributes to network edge table
        CyRow row;
        String nodeName;
        Object[] edgeAttr;
        for (CyNode node : network.getNodeList()) {
            row = network.getRow(node);
            nodeName = row.get(CyNetwork.NAME, String.class);
            edgeAttr = tableMap.aMap.get(nodeName);
            for (int i = 0; i < edgeAttr.length; i++) {
                row.set(tableMap.header[i], edgeAttr[i]);
            }
        }
    }

    /**
     * Adds the edge attributes from a edge attribute file to a given network.
     * The edge attribute file must be tab delimited, the first column must
     * contain the edge names. The file must also contain a tab delimited header
     * line, the header is used to assign column names. Data types are assigned
     * automatically: if a column entry is a latin number the data type is
     * {@link double}, if not the data type is {@link String}.
     * <p>
     * I don't know yet what will happen if column has mixed data types, but it
     * will probably be bad.
     *
     * @param edaPath path to edge attribute file
     * @param network network to add attributes to
     * @param model the {@link  Model} for the app
     * @throws IOException it the attribute file could not be accessed
     */
    public static void readEDA(Path edaPath, CyNetwork network, Model model) throws IOException {

        //get the required service for this method
        CyNetworkTableManager cyNetworkTableManager = model.getServices().getCyNetworkTableManager();

        //read the file in a TableMap
        TableMap tableMap = makeTableMap(edaPath);

        //add columns to network edge table 
        CyTable cevEdgeTable = cyNetworkTableManager.getTable(network, CyEdge.class, CyNetwork.LOCAL_ATTRS);
        for (int i = 0; i < tableMap.header.length; i++) {
            String attrColumnName = tableMap.header[i];
            if (cevEdgeTable.getColumn(attrColumnName) == null) {
                cevEdgeTable.createColumn(attrColumnName, tableMap.attrTypes[i], false);
            }
        }

        //add attributes to network edge table
        CyRow row;
        String edgeName;
        Object[] edgeAttr;
        for (CyEdge edge : network.getEdgeList()) {
            row = network.getRow(edge);
            edgeName = row.get(CyNetwork.NAME, String.class);
            edgeAttr = tableMap.aMap.get(edgeName);
            for (int i = 0; i < edgeAttr.length; i++) {
                row.set(tableMap.header[i], edgeAttr[i]);
            }
        }
    }

    /**
     * Utility method to determine if a string is numeric. Does not work for
     * non-latin digits.
     *
     * @param str any object of type {@link String}
     * @return <code>true<\code> if str is numeric and has latin digits,
     * <code>false<\code> otherwise
     */
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * Creates a map from an attribute file which holds attributes of
     * nodes/edges mapped to their name. The type of the attribute is detected
     * automatically. The file header information, the types of the attributes
     * and the attributes themselves (stored in the map) are returned as fields
     * of a <code>TableMap</code> object.
     *
     * @param edaPath The {@link Path} to a attribute file
     * @return The attributes and other data stored in a <code>TableMap</code>
     * @throws IOException if the input file can not be accessed
     */
    private static TableMap makeTableMap(Path edaPath) throws IOException {
        //open file
        Charset charset = Charset.forName("UTF-8");
        BufferedReader reader = Files.newBufferedReader(edaPath, charset);

        //get column names, first column name does not matter because it is 
        // already present, it contains the attribute mapping names.
        String[] header = reader.readLine().split("\t");
        header = Arrays.copyOfRange(header, 1, header.length);

        //holds attributes
        Map<String, Object[]> aMap = new HashMap<String, Object[]>();
        String line;
        String[] lineSplit = null;
        Object[] attributes = new Object[header.length];

        //holds types of columns
        Class[] attrTypes = new Class[header.length];
        //start all columns as double, set to string if at least one value in
        // column isn't numeric
        for (int i = 0; i < header.length; i++) {
            attrTypes[i] = Double.class;
        }

        //iterate over the file
        while ((line = reader.readLine()) != null) {
            //parse line
            lineSplit = line.split("\t");
            attributes = new Object[header.length];
            //store attribute as number if it is numeric
            for (int i = 1; i < lineSplit.length; i++) {
                if (isNumeric(lineSplit[i])) {
                    attributes[i - 1] = Double.parseDouble(lineSplit[i]);
                } else {
                    attributes[i - 1] = lineSplit[i];
                    attrTypes[i - 1] = String.class; //any non numeric value makes this a column of Strings
                }
            }
            aMap.put(lineSplit[0], attributes);
        }
        TableMap tableMap = new TableMap(header, attrTypes, aMap);
        return tableMap;
    }

    /**
     * Small utility class, its only purpose is to hold some variables created
     * by the <code>makeTableMap</code> method. These variables can then easily
     * be passed around in one object.
     */
    private static class TableMap {

        String[] header;
        Class[] attrTypes;
        Map<String, Object[]> aMap = new HashMap<String, Object[]>();

        TableMap(String[] header, Class[] attrTypes, Map<String, Object[]> aMap) {
            this.header = header;
            this.attrTypes = attrTypes;
            this.aMap = aMap;
        }
    }
}
