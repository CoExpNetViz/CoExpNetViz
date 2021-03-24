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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Container extends UIComponent {
	
	private List<UIComponent> children;
	private double x;
	private double y;
	
	public Container() {
		children = new ArrayList<>();
		x = 0;
		y = 0;
	}
	
	public void add(UIComponent child) {
		children.add(child);
	}
	
	/**
     * Sort children by geometric area
     */
    public void sortByArea() {
    	Collections.sort(children, new Comparator<UIComponent>() {
    		@Override
    		public int compare(UIComponent o1, UIComponent o2) {
    			return Double.compare(o1.getArea(), o2.getArea());
    		}
		});
    }
    
    /**
     * Lay out children in a grid
     */
    public void layOutInGrid(double spacing) {
    	int columns = (int)Math.ceil(Math.sqrt(children.size()));
    	int rows = columns;
    	
    	// Determine column and row sizes
    	List<Double> rowSizes = new ArrayList<>(Collections.nCopies(rows, 0.0));
    	List<Double> columnSizes = new ArrayList<>(Collections.nCopies(rows, 0.0));
    	for (int i=0; i < children.size(); i++) {
    		int row = i / columns;
    		int column = i % columns;
    		UIComponent child = children.get(i);
    		rowSizes.set(row, Math.max(rowSizes.get(row), child.getHeight()));
    		columnSizes.set(column, Math.max(columnSizes.get(column), child.getWidth()));
    	}
    	
    	// Get left and top of column and rows respectively
    	List<Double> rowTops = new ArrayList<>();
    	List<Double> columnLefts = new ArrayList<>();
    	rowTops.add(0.0);
    	columnLefts.add(0.0);
    	for (int i=0; i < rowSizes.size()-1; i++) {
    		rowTops.add(spacing + rowTops.get(i) + rowSizes.get(i));
    	}
    	for (int i=0; i < columnSizes.size()-1; i++) {
    		columnLefts.add(spacing + columnLefts.get(i) + columnSizes.get(i));
    	}
    	
    	// Lay out children
    	for (int i=0; i < children.size(); i++) {
    		int row = i / columns;
    		int column = i % columns;
    		children.get(i).setPosition(columnLefts.get(column), rowTops.get(row));
    	}
    }
    
    /**
     * Lay out children in a circle
     * 
     * @param minRadius No child shall overlap with the inner circle with this radius
     */
    public void layOutInCircle(double spacing, double minRadius) {
    	// Determine circumference
    	double circumference = 0.0;
    	double maxDiameter = 0.0;
    	for (UIComponent child : children) {
    		circumference += child.getDiameter();
    		maxDiameter = Math.max(child.getDiameter(), maxDiameter);
    	}
    	circumference += spacing * (children.size()-1);
    	
    	// Determine radius
    	double radius = Math.max(circumference / 2.0 / Math.PI, minRadius + maxDiameter / 2.0);
    	
    	// Lay out children
    	double angle = 0.0;
    	double spacingPerChild = spacing * (children.size()-1) / children.size();
    	for (UIComponent child : children) {
    		double angularShare = (child.getDiameter() + spacingPerChild) / circumference * 2.0 * Math.PI; // how much radians will we use to fit this child
    		angle += angularShare / 2.0;
    		child.setCenter(radius + radius * Math.cos(angle), radius + radius * Math.sin(angle)); // +radius to keep things positive
    		angle += angularShare / 2.0;
    	}
    }

	@Override
	double getWidth() {
		double max = 0.0;
		for (UIComponent child : children) {
			max = Math.max(max, child.getX() + child.getWidth());
		}
		return max;
	}

	@Override
	double getHeight() {
		double max = 0.0;
		for (UIComponent child : children) {
			max = Math.max(max, child.getY() + child.getHeight());
		}
		return max;
	}

	@Override
	void updateView(double parentX, double parentY) {
		for (UIComponent child : children) {
			child.updateView(parentX + x, parentY + y);
		}
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
