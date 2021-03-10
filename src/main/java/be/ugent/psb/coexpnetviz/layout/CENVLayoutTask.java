package be.ugent.psb.coexpnetviz.layout;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import be.ugent.psb.util.Sets;

/**
 * Lay out nodes of a CENV network.
 * 
 * First the network is separated into a grid according to its connected components.
 * 
 * Within each connected component, each partition is laid out as a grid. The baits
 * in the connected component are laid out in the middle as a circle, with other
 * partitions placed around it in a circle. Singleton partitions are grouped together
 * in a grid as if it were a single partition (but the colours will give away they're
 * not really of the same partition).
 * 
 * Within each partition, nodes are sorted by their name. Partitions in the circle of
 * partitions are sorted by their size.
 */
public class CENVLayoutTask extends AbstractLayoutTask {

    private static final String TYPE_ATTRIBUTE = "type"; // 'bait node' or 'family node'
    private static final String PARTITION_ATTRIBUTE = "partition_id";

    private CENVLayoutContext context;

    public CENVLayoutTask(String displayName, CyNetworkView networkView, Set<View<CyNode>> nodesToLayOut, CENVLayoutContext context, UndoSupport undo)
    {
        super(displayName, networkView, nodesToLayOut, PARTITION_ATTRIBUTE, undo);
        this.context = context;
    }

    @Override
    final protected void doLayout(final TaskMonitor taskMonitor) {
        // Check the required node attributes are present (i.e. is it a CENV network?)
        CyTable dataTable = getNetwork().getDefaultNodeTable();
        ensureExists(dataTable, TYPE_ATTRIBUTE);
        ensureExists(dataTable, PARTITION_ATTRIBUTE);

        // Do layout
        Container container = layOutConnectedComponents(getConnectedComponents());
        container.updateView(0, 0);
    }
    
    private CyNetwork getNetwork() {
    	return networkView.getModel();
    }
    
    private void ensureExists(CyTable dataTable, String nodeAttribute) {
    	if (dataTable.getColumn(nodeAttribute) == null) {
            throw new RuntimeException(String.format("Could not find node attribute: %s", nodeAttribute));
        }
    }
    
    private Set<Set<CyNode>> getConnectedComponents() {
        Set<Set<CyNode>> connectedComponents = new HashSet<>();
        Set<CyNode> unvisited = new HashSet<CyNode>(getNetwork().getNodeList());
        
        while (!unvisited.isEmpty()) {
        	Set<CyNode> connectedComponent = new HashSet<>();
        	connectedComponents.add(connectedComponent);
        	
        	Set<CyNode> toVisit = new HashSet<>(); // nodes of the connected component that we know we haven't visited yet
        	toVisit.add(Iterables.getFirst(unvisited, null));
            
            while (!toVisit.isEmpty()) {
                CyNode node = Sets.pop(toVisit);
                unvisited.remove(node);
                connectedComponent.add(node);
                for (CyNode neighbour : getNetwork().getNeighborList(node, CyEdge.Type.ANY)) {
                	if (unvisited.contains(neighbour)) {
                		toVisit.add(neighbour);
                	}
                }
            }
            System.out.println("connected component: " + connectedComponent.size());
        }
        
        System.out.println("got the connected components");
        return connectedComponents;
    }
    
    private Container layOutConnectedComponents(Set<Set<CyNode>> connectedComponents) {
    	Container container = new Container();
        
    	// Create children
    	for (Set<CyNode> connectedComponent : connectedComponents) {
        	container.getChildren().add(layOutConnectedComponent(connectedComponent));
        }
        
        // Lay out children
    	container.layOutInGrid(context.connectedComponentSpacing);
        return container;
    }
    
    private Container layOutConnectedComponent(Set<CyNode> connectedComponent) {
    	// Split into family partitions and baits partition
    	Map<Object, Set<CyNode>> familyPartitions = new HashMap<>(); // partition id -> partition
    	Set<CyNode> baitPartition = new HashSet<>();
    	for (CyNode node : connectedComponent) {
    		CyRow row = getNetwork().getRow(node);
    		String type = row.get(TYPE_ATTRIBUTE, String.class);
    		if (type.equals("bait node")) {
    			baitPartition.add(node);
    		}
    		else if (type.equals("family node")) {
    			Object partitionId = row.getRaw(PARTITION_ATTRIBUTE);
    			if (!familyPartitions.containsKey(partitionId)) {
    				familyPartitions.put(partitionId, new HashSet<CyNode>());
    			}
    			familyPartitions.get(partitionId).add(node);
    		}
    		else {
    			throw new RuntimeException("Invalid node type: " + type);
    		}
    	}
    	
    	// Create children
    	Set<CyNode> singletons = new HashSet<>();
    	Container familyPartitionsContainer = new Container();
    	for (Set<CyNode> partition : familyPartitions.values()) { // family partitions and merge singleton partitions into a pseudo partition
    		if (partition.size() > 1) {
    			familyPartitionsContainer.getChildren().add(layOutFamilyPartition(partition));
    		}
    		else {
    			singletons.add(Iterables.getOnlyElement(partition));
    		}
    	}
    	familyPartitionsContainer.getChildren().add(layOutFamilyPartition(singletons));
    	
    	Container baitsContainer = layOutBaitPartition(baitPartition);
    	
    	Container container = new Container();
    	container.getChildren().add(familyPartitionsContainer);
    	container.getChildren().add(baitsContainer);
    	
    	// Lay out children
    	familyPartitionsContainer.sortByArea();
    	familyPartitionsContainer.layOutInCircle(context.familyNodeSpacing, context.baitToFamilyPartitionSpacing + Math.max(baitsContainer.getWidth(), baitsContainer.getHeight()));
    	baitsContainer.setCenter(familyPartitionsContainer.getWidth() / 2.0, familyPartitionsContainer.getHeight() / 2.0);
    	
    	return container;
    }
    
    private Container layOutFamilyPartition(Set<CyNode> partition) {
    	Container container = layOutPartition(partition);
    	container.layOutInGrid(context.familyNodeSpacing);
    	return container;
    }
    
    private Container layOutBaitPartition(Set<CyNode> partition) {
    	Container container = layOutPartition(partition);
    	container.layOutInCircle(context.baitNodeSpacing, 0);
    	return container;
    }
    
    /**
     * Get container of partition with nodes sorted by name 
     */
    private Container layOutPartition(Set<CyNode> partition) {
    	// Sort nodes by name
    	List<CyNode> sortedPartition = new ArrayList<>(partition);
    	Collections.sort(sortedPartition, new Comparator<CyNode>() {
    		@Override
    		public int compare(CyNode n1, CyNode n2) {
    			String name1 = getNetwork().getRow(n1).get(CyNetwork.NAME, String.class);
    			String name2 = getNetwork().getRow(n2).get(CyNetwork.NAME, String.class);
    			return name1.compareToIgnoreCase(name2);
    		}
    	});
    	
    	// Create `Container` with `Node`s
    	Container container = new Container();
    	for (CyNode node : sortedPartition) {
    		container.getChildren().add(new Node(networkView.getNodeView(node)));
    	}
    	
    	return container;
    }

}
