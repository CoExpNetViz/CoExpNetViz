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

import be.ugent.psb.coexpnetviz.gui.controller.BrowseButtonHandler;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.BaitGroupSource;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.CorrelationMethod;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.GeneFamiliesSource;
import be.ugent.psb.util.TCCLRunnable;
import be.ugent.psb.util.javafx.BrowseButtonTableCell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Callback;
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
	
	@FXML
	private CardPane<GeneFamiliesSource> geneFamiliesCardPane;
	
	@FXML
	private FileInput geneFamiliesFileInput;
	
	@FXML
	private TableView<StringProperty> expressionMatricesTableView;
	
	@FXML
	private Button expressionMatricesAddButton;
	
	@FXML
	private Button expressionMatricesRemoveButton;
	
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
		
		geneFamiliesFileInput.setUserData(GeneFamiliesSource.CUSTOM);
    }
	
	public void init(final JobInputModel model, final Window window) {
		this.model = model;
		
		// Bait group
		baitGroupSourceGroup.valueProperty().bindBidirectional(model.baitGroupSourceProperty());
		baitGroupCardPane.shownCardDataProperty().bind(model.baitGroupSourceProperty());
		baitGroupTextArea.textProperty().bindBidirectional(model.baitGroupTextProperty());
		baitGroupFileInput.getTextField().textProperty().bindBidirectional(model.baitGroupPathProperty());
		baitGroupFileInput.getBrowseButton().setOnAction(new BrowseButtonHandler("Select bait group file", model.baitGroupPathProperty(), window));
		
		// Gene families
		geneFamiliesSourceGroup.valueProperty().bindBidirectional(model.geneFamiliesSourceProperty());
		geneFamiliesCardPane.shownCardDataProperty().bind(model.geneFamiliesSourceProperty());
		
		// Expression matrices: Add columns
		Callback<CellDataFeatures<StringProperty, String>, ObservableValue<String>> idCellValueFactory = new Callback<CellDataFeatures<StringProperty, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<StringProperty,String> cellData) {
				return cellData.getValue();
			};
		};
		
		TableColumn<StringProperty, String> pathColumn = new TableColumn<>("Path");
		pathColumn.setCellValueFactory(idCellValueFactory);
		pathColumn.setCellFactory(TextFieldTableCell.<StringProperty>forTableColumn());
		expressionMatricesTableView.getColumns().add(pathColumn);
		
		TableColumn<StringProperty, String> browseColumn = new TableColumn<>();
		browseColumn.setCellValueFactory(idCellValueFactory);
		browseColumn.setCellFactory(new Callback<TableColumn<StringProperty, String>, TableCell<StringProperty, String>>() {
			public TableCell<StringProperty, String> call(TableColumn<StringProperty, String> column) {
				return new BrowseButtonTableCell("Select gene expression matrix", window);
			};
		});
		expressionMatricesTableView.getColumns().add(browseColumn);
		
		// Expression matrices: Maximise first column
		pathColumn.prefWidthProperty().bind(expressionMatricesTableView.widthProperty().subtract(browseColumn.prefWidthProperty()).subtract(8)); // magic 8 is for the distance between columns
		
		// Expression matrices: Bind paths and buttons
		expressionMatricesTableView.setItems(model.getExpressionMatrixPaths());
		
		// Expression matrices: CRUD
		expressionMatricesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		expressionMatricesAddButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				model.getExpressionMatrixPaths().add(new SimpleStringProperty());
			};
		});
		expressionMatricesRemoveButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				model.getExpressionMatrixPaths().removeAll(expressionMatricesTableView.getSelectionModel().getSelectedItems());
			};
		});
		expressionMatricesRemoveButton.disableProperty().bind(Bindings.isEmpty(expressionMatricesTableView.getSelectionModel().getSelectedItems()));
		
		// Bind last bits
		correlationMethodGroup.valueProperty().bindBidirectional(model.correlationMethodProperty());
	}

}
