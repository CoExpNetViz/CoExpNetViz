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

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class FamLayout extends AbstractLayoutAlgorithm {

	public static final String NAME = "fam-layout";
    public static final String HUMAN_FRIENDLY_NAME = "Family node Layout";
    
    /**
     * Creates a new GroupAttributesLayout object.
     * @param undo
     */
    public FamLayout(UndoSupport undo) {
        super(NAME, HUMAN_FRIENDLY_NAME, undo);
    }

    public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String attrName) {
        return new TaskIterator(new FamLayoutTask(toString(), networkView, nodesToLayOut, (FamLayoutContext) context, attrName, null, undoSupport));
    }

    public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String attrName, String baitName) {
        return new TaskIterator(new FamLayoutTask(toString(), networkView, nodesToLayOut, (FamLayoutContext) context, attrName, baitName, undoSupport));
    }

    @Override
    public Set<Class<?>> getSupportedNodeAttributeTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();

        ret.add(Integer.class);
        ret.add(Double.class);
        ret.add(String.class);
        ret.add(Boolean.class);

        return ret;
    }

    @Override
    public FamLayoutContext createLayoutContext() {
        return new FamLayoutContext();
    }

    @Override
    public boolean getSupportsSelectedOnly() {
        return true;
    }

}
