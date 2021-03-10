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

import org.cytoscape.work.Tunable;

public class CENVLayoutContext {
    
	@Tunable(description = "Space between adjacent connected components in the grid of connected components")
	public double connectedComponentSpacing = 800.0;
    
    @Tunable(description = "Space between adjacent partitions in a circle layout of partitions")
    public double partitionSpacing = 400.0;
    
    @Tunable(description = "Minimum space between a baits partition and its surrounding family partitions")
    public double baitToFamilyPartitionSpacing = 400.0;
    
    @Tunable(description = "Space between adjacent family nodes in a grid of a partition of family nodes")
    public double familyNodeSpacing = 80.0;
    
    @Tunable(description = "Space between adjacent bait nodes in a circle layout of baits")
    public double baitNodeSpacing = 80.0;
    
}
