package be.ugent.psb.coexpnetviz.gui.model;

import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.swing.event.ListDataEvent;
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
public class ListDataListeners {
	
	private Set<ListDataListener> listeners;
	private Object source;
	
	public ListDataListeners(Object source) {
		this.source = source;
	}
	
	public void add(ListDataListener l) {
		listeners.add(l);
	}

	public void remove(ListDataListener l) {
		listeners.remove(l);
	}
	
	public void	fireContentsChanged(int index0, int index1) {
		for (ListDataListener listener : listeners) {
			listener.contentsChanged(new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1));
		}
	}
	
	public void	fireIntervalAdded(int index0, int index1) {
		for (ListDataListener listener : listeners) {
			listener.intervalAdded(new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1));
		}
	}
	
	public void	fireIntervalRemoved(int index0, int index1) {
		for (ListDataListener listener : listeners) {
			listener.intervalRemoved(new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1));
		}
	}

}
