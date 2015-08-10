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
import java.util.HashMap;
import java.util.Map;

/**
 * All a job server needs to know to run a job
 */
public class JobDescription {
	
	private boolean sendBaitsAsFile; // whether to send baits as a file or directly
	private String baits; // valid iff baitsFromFile is false
    private Path baitsFilePath; // valid iff baitsFromFile is true
    
    private Map<String, Path> expressionMatrices; // names of matrices
    private double negativeCutoff;
    private double positiveCutoff;
    private Path saveFilePath;
    private Map<String, Path> geneFamilies; // Name -> Path of each ortholog-families file
    
    public JobDescription() {
		expressionMatrices = new HashMap<String, Path>();
		geneFamilies = new HashMap<String, Path>();
	}
    
	public String getBaits() {
		assert(!sendBaitsAsFile);
		return baits;
	}
	
	public void setBaits(String baits) {
		sendBaitsAsFile = false;
		this.baits = baits;
	}
	
	public Path getBaitsFilePath() {
		assert(sendBaitsAsFile);
		return baitsFilePath;
	}
	
	public void setBaitsFilePath(Path baitsFilePath) {
		sendBaitsAsFile = true;
		this.baitsFilePath = baitsFilePath;
	}
	
	public Map<String, Path> getExpressionMatrices() {
		return expressionMatrices;
	}

	public void setExpressionMatrices(Map<String, Path> expressionMatrices) {
		this.expressionMatrices = expressionMatrices;
	}

	public double getNegativeCutoff() {
		return negativeCutoff;
	}
	
	public void setNegativeCutoff(double negativeCutoff) {
		this.negativeCutoff = negativeCutoff;
	}
	
	public double getPositiveCutoff() {
		return positiveCutoff;
	}
	
	public void setPositiveCutoff(double positiveCutoff) {
		this.positiveCutoff = positiveCutoff;
	}
	
	public Path getSaveFilePath() {
		return saveFilePath;
	}
	
	public void setSaveFilePath(Path saveFilePath) {
		this.saveFilePath = saveFilePath;
	}
	
	public Map<String, Path> getGeneFamilies() {
		return geneFamilies;
	}
	
	public void setGeneFamilies(Map<String, Path> geneFamilies) {
		this.geneFamilies = geneFamilies;
	}

	public boolean isSendBaitsAsFile() {
		return sendBaitsAsFile;
	}
	
}
