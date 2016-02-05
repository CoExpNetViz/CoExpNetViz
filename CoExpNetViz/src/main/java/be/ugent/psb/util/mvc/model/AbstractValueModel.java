package be.ugent.psb.util.mvc.model;

/**
 * Model of a value
 * 
 * Basically makes a value observable and provides it as a mutable object.
 * Observers are passed the last value as arg to update.
 */
public abstract class AbstractValueModel<V> implements ValueModel<V> {
	
	private ValueChangeListeners<V> listeners;
	
	public AbstractValueModel() {
		listeners = new ValueChangeListeners<>(this);
	}
	
	public void addListener(ValueChangeListener<V> l) {
		listeners.add(l);
	}
	
	public void removeListener(ValueChangeListener<V> l) {
		listeners.remove(l);
	}
	
	public void set(V value) {
		V oldValue = get();
		_set(value);
		if (!value.equals(oldValue)) {
			listeners.fireValueChanged(oldValue);
		}
	}
	
	abstract public V get();
	
	abstract protected void _set(V value);

}