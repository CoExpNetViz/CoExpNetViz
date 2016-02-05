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
 * Transforms a ValueModel
 */
public abstract class TransformedValueModel<From, To> implements ValueModel<To> {
	
	private ValueModel<From> delegate;
	private ValueChangeListeners<To> listeners;
	
	public TransformedValueModel(ValueModel<From> model) {
		listeners = new ValueChangeListeners<>(this);
		model.addListener(new ValueChangeListener<From>() {
			@Override
			public void valueChanged(ValueModel<From> source, From oldValue) {
				listeners.fireValueChanged(transform(oldValue));
			}
		});
	}
	
	public void addListener(ValueChangeListener<To> l) {
		listeners.add(l);
	}
	
	public void removeListener(ValueChangeListener<To> l) {
		listeners.remove(l);
	}
	
	public To get() {
		return transform(delegate.get());
	}
	
	public void set(To value) {
		delegate.set(transformInverse(value));
	}
	
	protected abstract To transform(From value);
	protected abstract From transformInverse(To value);

}
