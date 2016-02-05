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

public class MapListeners<K, V> {
	
	private Set<MapListener<K,V>> listeners;
	private Map<K, V> source;
	
	public MapListeners(Map<K,V> source) {
		this.source = source;
	}
	
	public void add(MapListener<K,V> l) {
		listeners.add(l);
	}

	public void remove(MapListener<K,V> l) {
		listeners.remove(l);
	}
	
	public void	fireChanged(Map<K,V> entriesInserted, Map<K, MapDifference.ValueDifference<V>> entriesChanged, Map<K,V> entriesRemoved) {
		for (MapListener<K,V> listener : listeners) {
			listener.mapChanged(source, entriesInserted, entriesChanged, entriesRemoved);
		}
	}

}
