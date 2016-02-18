package be.ugent.psb.coexpnetviz.gui.view;

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

import java.io.IOException;

import be.ugent.psb.util.TCCLRunnable;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

/**
 * File input control: a text field with a browse button
 */
public class FileInput extends HBox {
	
	public FileInput() {
		new TCCLRunnable() {
			protected void runInner() {
		        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("file_input.fxml"));
		        fxmlLoader.setRoot(FileInput.this);
		        fxmlLoader.setController(FileInput.this);

		        try {
		            fxmlLoader.load();
		        } catch (IOException exception) {
		            throw new RuntimeException(exception);
		        }
			};
		}.run();
    }

}
