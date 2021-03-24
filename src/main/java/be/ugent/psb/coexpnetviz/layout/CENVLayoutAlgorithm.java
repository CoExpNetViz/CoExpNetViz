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

package be.ugent.psb.coexpnetviz.layout;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

import be.ugent.psb.coexpnetviz.CENVContext;


public class CENVLayoutAlgorithm extends AbstractLayoutAlgorithm {
	
    public CENVLayoutAlgorithm(UndoSupport undoSupport) {
        super(CENVContext.NAMESPACE, CENVContext.APP_NAME, undoSupport);
    }

    public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String layoutAttribute) {
        return new TaskIterator(new CENVLayoutTask(networkView, nodesToLayOut, (CENVLayoutContext) context, undoSupport));
    }

    @Override
    public CENVLayoutContext createLayoutContext() {
        return new CENVLayoutContext();
    }

    @Override
    public boolean getSupportsSelectedOnly() {
        return false;
    }

}
