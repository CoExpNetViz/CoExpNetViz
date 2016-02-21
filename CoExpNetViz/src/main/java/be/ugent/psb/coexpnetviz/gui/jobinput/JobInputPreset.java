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

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.StringProperty;

/**
 * Preset that can be loaded into a job input form.
 * 
 * JobInputPreset and JobInputModel both represent a JobInput form state.
 * The difference is you can bind to a JobInputModel and serialise a 
 * JobInputPreset, but not vice versa. Additionally, a preset has a name.
 */
public class JobInputPreset {

	private String name;
    private BaitGroupSource baitGroupSource;
    private String baitGroupText;
    private String baitGroupPath;
    private List<String> expressionMatrixPaths;
    private GeneFamiliesSource geneFamiliesSource;
    private String geneFamiliesPath;
    private double lowerPercentile;
    private double upperPercentile;
    private String outputPath;
    private CorrelationMethod correlationMethod;
    
    /**
     * Do not use, this is meant for bean code (e.g. serialisation library)
     */
    public JobInputPreset() {
	}
    
    public JobInputPreset(String name, JobInputModel model) {
    	this.name = name;
    	baitGroupSource = model.getBaitGroupSource();
    	baitGroupText = model.getBaitGroupText();
    	baitGroupPath = model.getBaitGroupPath();
    	geneFamiliesSource = model.getGeneFamiliesSource();
    	geneFamiliesPath = model.getGeneFamiliesPath();
    	lowerPercentile = model.getLowerPercentile();
    	upperPercentile = model.getUpperPercentile();
    	outputPath = model.getOutputPath();
    	correlationMethod = model.getCorrelationMethod();
    	
    	expressionMatrixPaths = new ArrayList<>();
    	for (StringProperty pathProperty : model.getExpressionMatrixPaths()) {
    		expressionMatrixPaths.add(pathProperty.get());
    	}
    }
    
    public String getName() {
		return name;
	}
    
    public void setName(String name) {
		this.name = name;
	}
    
	public BaitGroupSource getBaitGroupSource() {
		return baitGroupSource;
	}
	
	public void setBaitGroupSource(BaitGroupSource baitGroupSource) {
		this.baitGroupSource = baitGroupSource;
	}
	
	public String getBaitGroupText() {
		return baitGroupText;
	}
	
	public void setBaitGroupText(String baitGroupText) {
		this.baitGroupText = baitGroupText;
	}
	
	public String getBaitGroupPath() {
		return baitGroupPath;
	}
	
	public void setBaitGroupPath(String baitGroupPath) {
		this.baitGroupPath = baitGroupPath;
	}
	
	public List<String> getExpressionMatrixPaths() {
		return expressionMatrixPaths;
	}
	
	public void setExpressionMatrixPaths(List<String> expressionMatrixPaths) {
		this.expressionMatrixPaths = expressionMatrixPaths;
	}
	
	public GeneFamiliesSource getGeneFamiliesSource() {
		return geneFamiliesSource;
	}
	
	public void setGeneFamiliesSource(GeneFamiliesSource geneFamiliesSource) {
		this.geneFamiliesSource = geneFamiliesSource;
	}
	
	public String getGeneFamiliesPath() {
		return geneFamiliesPath;
	}
	
	public void setGeneFamiliesPath(String geneFamiliesPath) {
		this.geneFamiliesPath = geneFamiliesPath;
	}
	
	public double getLowerPercentile() {
		return lowerPercentile;
	}
	
	public void setLowerPercentile(double lowerPercentile) {
		this.lowerPercentile = lowerPercentile;
	}
	
	public double getUpperPercentile() {
		return upperPercentile;
	}
	
	public void setUpperPercentile(double upperPercentile) {
		this.upperPercentile = upperPercentile;
	}
	
	public String getOutputPath() {
		return outputPath;
	}
	
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	public CorrelationMethod getCorrelationMethod() {
		return correlationMethod;
	}
	
	public void setCorrelationMethod(CorrelationMethod correlationMethod) {
		this.correlationMethod = correlationMethod;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
