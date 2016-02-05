package be.ugent.psb.util.mvc.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import be.ugent.psb.util.DefaultMapValueDifference;

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

/**
 * Wrapper to make a sorted map an observable model
 */
public class DefaultMapModel<K, V> implements MapModel<K, V> {

	private Map<K, V> map;
	private MapListeners<K,V> listeners;
	
	public DefaultMapModel(Map<K, V> map) {
		this.map = map;
		listeners = new MapListeners<>(this);
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	
	public V get(Object key) {
		return map.get(key);
	}

	public V put(K key, V value) {
		boolean hasKey = map.containsKey(key);
		V putResult = map.put(key, value);
		
		if (hasKey) {
			if (value != putResult) {
				Map<K, MapDifference.ValueDifference<V>> changed = new HashMap<>();
				changed.put(key, new DefaultMapValueDifference<V>(putResult, value));
				listeners.fireChanged(Collections.<K, V>emptyMap(), changed, Collections.<K, V>emptyMap());
			}
		}
		else {
			Map<K, V> inserted = new HashMap<>();
			inserted.put(key, value);
			listeners.fireChanged(inserted, Collections.<K, MapDifference.ValueDifference<V>>emptyMap(), Collections.<K, V>emptyMap());
		}
		
		return putResult;
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	public V remove(Object key_) {
		K key = (K)key_;
		if (map.containsKey(key)) {
			V value = map.remove(key);
			Map<K,V> removed = new HashMap<K, V>();
			removed.put(key, value);
			listeners.fireChanged(Collections.<K, V>emptyMap(), Collections.<K, MapDifference.ValueDifference<V>>emptyMap(), removed);
			return value;
		}
		else {
			return null;
		}
	}

	public Collection<V> values() {
		return map.values();
	}

	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		MapDifference<K, V> difference = Maps.difference(map, m);
		map.putAll(m);
		listeners.fireChanged(difference.entriesOnlyOnRight(), difference.entriesDiffering(), Collections.<K, V>emptyMap());
	}

	public void clear() {
		map.clear();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public void addListener(MapListener<K,V> l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(MapListener<K,V> l) {
		listeners.remove(l);
	}

}
