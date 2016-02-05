package be.ugent.psb.util.awt.event;

import java.awt.event.ItemEvent;

public abstract class ItemEvents {
	public static <E> E getItem(ItemEvent e) {
		return (E)e.getItem();
	}
}
