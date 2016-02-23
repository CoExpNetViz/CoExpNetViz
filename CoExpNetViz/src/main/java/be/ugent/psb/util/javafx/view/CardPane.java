package be.ugent.psb.util.javafx.view;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;

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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Pane that only shows one of its 'cards'.
 *  
 * Similar to Swing's CardLayout. The cards property is exactly like 
 * the children property, i.e. a list of `Node`s to show, except that 
 * only one Card is ever shown (and thus is the only one in `children`).
 * If 2 cards' userData matches shownCardData, only the first is shown.
 * 
 * T: userData type
 */
@DefaultProperty("cards")
public class CardPane<T> extends Region {
	
	private ObjectProperty<T> shownCardData; // userData of the shown card
	private ListProperty<Node> cards;
	
	public CardPane() {
		cards = new SimpleListProperty<Node>();
		shownCardData = new SimpleObjectProperty<T>(null);
		shownCardData.addListener(new ChangeListener<T>() {
			@Override
			public void changed(ObservableValue<? extends T> arg0, T oldValue, T newValue) {
				getChildren().clear();
				for (Node card : cards) {
					Object data = card.getUserData();
					if (data != null && data.equals(shownCardData.get())) {
						getChildren().add(card);
						break;
					}
				}
			}
		});
    }
	
	public T getShownCardData() {
		return shownCardData.get();
	}
	
	public void setShownCardData(T shownChildData) {
		this.shownCardData.set(shownChildData);
	}
	
	public ObjectProperty<T> shownCardDataProperty() {
		return shownCardData;
	}
	
	public ObservableList<Node> getCards() {
		return cards.get();
	}
	
	public void setCards(ObservableList<Node> cards) {
		this.cards.set(cards);
	}
	
	public ListProperty<Node> cardsProperty() {
		return cards;
	}
	
	@Override
	protected void layoutChildren() {
		// Note: if this turns out a bit broken, use layoutInArea and refer to javafx source for an example as it's not really documented http://grepcode.com/file/repo1.maven.org/maven2/net.java.openjfx.backport/openjfx-78-backport/1.8.0-ea-b96.1/javafx/scene/layout/GridPane.java#GridPane.layoutChildren%28%29
		if (!getChildren().isEmpty()) {
			Node child = getChildren().get(0);
			child.resizeRelocate(0, 0, getWidth(), getHeight());
		}
	}

}
