/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2021 PSB/UGent
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

package be.ugent.psb.coexpnetviz;

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

import com.google.common.collect.Iterables;

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
public class LayoutTask extends AbstractLayoutTask {

    private static final String TYPE_ATTRIBUTE = Context.NAMESPACE + "::type";
    private static final String PARTITION_ATTRIBUTE = Context.NAMESPACE + "::partition_id";

    private LayoutContext context;

    public LayoutTask(CyNetworkView networkView, Set<View<CyNode>> nodesToLayOut, LayoutContext context, UndoSupport undo)
    {
        super(Context.APP_NAME, networkView, nodesToLayOut, null, undo);
        this.context = context;
    }

    @Override
    final protected void doLayout(final TaskMonitor taskMonitor) {
        // Check the required node attributes are present
        CyTable dataTable = getNetwork().getDefaultNodeTable();
        requireColumn(dataTable, TYPE_ATTRIBUTE);
        requireColumn(dataTable, PARTITION_ATTRIBUTE);

        // Do layout
        LayoutBranch container = layOutConnectedComponents();
        container.updateView(0, 0);
    }
    
    private CyNetwork getNetwork() {
    	return networkView.getModel();
    }
    
    private void requireColumn(CyTable dataTable, String nodeAttribute) {
    	if (dataTable.getColumn(nodeAttribute) == null) {
            throw new RuntimeException("Could not find node attribute: " + nodeAttribute);
        }
    }
    
    private LayoutBranch layOutConnectedComponents() {
    	LayoutBranch container = new LayoutBranch();
        
    	// Create a container per connected component
    	for (Set<CyNode> connectedComponent : getConnectedComponents()) {
        	container.add(layOutConnectedComponent(connectedComponent));
        }
        
        // and lay out those components in a grid
    	container.layOutInGrid(context.connectedComponentSpacing);
        return container;
    }
    
    /*
     * Divide the the network into disjunct sets of nodes with no edge between nodes
     * of different sets. Nodes in a sit are (indirectly) connected to each other (by edges).
     * These sets are called connected components.
     */
    private Set<Set<CyNode>> getConnectedComponents() {
    	Set<Set<CyNode>> connectedComponents = new HashSet<>();
    	Set<CyNode> unvisited = new HashSet<CyNode>(getNetwork().getNodeList());
    	
    	while (!unvisited.isEmpty()) {
    		Set<CyNode> connectedComponent = new HashSet<>();
    		connectedComponents.add(connectedComponent);
    		
    		Set<CyNode> toVisit = new HashSet<>(); // nodes of the connected component that we know we haven't visited yet
    		toVisit.add(Iterables.getFirst(unvisited, null));
    		
    		while (!toVisit.isEmpty()) {
    			CyNode node = pop(toVisit);
    			unvisited.remove(node);
    			connectedComponent.add(node);
    			for (CyNode neighbour : getNetwork().getNeighborList(node, CyEdge.Type.ANY)) {
    				if (unvisited.contains(neighbour)) {
    					toVisit.add(neighbour);
    				}
    			}
    		}
    	}
    	
    	return connectedComponents;
    }
    
    private LayoutBranch layOutConnectedComponent(Set<CyNode> connectedComponent) {
    	// Group nodes by partition_id and keep baits separate
    	Map<Long, Set<CyNode>> nonBaitPartitions = new HashMap<>(); // partition id -> partition
    	Set<CyNode> baitPartition = new HashSet<>();
    	for (CyNode node : connectedComponent) {
    		CyRow row = getNetwork().getRow(node);
    		String type = row.get(TYPE_ATTRIBUTE, String.class);
    		if (type.equals("bait")) {
    			baitPartition.add(node);
    		}
    		else {
    			Long partitionId = row.get(PARTITION_ATTRIBUTE, Long.class);
    			if (!nonBaitPartitions.containsKey(partitionId)) {
    				nonBaitPartitions.put(partitionId, new HashSet<CyNode>());
    			}
    			nonBaitPartitions.get(partitionId).add(node);
    		}
    	}
    	
    	// Create children. Singletons are merged into a partition of their own.
    	Set<CyNode> singletons = new HashSet<>();
    	LayoutBranch nonBaitPartitionsContainer = new LayoutBranch();
    	for (Set<CyNode> partition : nonBaitPartitions.values()) {
    		if (partition.size() > 1) {
    			nonBaitPartitionsContainer.add(layOutNonBaitPartition(partition));
    		}
    		else {
    			singletons.add(Iterables.getOnlyElement(partition));
    		}
    	}
    	nonBaitPartitionsContainer.add(layOutNonBaitPartition(singletons));
    	
    	LayoutBranch baitsContainer = layOutBaitPartition(baitPartition);
    	
    	LayoutBranch container = new LayoutBranch();
    	container.add(nonBaitPartitionsContainer);
    	container.add(baitsContainer);
    	
    	// Lay out children
    	nonBaitPartitionsContainer.sortByArea();
    	nonBaitPartitionsContainer.layOutInCircle(context.nodeSpacing, context.baitPartitionSpacing + Math.max(baitsContainer.getWidth(), baitsContainer.getHeight()));
    	baitsContainer.setCenter(nonBaitPartitionsContainer.getWidth() / 2.0, nonBaitPartitionsContainer.getHeight() / 2.0);
    	
    	return container;
    }

	private LayoutBranch layOutNonBaitPartition(Set<CyNode> partition) {
    	LayoutBranch container = createContainer(partition);
    	container.layOutInGrid(context.nodeSpacing);
    	return container;
    }
    
    private LayoutBranch layOutBaitPartition(Set<CyNode> partition) {
    	LayoutBranch container = createContainer(partition);
    	container.layOutInCircle(context.baitNodeSpacing, 0);
    	return container;
    }
    
    private LayoutBranch createContainer(Set<CyNode> partition) {
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
    	
    	// Create Container with `Node`s
    	LayoutBranch container = new LayoutBranch();
    	for (CyNode node : sortedPartition) {
    		container.add(new LayoutLeaf(networkView.getNodeView(node)));
    	}
    	
    	return container;
    }

	private <E> E pop(Set<E> set) {
		E element = Iterables.getFirst(set, null);
		set.remove(element);
		return element;
	}

}
