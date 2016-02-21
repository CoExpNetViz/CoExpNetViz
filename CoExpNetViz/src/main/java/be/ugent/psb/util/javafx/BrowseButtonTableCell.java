package be.ugent.psb.util.javafx;

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

import be.ugent.psb.util.javafx.controller.BrowseButtonHandler;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Window;

/**
 * A browse button in a table cell for a table view using simply a list of paths as model
 */
public class BrowseButtonTableCell extends TableCell<StringProperty, String> { // Note: using a callback or such we could support e.g. a bean model and fetch the StringProperty on it using BrowseButtonTableCell<T> and Callback<T, StringProperty> pathPropertyGetter
	
	private String browseDialogTitle;
	private Window window;
	private Button button;
	private BrowseButtonHandler buttonHandler;
	
	public BrowseButtonTableCell(String browseDialogTitle, Window window) {
		this.browseDialogTitle = browseDialogTitle;
		this.window = window;
		button = new Button("Browse");
		buttonHandler = new BrowseButtonHandler(browseDialogTitle, window);
		button.setOnAction(buttonHandler);
		indexProperty().addListener(new ChangeListener<Number>() {
			public void changed(javafx.beans.value.ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
				StringProperty pathProperty = (StringProperty)getTableColumn().getCellObservableValue(getIndex());
				buttonHandler.setPathProperty(pathProperty);
			};
		});
	} 
	
	@Override
    protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		
		setText(null);
		if(empty) {	    
		    setGraphic(null);
		} else {
		    this.setGraphic(button);
		}
		
    }
}
