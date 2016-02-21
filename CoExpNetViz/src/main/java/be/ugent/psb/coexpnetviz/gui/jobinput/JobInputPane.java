package be.ugent.psb.coexpnetviz.gui.jobinput;

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
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.ugent.psb.coexpnetviz.Context;
import be.ugent.psb.coexpnetviz.io.JobDescription;
import be.ugent.psb.util.TCCLRunnable;
import be.ugent.psb.util.ValidationException;
import be.ugent.psb.util.Validator;
import be.ugent.psb.util.javafx.BrowseButtonTableCell;
import be.ugent.psb.util.javafx.controller.BrowseButtonHandler;
import be.ugent.psb.util.javafx.view.CardPane;
import be.ugent.psb.util.javafx.view.FileInput;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import jfxtras.labs.scene.control.ToggleGroupValue;

/**
 * File input control: a text field with a browse button
 */
public class JobInputPane extends GridPane {
	
	private Context context;
	private ListProperty<JobInputPreset> presets;
	private JobInputModel model;
	private ToggleGroupValue<BaitGroupSource> baitGroupSourceGroup;
	private ToggleGroupValue<GeneFamiliesSource> geneFamiliesSourceGroup;
	private ToggleGroupValue<CorrelationMethod> correlationMethodGroup;
	
	@FXML
	private ComboBox<JobInputPreset> presetsComboBox;
	
	@FXML
	private Button loadPresetButton;
	
	@FXML
	private Button savePresetButton;
	
	@FXML
	private Button deletePresetButton;
	
	@FXML
	private Button resetFormButton;
	
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
	
	@FXML
	private TextField lowerPercentileRankInput;
	
	@FXML
	private TextField upperPercentileRankInput;
	
	@FXML
	private FileInput outputDirectoryFileInput;
	
	@FXML
	private Button runButton;
	
	@FXML
	private HBox errorTextArea;
	
	@FXML
	private Text errorText;
	
	public JobInputPane() {
		new TCCLRunnable() {
			protected void runInner() {
		        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("input_pane.fxml"));
		        fxmlLoader.setRoot(JobInputPane.this);
		        fxmlLoader.setController(JobInputPane.this);

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
	
	public void init(final Context context, final Window window) {
		this.model = new JobInputModel();
		
		// Presets
		presets = new SimpleListProperty<>(FXCollections.observableList(context.getPresets()));
		presetsComboBox.itemsProperty().bindBidirectional(presets);
		
		BooleanBinding hasPresetSelection = presetsComboBox.getSelectionModel().selectedItemProperty().isNull();
		loadPresetButton.disableProperty().bind(hasPresetSelection);
		deletePresetButton.disableProperty().bind(hasPresetSelection);
		
		loadPresetButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				model.assign(presetsComboBox.getSelectionModel().getSelectedItem());
			};
		});
		
		savePresetButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				String name = presetsComboBox.getEditor().getText();
				System.out.println("name: " + name);
				
				// validate: not empty
				try {
					Validator validator = new Validator();
					validator.setName("Preset name");
					name = validator.ensureIsNotEmpty(name);
				}
				catch (ValidationException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText(e.getMessage());
					alert.showAndWait();
					return;
				}
				
				// check whether already exists
				JobInputPreset existing = null;
				for (JobInputPreset preset : presets) {
					assert preset != null;
					if (preset.getName().equals(name)) {
						existing = preset;
						break;
					}
				}
				
