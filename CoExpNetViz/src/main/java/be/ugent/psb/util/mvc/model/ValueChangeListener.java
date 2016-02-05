package be.ugent.psb.util.mvc.model;

public interface ValueChangeListener<E> {
	
	/**
	 * Notify after value changed
	 * 
	 * @param source Model whose value changed
	 * @param oldValue The old value, do not modify this.
	 */
	void valueChanged(ValueModel<E> source, E oldValue);
}