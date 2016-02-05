package be.ugent.psb.util.mvc.model;

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

/**
 * Model of a value
 * 
 * Basically makes a value observable and provides it as a mutable object.
 * Observers are passed the last value as arg to update.
 * 
 * Basically a Python property with a value changed callback.
 */
public interface ValueModel<V> {
	void addListener(ValueChangeListener<V> l);
	void removeListener(ValueChangeListener<V> l);
	void set(V value);
	V get();
}