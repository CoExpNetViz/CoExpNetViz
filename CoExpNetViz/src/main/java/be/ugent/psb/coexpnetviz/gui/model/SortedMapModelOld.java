package be.ugent.psb.coexpnetviz.gui.model;

import java.util.Map.Entry;
import java.util.SortedMap;

import javax.swing.ListModel;
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
 * A map whose list of entries can be observed
 */
public interface SortedMapModelOld<K, V> extends SortedMap<K, V>, ListModel<Entry<K,V>> {

	void addListDataListener(ListDataListener l);
	void removeListDataListener(ListDataListener l);
	Entry<K,V> getElementAt(int index);
	int indexOf(K key);
	
}
