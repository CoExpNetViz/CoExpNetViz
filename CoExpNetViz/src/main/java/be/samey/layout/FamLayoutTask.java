package be.samey.layout;

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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.cytoscape.model.CyEdge;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FamLayoutTask extends AbstractLayoutTask {

    private static Logger logger = LoggerFactory.getLogger(FamLayoutTask.class);

    private TaskMonitor taskMonitor;
    private CyNetwork network;
    private String speciesAttribute;

    private FamLayoutContext context;

    public FamLayoutTask(final String displayName,
        CyNetworkView networkView,
        Set<View<CyNode>> nodesToLayOut,
        FamLayoutContext context,
        String attrName,
        String baitName,
        UndoSupport undo) {

        super(displayName, networkView, nodesToLayOut, attrName, undo);

        this.context = context;
        this.speciesAttribute = baitName;
    }

    @Override
    final protected void doLayout(final TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
        this.network = networkView.getModel();

        taskMonitor.setStatusMessage("Applying layout algorithm");
        taskMonitor.setProgress(0.7);

        if (layoutAttribute == null || layoutAttribute.equals("(none)")) {
            throw new NullPointerException("Attribute is null.  This is required for this layout.");
        }
        if (speciesAttribute == null) {
            speciesAttribute = context.speciesAttribute;
            CyTable dataTable = network.getDefaultNodeTable();
            if (dataTable.getColumn(speciesAttribute) == null) {
                throw new NullPointerException(String.format("Could not find the column \"%s\"%n", speciesAttribute));
            }
        }

        construct();
    }

    /**
     * Pseudo-procedure:
     *
     * TODO: document this, because I will never remember how I did this
     *
     */
    private void construct() {

        if (layoutAttribute == null) {
            logger.warn("Attribute name is not defined.");
            return;
        }

        taskMonitor.setStatusMessage("Initializing");

        CyTable dataTable = network.getDefaultNodeTable();
        Class<?> klass = dataTable.getColumn(layoutAttribute).getType();
        Class<?> qlass = dataTable.getColumn(speciesAttribute).getType();

        if (Comparable.class.isAssignableFrom(klass) && Comparable.class.isAssignableFrom(qlass)) {
            Class<Comparable> kasted = (Class<Comparable>) klass;
            Class<Comparable> qasted = (Class<Comparable>) qlass;
            doConstruct(kasted, qasted);
        } else {
            /* FIXME Error. */
        }

    }

    /**
     * Does the actual layout
     */
    private <T extends Comparable<T>, U extends Comparable<U>> void doConstruct(Class<T> klass, Class<U> qlass) {
        final List<CComponent<T, U>> cCList = new ArrayList<CComponent<T, U>>();
        preprocessNodes(cCList, klass, qlass);

        double offsetx = 0.0;
        double offsety = 0.0;
        double maxheight = 0.0;
        double ccradius = 0.0;

        for (CComponent cComponent : cCList) {
            List<List<CyNode>> targetList = cComponent.getSortedTargets();
            List<List<CyNode>> baitsList = cComponent.getSortedBaits();

            if (cComponent.size == 1) {
                //group unconnected nodes in a square

                addToSquare(targetList.get(0).get(0), offsetx, offsety);

                if (offsetx > context.maxwidths) {
                    offsety += context.nspacingy;
                    offsetx = 0.0;
                } else {
                    offsetx += context.nspacingx;
                }

            } else if (cComponent.size > 1) {
                //one connected component is grouped in partions here

                double ccstarty = offsety;

                //group targets in partitions
                for (List<CyNode> partition : targetList) {
                    if (cancelled) {
                        return;
                    }

                    double radius = encircle(partition, offsetx, offsety, 1.0);

                    double diameter = 2.0 * radius;

                    if (diameter > maxheight) {
                        maxheight = diameter;
                    }

                    offsetx += diameter;

                    if (offsetx > context.maxwidth) {
                        offsety += (maxheight + context.spacingy);
                        offsetx = 0.0;
                        maxheight = 0.0;
                    } else {
                        offsetx += context.spacingx;
                    }
                }

                //baits are placed around the targets in a large circle
                if (baitsList != null) {
                    ccradius = encirclebaits(ccstarty, offsety, baitsList);
                } else {
                    ccradius = 0.0;
                }

                //prepare offsets for next connected component
                offsetx = 0.0;
                offsety += maxheight + context.ccspacingy + ccradius;

            }
        }
    }

    private <T extends Comparable<T>, U extends Comparable<U>> void preprocessNodes(List<CComponent<T, U>> cCompList, Class<T> klass, Class<U> qlass) {
        if (cCompList == null) {
            return;
        }

        //iterate over all nodes to lay out
        Set<CyNode> traversed = new HashSet<CyNode>(nodesToLayOut.size());
        for (View<CyNode> nv : nodesToLayOut) {
            CyNode seedNode = nv.getModel();

            if (!traversed.contains(seedNode)) {
                //the current node is the seed for a new connected component,
                //now collect all nodes in the new connected component by
                //traversing all nodes reachable from this node
                LinkedList<CyNode> toTraverse = new LinkedList<CyNode>();
                toTraverse.add(seedNode);
                //create a new connected component
                CComponent<T, U> ccomponent = new CComponent<T, U>();
                cCompList.add(ccomponent);
                //add seed node to connected compenent
                T group = network.getRow(seedNode).get(layoutAttribute, klass);
                U species = network.getRow(seedNode).get(speciesAttribute, qlass);
                ccomponent.addNode(seedNode, group, species);

                //find other reachable nodes
                while (!toTraverse.isEmpty()) {
                    CyNode reachableNode = toTraverse.removeFirst();
                    List<CyNode> neighbors = network.getNeighborList(reachableNode, CyEdge.Type.ANY);

                    for (CyNode nbNode : neighbors) {

                        if (!traversed.contains(nbNode) && nodesToLayOut.contains(networkView.getNodeView(nbNode))) {
                            traversed.add(nbNode);
                            toTraverse.add(nbNode);
                            //collect node info
                            group = network.getRow(nbNode).get(layoutAttribute, klass);
                            species = network.getRow(nbNode).get(speciesAttribute, qlass);
                            ccomponent.addNode(nbNode, group, species);
                        }
                    }
                }
            }
        }

        Collections.sort(cCompList);
    }

    private void addToSquare(CyNode node, double offsetx, double offsety) {

        networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, offsetx);
        networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, offsety);
    }

    private double encircle(List<CyNode> partition, double offsetx, double offsety, double enlargef) {
        if (partition == null) {
            return 0.0;
        }

        if (partition.size() == 1) {
            CyNode node = partition.get(0);
            networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, offsetx);
            networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, offsety);

            return 0.0;
        }

        double radius = context.radmult * Math.sqrt(partition.size()) * enlargef;

        if (radius < context.minrad) {
            radius = context.minrad;
        }

        double phidelta = (2.0 * Math.PI) / partition.size();
        double phi = 0.0;

        for (CyNode node : partition) {
            double x = offsetx + radius + (radius * Math.cos(phi));
            double y = offsety + radius + (radius * Math.sin(phi));
            networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
            networkView.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
            phi += phidelta;
        }

        return radius;
    }

    private double encirclebaits(double ccstarty, double offsety, List<List<CyNode>> baitsList) {

        double centerx = context.maxwidth / 2;
        double centery = (ccstarty - offsety) / 2;
        double ccphidelta = (2.0 * Math.PI) / baitsList.size();
        double ccphi = -Math.PI;

        double ccradius = (centerx > centery ? centerx : centery) * context.enlargef;

        for (List<CyNode> bg : baitsList) {

            double bgx = centerx + (ccradius * Math.cos(ccphi));
            double bgy = offsety + centery + (ccradius * Math.sin(ccphi));
            encircle(bg, bgx, bgy, context.benlargef);
            ccphi += ccphidelta;
        }

        return ccradius;
    }
}
