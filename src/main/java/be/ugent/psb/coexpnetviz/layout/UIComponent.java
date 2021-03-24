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

public abstract class UIComponent {
	abstract double getX();
	abstract void setX(double x);
	abstract double getY();
	abstract void setY(double y);
	abstract double getWidth();
	abstract double getHeight();
	
	/**
	 * Commit modeled layout to actual view (i.e. update View<CyNode>)
	 * 
	 * @param parentX Global X position of parent
	 * @param parentY
	 */
	abstract void updateView(double parentX, double parentY);
	
	void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}

	double getArea() {
		return getWidth() * getHeight();
	}
	
	void setCenter(double x, double y) {
		setPosition(x - getWidth() / 2.0, y - getHeight() / 2.0);
	}
	
	double getDiameter() {
		return Math.sqrt(getWidth()*getWidth() + getHeight()*getHeight());
	}
	
}
