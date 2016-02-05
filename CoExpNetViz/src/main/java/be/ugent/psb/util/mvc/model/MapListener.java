package be.ugent.psb.util.mvc.model;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.MapDifference;

import java.util.Set;

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

public interface MapListener<K, V> {
	
	/**
	 * Notify listener after a map changed.
	 * 
	 * Each key will appear in at most one of entriesInserted, entriesChanged, entriesRemoved.
	 * Do not modify any of the passed arguments.
	 * 
	 * @param source The map that changed
	 * @param entriesInserted Entries that were inserted
	 * @param entriesChanged Entries that changed. `leftValue` refers to the original value, `rightValue` is the new value.
	 * @param entriesRemoved Entries that were removed.
	 */
	void mapChanged(Map<K,V> source, Map<K,V> entriesInserted, Map<K, MapDifference.ValueDifference<V>> entriesChanged, Map<K,V> entriesRemoved);

}
