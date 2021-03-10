package be.ugent.psb.coexpnetviz.layout;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2016 PSB/UGent
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

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Adapts a CyNode to a UIComponent
 */
public class Node extends UIComponent {
	
	private View<CyNode> node;
	private double x;
	private double y;

	public Node(View<CyNode> node) {
		this.node = node;
		x = 0;
		y = 0;
	}

	@Override
	void updateView(double parentX, double parentY) {
		node.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, parentX + x);
        node.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, parentY + y);
	}

	@Override
	double getWidth() {
		return node.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
	}

	@Override
	double getHeight() {
		return node.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
}