				// need overwrite?
				JobInputPreset newPreset = new JobInputPreset(name, model);
				if (existing != null) {
					// confirm overwrite
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setContentText("A preset with this name already exists. Would you like to overwrite it?");
					ButtonType overwrite = new ButtonType("Overwrite");
					ButtonType cancel = new ButtonType("Cancel");
					alert.getButtonTypes().setAll(overwrite, cancel);
					if (alert.showAndWait().get() == cancel) {
						return;
					}
					
					// overwrite (replace)
					int i = presets.indexOf(existing);
					presets.remove(i);
					presets.add(i, newPreset);
				}
				else {
					// add new one
					presets.add(newPreset);
				}
				presetsComboBox.getSelectionModel().select(newPreset);
			};
		});
		
		deletePresetButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				presets.remove(presetsComboBox.getSelectionModel().getSelectedItem());
			};
		});
		
		// Bait group
		baitGroupSourceGroup.valueProperty().bindBidirectional(model.baitGroupSourceProperty());
		baitGroupCardPane.shownCardDataProperty().bind(model.baitGroupSourceProperty());
		baitGroupTextArea.textProperty().bindBidirectional(model.baitGroupTextProperty());
		baitGroupFileInput.getTextField().textProperty().bindBidirectional(model.baitGroupPathProperty());
		baitGroupFileInput.getBrowseButton().setOnAction(new BrowseButtonHandler("Select bait group file", model.baitGroupPathProperty(), window));
		
		// Gene families
		geneFamiliesSourceGroup.valueProperty().bindBidirectional(model.geneFamiliesSourceProperty());
		geneFamiliesCardPane.shownCardDataProperty().bind(model.geneFamiliesSourceProperty());
		geneFamiliesFileInput.getTextField().textProperty().bindBidirectional(model.geneFamiliesPathProperty());
		geneFamiliesFileInput.getBrowseButton().setOnAction(new BrowseButtonHandler("Select gene families file", model.geneFamiliesPathProperty(), window));
		
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
		
		// Bind output directory
		outputDirectoryFileInput.getTextField().textProperty().bindBidirectional(model.outputPathProperty());
		outputDirectoryFileInput.getBrowseButton().setOnAction(new BrowseButtonHandler("Select output directory file", model.outputPathProperty(), window));
		
		// Bind last bits
		NumberStringConverter doubleStringConverter  = new NumberStringConverter(new DecimalFormat());
		lowerPercentileRankInput.textProperty().bindBidirectional(model.lowerPercentileProperty(), doubleStringConverter);
		upperPercentileRankInput.textProperty().bindBidirectional(model.upperPercentileProperty(), doubleStringConverter);
		correlationMethodGroup.valueProperty().bindBidirectional(model.correlationMethodProperty());
		
		// Run button
		runButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				onRunButton();
			};
		});
	}
	
	private void onRunButton() {
		// Validate input and build a JobDescription, then run analysis
		// Note: in a brighter future we will only need to support Java 8 and this library becomes available with good validation widgets http://fxexperience.com/controlsfx/features/#decorationvalidation
		try {
			JobDescription jobDescription = new JobDescription();
			Validator validator = new Validator();
	        
	        // bait group source
	        jobDescription.setBaitGroupSource(model.getBaitGroupSource());
	        
	        if (model.getBaitGroupSource() == BaitGroupSource.FILE) {
	        	validator.setName("Bait group path");
	        	Path path = validator.ensurePath(model.getBaitGroupPath());
	        	validator.ensureIsRegularFile(path);
	        	validator.ensureReadable(path);
	            jobDescription.setBaitGroupPath(path);
	        }
	        else if (model.getBaitGroupSource() == BaitGroupSource.TEXT) {
	        	validator.setName("Bait group text");
	        	String text = validator.ensureIsNotEmpty(model.getBaitGroupText());
	        	jobDescription.setBaitGroupText(text);
	        }
	        else {
	        	assert false;
	        }
	        
	        // expression matrix paths
	        List<StringProperty> paths = model.getExpressionMatrixPaths();
	        Set<Path> paths_ = new HashSet<>();
	        for (int i=0; i < paths.size(); i++) {
	        	validator.setName("Expression matrix path at line " + i);
	        	Path path = validator.ensurePath(paths.get(i).get());
	        	validator.ensureIsRegularFile(path);
	        	validator.ensureReadable(path);
	        	paths_.add(path);
	        }
	        jobDescription.setExpressionMatrixPaths(paths_);
	        
	        // gene families
	        jobDescription.setGeneFamiliesSource(model.getGeneFamiliesSource());
	        if (model.getGeneFamiliesSource() == GeneFamiliesSource.CUSTOM) {
		        validator.setName("Custom gene families path");
	        	Path path = validator.ensurePath(model.getGeneFamiliesPath());
		    	validator.ensureIsRegularFile(path);
		    	validator.ensureReadable(path);
		    	jobDescription.setGeneFamiliesPath(path);
	        }
	        
	        // percentile thresholds
	        validator.setName("Lower percentile rank");
	        validator.ensureInRange(model.getLowerPercentile(), 0, 100);
	        
	        if (model.getLowerPercentile() > model.getUpperPercentile()) {
	        	throw new ValidationException("Lower percentile rank must be less than upper percentile rank");
	        }
	        
	        validator.setName("Lower percentile rank");
	        validator.ensureInRange(model.getUpperPercentile(), 0, 100);
	        
	        jobDescription.setLowerPercentile(model.getLowerPercentile());
	        jobDescription.setUpperPercentile(model.getUpperPercentile());
	        
	        // correlation method
	        jobDescription.setCorrelationMethod(model.getCorrelationMethod());
	        
	        // result path
	        validator.setName("Output path");
	        Path path = validator.ensurePath(model.getOutputPath());
	    	validator.ensureIsDirectory(path);
	    	validator.ensureReadable(path);
	    	
	        String networkName = Context.APP_NAME + "_" + Context.getTimeStamp(); // TODO could use preset name
	        jobDescription.setResultPath(path.resolve(networkName));
	        
	        // Valid input
	        errorTextArea.setVisible(false);
	        
	        // Run the analysis
	        new RunAnalysisTaskController(context, jobDescription, networkName);
		}
		catch (ValidationException ex) {
			errorText.setText(ex.getMessage());
			errorTextArea.setVisible(true);
		}
	}

}
