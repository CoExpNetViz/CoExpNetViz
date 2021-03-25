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

import org.cytoscape.work.Tunable;

public class LayoutContext {
    
	@Tunable(description = "Space between adjacent connected components in the grid of connected components")
	public double connectedComponentSpacing = 800.0;
    
    @Tunable(description = "Minimum space between the baits partition and its surrounding partitions")
    public double baitPartitionSpacing = 400.0;
    
    @Tunable(description = "Space between adjacent nodes in a grid of a non-bait partition")
    public double nodeSpacing = 80.0;
    
    @Tunable(description = "Space between adjacent nodes in a circle layout of baits")
    public double baitNodeSpacing = 80.0;
    
}
