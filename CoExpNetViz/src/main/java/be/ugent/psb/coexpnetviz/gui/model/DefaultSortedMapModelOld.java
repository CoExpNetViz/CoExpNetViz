package be.ugent.psb.coexpnetviz.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.event.ListDataListener;

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

// TODO rm
/**
 * Wrapper to make a sorted map an observable model
 */
public class DefaultSortedMapModelOld<K, V> implements SortedMapModel<K, V> {

	private SortedMap<K, V> map;
	private ListDataListeners entryListListeners;
	
	public DefaultSortedMapModelOld(SortedMap<K, V> map) {
		this.map = map;
		entryListListeners = new ListDataListeners(this);
	}
	
	public Comparator<? super K> comparator() {
		return map.comparator();
	}

	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return map.subMap(fromKey, toKey);
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

	public SortedMap<K, V> headMap(K toKey) {
		return map.headMap(toKey);
	}

	public V get(Object key) {
		return map.get(key);
	}

	public SortedMap<K, V> tailMap(K fromKey) {
		return map.tailMap(fromKey);
	}

	public V put(K key, V value) {
		boolean hasKey = map.containsKey(key);
		V putResult = map.put(key, value);
		
		int index = new ArrayList<>(map.keySet()).indexOf(key);
		if (hasKey) {
			if (value != putResult) {
				entryListListeners.fireContentsChanged(index, index);
			}
		}
		else {
			entryListListeners.fireIntervalAdded(index, index);
		}
		
		return putResult;
	}

	public K firstKey() {
		return map.firstKey();
	}

	public K lastKey() {
		return map.lastKey();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public V remove(Object key) {
		if (map.containsKey(key)) {
			int index = new ArrayList<>(map.keySet()).indexOf(key);
			V removeResult = map.remove(key);
			entryListListeners.fireIntervalRemoved(index, index);
			return removeResult;
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
		List<K> originalKeys = new ArrayList<>(map.keySet());
		map.putAll(m);
		List<K> keys = new ArrayList<>(map.keySet());
		
		// generate index intervals missing in `keys` indexed by `keys`
		// and immediately use these intervals to fire the relevant events
		boolean intervalStarted = false;
		int startIndex = 0;
		int i=0;
		for (; i<keys.size(); i++) {
			if (!originalKeys.contains(keys.get(i))) {
				if (!intervalStarted) {
					intervalStarted = true;
					startIndex = i;
				}
			}
			else if (intervalStarted) {
				intervalStarted = false;
				entryListListeners.fireIntervalAdded(startIndex, i-1);
			}
		}
		if (intervalStarted) {
			entryListListeners.fireIntervalAdded(startIndex, i-1);
		}
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
	public void addListDataListener(ListDataListener l) {
		entryListListeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		entryListListeners.remove(l);
	}
	
	@Override
	public Entry<K, V> getElementAt(int index) {
		return new ArrayList<>(map.entrySet()).get(index);  // oh boy, so efficient
	}
	
	@Override
	public int getSize() {
		return size();
	}
	
	public int indexOf(K key) {
		return new ArrayList<>(map.entrySet()).indexOf(key);
	}

}
