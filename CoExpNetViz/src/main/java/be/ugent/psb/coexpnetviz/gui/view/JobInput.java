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

import be.ugent.psb.coexpnetviz.gui.model.JobInputModel;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.BaitGroupSource;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.CorrelationMethod;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.GeneFamiliesSource;
import be.ugent.psb.util.TCCLRunnable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import jfxtras.labs.scene.control.ToggleGroupValue;

/**
 * File input control: a text field with a browse button
 */
public class JobInput extends GridPane {
	
	private JobInputModel model;
	private ToggleGroupValue<JobInputModel.BaitGroupSource> baitGroupSourceGroup;
	private ToggleGroupValue<JobInputModel.GeneFamiliesSource> geneFamiliesSourceGroup;
	private ToggleGroupValue<JobInputModel.CorrelationMethod> correlationMethodGroup;
	
	@FXML
	private RadioButton radioBaitGroupSourceText;
	
	@FXML
	private RadioButton radioBaitGroupSourceFile;
	
	@FXML
	private RadioButton radioGeneFamiliesSourcePlaza;
	
	@FXML
	private RadioButton radioGeneFamiliesSourceCustom;
	
	@FXML
	private RadioButton radioGeneFamiliesSourceNone;
	
	@FXML
	private RadioButton radioCorrelationMethodPearson;
	
	@FXML
	private RadioButton radioCorrelationMethodMutualInformation;
	
	@FXML
	private CardPane<BaitGroupSource> baitGroupCardPane;
	
	@FXML
	private TextArea baitGroupTextArea;
	
	@FXML
	private FileInput baitGroupFileInput;
	
	public JobInput() {
		new TCCLRunnable() {
			protected void runInner() {
		        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("input_pane.fxml"));
		        fxmlLoader.setRoot(JobInput.this);
		        fxmlLoader.setController(JobInput.this);

		        try {
		            fxmlLoader.load();
		        } catch (IOException exception) {
		            throw new RuntimeException(exception);
		        }
			};
		}.run();
		
		// Create radio groups
		baitGroupSourceGroup = new ToggleGroupValue<>();
		baitGroupSourceGroup.add(radioBaitGroupSourceText, BaitGroupSource.TEXT);
		baitGroupSourceGroup.add(radioBaitGroupSourceFile, BaitGroupSource.FILE);
		
		geneFamiliesSourceGroup = new ToggleGroupValue<>();
		geneFamiliesSourceGroup.add(radioGeneFamiliesSourcePlaza, GeneFamiliesSource.PLAZA);
		geneFamiliesSourceGroup.add(radioGeneFamiliesSourceCustom, GeneFamiliesSource.CUSTOM);
		geneFamiliesSourceGroup.add(radioGeneFamiliesSourceNone, GeneFamiliesSource.NONE);
		
		correlationMethodGroup = new ToggleGroupValue<>();
		correlationMethodGroup.add(radioCorrelationMethodPearson, CorrelationMethod.PEARSON);
		correlationMethodGroup.add(radioCorrelationMethodMutualInformation, CorrelationMethod.MUTUAL_INFORMATION);
		
		// User data
		baitGroupTextArea.setUserData(BaitGroupSource.TEXT);
		baitGroupFileInput.setUserData(BaitGroupSource.FILE);
    }
	
	public void init(JobInputModel model) {
		this.model = model;
		
		// Bind radio groups
		baitGroupSourceGroup.valueProperty().bindBidirectional(model.getBaitGroupSourceProperty());
		geneFamiliesSourceGroup.valueProperty().bindBidirectional(model.getGeneFamiliesSourceProperty());
		correlationMethodGroup.valueProperty().bindBidirectional(model.getCorrelationMethodProperty());
		
		// Bind other bits
		baitGroupCardPane.shownCardDataProperty().bind(model.getBaitGroupSourceProperty());
	}

}
