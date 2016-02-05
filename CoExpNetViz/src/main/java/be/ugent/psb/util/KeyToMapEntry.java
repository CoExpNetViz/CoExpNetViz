package be.ugent.psb.util;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.Transformer;

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

// TODO rm me
/**
 * Transform a key to a Map.Entry (not of the actual map)
 */
class KeyToMapEntry<K, V> implements Transformer<K, Entry<K, V>> {

	private Map<K,V> map;
	
	public KeyToMapEntry(Map<K, V> map) {
		this.map = map;
	}

	@Override
	public Entry<K, V> transform(K key) {
		return new DefaultMapEntry<K, V>(key, map.get(key));
	}

}
