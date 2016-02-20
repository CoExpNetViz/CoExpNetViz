package be.ugent.psb.coexpnetviz.io;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 PSB/UGent
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

import java.nio.file.Path;
import java.util.Set;

import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.BaitGroupSource;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.CorrelationMethod;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel.GeneFamiliesSource;

/**
 * All a job server needs to know to run a job
 */
public class JobDescription {
	
	private BaitGroupSource baitGroupSource;
    private String baitGroupText;
    private Path baitGroupPath;
    private Set<Path> expressionMatrixPaths;
    private GeneFamiliesSource geneFamiliesSource;
    private Path geneFamiliesPath;
    private double lowerPercentile;
    private double upperPercentile;
    private CorrelationMethod correlationMethod;
    private Path resultPath;
    
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
	
	public Path getBaitGroupPath() {
		return baitGroupPath;
	}
	
	public void setBaitGroupPath(Path baitGroupPath) {
		this.baitGroupPath = baitGroupPath;
	}
	
	public Set<Path> getExpressionMatrixPaths() {
		return expressionMatrixPaths;
	}
	
	public void setExpressionMatrixPaths(Set<Path> expressionMatrixPaths) {
		this.expressionMatrixPaths = expressionMatrixPaths;
	}
	
	public GeneFamiliesSource getGeneFamiliesSource() {
		return geneFamiliesSource;
	}
	
	public void setGeneFamiliesSource(GeneFamiliesSource geneFamiliesSource) {
		this.geneFamiliesSource = geneFamiliesSource;
	}
	
	public Path getGeneFamiliesPath() {
		return geneFamiliesPath;
	}
	
	public void setGeneFamiliesPath(Path geneFamiliesPath) {
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
	
	public CorrelationMethod getCorrelationMethod() {
		return correlationMethod;
	}
	
	public void setCorrelationMethod(CorrelationMethod correlationMethod) {
		this.correlationMethod = correlationMethod;
	}
	
	public Path getResultPath() {
		return resultPath;
	}
	
	public void setResultPath(Path resultPath) {
		this.resultPath = resultPath;
	}
	
}
