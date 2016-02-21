package be.ugent.psb.coexpnetviz.gui.jobinput;

import java.util.ArrayList;

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

import be.ugent.psb.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * State of a job input form
 */
public class JobInputModel implements Cloneable {

    private ObjectProperty<BaitGroupSource> baitGroupSource;
    private StringProperty baitGroupText;
    private StringProperty baitGroupPath;
    private ListProperty<StringProperty> expressionMatrixPaths;
    private ObjectProperty<GeneFamiliesSource> geneFamiliesSource;
    private StringProperty geneFamiliesPath;
    private DoubleProperty lowerPercentile;
    private DoubleProperty upperPercentile;
    private BooleanProperty saveOutput;
    private StringProperty outputPath;
    private ObjectProperty<CorrelationMethod> correlationMethod;

    public JobInputModel() {
        baitGroupSource = new SimpleObjectProperty<>(BaitGroupSource.FILE);
        baitGroupText = new SimpleStringProperty("");
        baitGroupPath = new SimpleStringProperty("");
        expressionMatrixPaths = new SimpleListProperty<>(javafx.collections.FXCollections.observableList(new ArrayList<StringProperty>()));
        geneFamiliesSource = new SimpleObjectProperty<>(GeneFamiliesSource.PLAZA);
        geneFamiliesPath = new SimpleStringProperty("");
        lowerPercentile = new SimpleDoubleProperty(5);
        upperPercentile = new SimpleDoubleProperty(95);
        saveOutput = new SimpleBooleanProperty(false);
        outputPath = new SimpleStringProperty(System.getProperty("user.home"));
        correlationMethod = new SimpleObjectProperty<>(CorrelationMethod.PEARSON);
    }

    public Object clone() throws CloneNotSupportedException {
    	// TODO clone deeply
    	JobInputModel clone_ = Objects.clone(this);
//    	clone_.expressionMatrixPaths = Objects.clone(expressionMatrixPaths);
    	return clone_;
    }

    // TODO old
//    public void setSpeciesEntry(SpeciesEntryModel sem, SpeciesEntryPanel se) {
//        species.put(sem, se);
//        setChanged();
//        notifyObservers();
//    }
//
//    public void addSpecies(SpeciesEntryModel sem, SpeciesEntryPanel se) {
//        if (!species.containsKey(sem)) {
//            species.put(sem, se);
//            setChanged();
//            notifyObservers();
//        }
//    }
//
//    public void removeSpecies(SpeciesEntryModel sem) {
//        if (species.containsKey(sem)) {
//            species.remove(sem);
//            setChanged();
//            notifyObservers();
//        }
//    }

	public BaitGroupSource getBaitGroupSource() {
		return baitGroupSource.get();
	}

	public void setBaitGroupSource(BaitGroupSource baitGroupSource) {
		this.baitGroupSource.set(baitGroupSource);
	}
	
	public ObjectProperty<BaitGroupSource> baitGroupSourceProperty() {
		return baitGroupSource;
	}

	public String getBaitGroupText() {
		return baitGroupText.get();
	}

	public void setBaitGroupText(String baitGroupText) {
		this.baitGroupText.set(baitGroupText);
	}
	
	public StringProperty baitGroupTextProperty() {
		return baitGroupText;
	}

	public ObservableList<StringProperty> getExpressionMatrixPaths() {
		return expressionMatrixPaths.get();
	}

	public void setExpressionMatrixPaths(ObservableList<StringProperty> expressionMatrixPaths) {
		this.expressionMatrixPaths.set(expressionMatrixPaths);
	}

	public ListProperty<StringProperty> expressionMatrixPathsProperty() {
		return expressionMatrixPaths;
	}
	
	public GeneFamiliesSource getGeneFamiliesSource() {
		return geneFamiliesSource.get();
	}

	public void setGeneFamiliesSource(GeneFamiliesSource geneFamiliesSource) {
		this.geneFamiliesSource.set(geneFamiliesSource);
	}
	
	public ObjectProperty<GeneFamiliesSource> geneFamiliesSourceProperty() {
		return geneFamiliesSource;
	}

	public String getGeneFamiliesPath() {
		return geneFamiliesPath.get();
	}

	public void setGeneFamiliesPath(String geneFamiliesPath) {
		this.geneFamiliesPath.set(geneFamiliesPath);
	}
	
	public StringProperty geneFamiliesPathProperty() {
		return geneFamiliesPath;
	}

	public double getLowerPercentile() {
		return lowerPercentile.get();
	}

	public void setLowerPercentile(double lowerPercentile) {
		this.lowerPercentile.set(lowerPercentile);
	}
	
	public DoubleProperty lowerPercentileProperty() {
		return lowerPercentile;
	}

	public double getUpperPercentile() {
		return upperPercentile.get();
	}

	public void setUpperPercentile(double upperPercentile) {
		this.upperPercentile.set(upperPercentile);
	}
	
	public DoubleProperty upperPercentileProperty() {
		return upperPercentile;
	}

	public boolean getSaveOutput() {
		return saveOutput.get();
	}

	public void setSaveOutput(boolean saveOutput) {
		this.saveOutput.set(saveOutput);
	}
	
	public BooleanProperty saveOutputProperty() {
		return saveOutput;
	}

	public String getOutputPath() {
		return outputPath.get();
	}

	public void setOutputPath(String outputPath) {
		this.outputPath.set(outputPath);
	}
	
	public StringProperty outputPathProperty() {
		return outputPath;
	}

	public CorrelationMethod getCorrelationMethod() {
		return correlationMethod.get();
	}

	public void setCorrelationMethod(CorrelationMethod correlationMethod) {
		this.correlationMethod.set(correlationMethod);
	}
	
	public ObjectProperty<CorrelationMethod> correlationMethodProperty() {
		return correlationMethod;
	}
	
	public String getBaitGroupPath() {
		return baitGroupPath.get();
	}

	public void setBaitGroupPath(String baitGroupPath) {
		this.baitGroupPath.set(baitGroupPath);
	}
	
	public StringProperty baitGroupPathProperty() {
		return baitGroupPath;
	}

	public static enum BaitGroupSource {
    	FILE, TEXT
    }
    
    public static enum GeneFamiliesSource {
    	PLAZA, CUSTOM, NONE
    }
    
    public static enum CorrelationMethod {
    	PEARSON, MUTUAL_INFORMATION
    }

}
