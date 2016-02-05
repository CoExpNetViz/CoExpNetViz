package be.ugent.psb.util.mvc.model;

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