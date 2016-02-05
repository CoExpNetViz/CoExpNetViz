package be.ugent.psb.util.mvc.model;

import java.util.HashSet;
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

public class ValueChangeListeners<V> {
	
	private Set<ValueChangeListener<V>> listeners;
	private ValueModel<V> source;
	
	public ValueChangeListeners(ValueModel<V> source) {
		listeners = new HashSet<>();
		this.source = source;
	}
	
	public void add(ValueChangeListener<V> l) {
		listeners.add(l);
	}

	public void remove(ValueChangeListener<V> l) {
		listeners.remove(l);
	}
	
	public void fireValueChanged(V oldValue) {
		for (ValueChangeListener<V> listener : listeners) {
			listener.valueChanged(source, oldValue);
		}
	}

}
