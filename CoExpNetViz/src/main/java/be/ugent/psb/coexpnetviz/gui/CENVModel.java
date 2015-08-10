package be.ugent.psb.coexpnetviz.gui;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;

import be.ugent.psb.coexpnetviz.io.JobDescription;

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

/**
 * Data class: some constants, global state that perhaps could be made less global
 */
public class CENVModel extends Observable {

    //which column contains the gene families
    public static final int FAMILIES_COLUMN = 0;
    //which column contains the genes
    public static final int GENES_COLUMN = 1;
    //which column contains the species of the baits
    public static final int SPECIES_COLUMN = 2;
    //Which column to use to group nodes
    public static final int GROUP_COLUMN = 3;
    //what value indicates a node is a bait in the SPECIES_COLUMN
    public static final String BAIT_GROUP = "#FFFFFF";
    //the name to use for the new layout created by this app
    public static final String COMP_LAYOUT_NAME = "fam-layout";
    public static final String HUMAN_LAYOUT_NAME = "Family node Layout";
    //status messages for the analysis task
    public static final String PROG_LAYT = "Applying " + HUMAN_LAYOUT_NAME;
    public static final double PROG_CONN_COMPLETE = 0.8;
    public static final double PROG_NETW_COMPLETE = 0.9;

    /*--------------------------------------------------------------------------
     The fields below represent all the information that is required to execute
     one analysis (one RunAnalysisTask)
     */
    //title of the current analysis
    private String title;
    
    private JobDescription jobDescription; 
    
    //Node table for last created network
    //this is set by CreateNetworkTask and used by ShowNetworkTask
    private CyTable lastNoaTable;
    //Network view for last created network
    //this is set by CreateNetworkTask and used by ShowNetworkTask
    private CyNetworkView lastCnv;

    /*--------------------------------------------------------------------------
     Fields to keep track of application state
     */
    private Path settingsPath;
    private HashSet<CyNetwork> visibleCevNetworks = new HashSet<CyNetwork>();

    public CENVModel() {
		jobDescription = new JobDescription();
	}
    
    /*--------------------------------------------------------------------------
     Getters and setters
     */
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the lastNoaTable
     */
    public CyTable getLastNoaTable() {
        return lastNoaTable;
    }

    /**
     * @param lastNoaTable the lastNoaTable to set
     */
    public void setLastNoaTable(CyTable lastNoaTable) {
        this.lastNoaTable = lastNoaTable;
    }

    /**
     * @return the lastCnv
     */
    public CyNetworkView getLastCnv() {
        return lastCnv;
    }

    /**
     * @param lastCnv the lastCnv to set
     */
    public void setLastCnv(CyNetworkView lastCnv) {
        this.lastCnv = lastCnv;
    }

    public Path getSettingsPath() {
        return settingsPath;
    }

    public void setSettingsPath(Path settingsPath) {
        this.settingsPath = settingsPath;
    }

    /**
     * @return the visibleCevNetworks
     */
    public HashSet<CyNetwork> getVisibleCevNetworks() {
        return visibleCevNetworks;
    }

    /**
     * @param visibleCevNetworks the visibleCevNetworks to set
     */
    public void setVisibleCevNetworks(HashSet<CyNetwork> visibleCevNetworks) {
        this.visibleCevNetworks = visibleCevNetworks;
    }

	public String getBaits() {
		return jobDescription.getBaits();
	}

	public void setBaits(String baits) {
		jobDescription.setBaits(baits);
	}

	public Path getBaitsFilePath() {
		return jobDescription.getBaitsFilePath();
	}

	public void setBaitsFilePath(Path baitsFilePath) {
		jobDescription.setBaitsFilePath(baitsFilePath);
	}

	public Map<String, Path> getExpressionMatrices() {
		return jobDescription.getExpressionMatrices();
	}

	public void setExpressionMatrices(Map<String, Path> expression_matrices) {
		jobDescription.setExpressionMatrices(expression_matrices);
	}

	public double getNegativeCutoff() {
		return jobDescription.getNegativeCutoff();
	}

	public void setNegativeCutoff(double negativeCutoff) {
		jobDescription.setNegativeCutoff(negativeCutoff);
	}

	public double getPositiveCutoff() {
		return jobDescription.getPositiveCutoff();
	}

	public void setPositiveCutoff(double positiveCutoff) {
		jobDescription.setPositiveCutoff(positiveCutoff);
	}

	public Path getSaveFilePath() {
		return jobDescription.getSaveFilePath();
	}

	public void setSaveFilePath(Path saveFilePath) {
		jobDescription.setSaveFilePath(saveFilePath);
	}

	public Map<String, Path> getGeneFamilies() {
		return jobDescription.getGeneFamilies();
	}

	public void setGeneFamilies(Map<String, Path> geneFamilies) {
		jobDescription.setGeneFamilies(geneFamilies);
	}
	
	/**
	 * Get job description corresponding to current configuration
	 */
	public JobDescription getJobDescription() {
		return jobDescription;
	}

}
