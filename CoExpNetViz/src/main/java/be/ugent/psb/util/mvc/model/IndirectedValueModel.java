package be.ugent.psb.util.mvc.model;

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
 * Encapsulate an indirection, presenting the result as the actual ValueModel
 */
public class IndirectedValueModel<V> implements ValueModel<V> {
	
	private ValueChangeListeners<V> listeners;
	private ValueModel<V> model;
	private ValueChangeListener<V> modelListener;
	
	public IndirectedValueModel(ValueModel<ValueModel<V>> indirectModel) {
		listeners = new ValueChangeListeners<>(this);
		
		// When current model changes, forward change event
		modelListener = new ValueChangeListener<V>() {
			@Override
			public void valueChanged(ValueModel<V> source, V oldValue) {
				listeners.fireValueChanged(oldValue);
			}
		};
		
		// When indirection changes, switch to new model
		indirectModel.addListener(new ValueChangeListener<ValueModel<V>>() {
			@Override
			public void valueChanged(ValueModel<ValueModel<V>> source, ValueModel<V> oldModel) {
				// Unregister old model and register to new one
				IndirectedValueModel.this.setModel(source.get());
				
				// Fire if value changed
				V oldValue = oldModel.get();
				if (model.get() != oldValue) {
					listeners.fireValueChanged(oldValue);
				}
			}
		});
		
		// Switch to initial model
		setModel(indirectModel.get());
	}
	
	private void setModel(ValueModel<V> model) {
		if (this.model != null) {
			this.model.removeListener(modelListener);
		}
		
		this.model = model;
		
		if (model != null) {
			model.addListener(modelListener);
		}
	}
	
	public void addListener(ValueChangeListener<V> l) {
		listeners.add(l);
	}
	
	public void removeListener(ValueChangeListener<V> l) {
		listeners.remove(l);
	}
	
	@Override
	public V get() {
		return model.get();
	}

	@Override
	public void set(V value) {
		model.set(value);
	}

}
