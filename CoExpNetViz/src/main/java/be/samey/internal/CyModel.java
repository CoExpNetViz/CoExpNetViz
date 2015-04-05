package be.samey.internal;

import java.nio.file.Path;
import java.util.Observable;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.view.model.CyNetworkView;

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
 *
 * @author sam
 */
public class CyModel extends Observable {

    /*--------------------------------------------------------------------------
     properties of the app that do not change during execution
     */
    public static final String APP_NAME = "CoExpNetViz";
    public static final int MAX_SPECIES_COUNT = 5;
    public static final String URL = "http://bioinformatics.psb.ugent.be/webtools/morph/coexpr/run.php";
    //Which column to use to group nodes
    public static final int GROUP_COLUMN = 3;
    //which column contains the species of the baits
    public static final int SPECIES_COLUMN = 2;
    //what value indicates a node is a bait in the SPECIES_COLUMN
    public static final String BAIT_GROUP = "#FFFFFF";
    //the name to use for the new layout created by this app
    public static final String COMP_LAYOUT_NAME = "fam-layout";
    public static final String HUMAN_LAYOUT_NAME = "Family node Layout";
    //status messages for the analysis task
    public static final String PROG_TITLE = "Running " + APP_NAME;
    public static final String PROG_SERV = "Running analysis on server";
    public static final String PROG_NETW = "Creating network";
    public static final String PROG_LAYT = "Applying " + HUMAN_LAYOUT_NAME;
    public static final double PROG_CONN_COMPLETE = 0.8;
    public static final double PROG_NETW_COMPLETE = 0.9;

    /*--------------------------------------------------------------------------
     The fields below represent all the information that is required to execute
     one analysis (one RunAnalysisTask)
     */
    //title of the current analysis
    private String title;
    //sent to server
    private String baits;
    private String[] speciesNames;
    private Path[] speciesPaths;
    private double nCutoff;
    private double pCutoff;
    private Path saveFilePath;
    //Node table for last created network
    //this is set by CreateNetworkTask and used by ShowNetworkTask
    private CyTable lastNoaTable;
    //Network view for last created network
    //this is set by CreateNetworkTask and used by ShowNetworkTask
    private CyNetworkView lastCnv;
    //paths to network files for last created network
    //these are set by runAnalysistask and then used by CreateNetworkTask
    private Path sifPath;
    private Path noaPath;
    private Path edaPath;
    private Path vizPath;
    private Path logPath;

    /*--------------------------------------------------------------------------
     Fields to keep track of the networks created by the app
     */

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
     * @return the baits
     */
    public String getBaits() {
        return baits;
    }

    /**
     * @param baits the baits to set
     */
    public void setBaits(String baits) {
        this.baits = baits;
    }

    /**
     * @return the speciesNames
     */
    public String[] getSpeciesNames() {
        return speciesNames;
    }

    /**
     * @param speciesNames the speciesNames to set
     */
    public void setSpeciesNames(String[] speciesNames) {
        this.speciesNames = speciesNames;
    }

    /**
     * @return the speciesPaths
     */
    public Path[] getSpeciesPaths() {
        return speciesPaths;
    }

    /**
     * @param speciesPaths the speciesPaths to set
     */
    public void setSpeciesPaths(Path[] speciesPaths) {
        this.speciesPaths = speciesPaths;
    }

    /**
     * @return the nCutoff
     */
    public double getNCutoff() {
        return nCutoff;
    }

    /**
     * @param nCutoff the nCutoff to set
     */
    public void setnCutoff(double nCutoff) {
        this.nCutoff = nCutoff;
    }

    /**
     * @return the pCutoff
     */
    public double getPCutoff() {
        return pCutoff;
    }

    /**
     * @param pCutoff the pCutoff to set
     */
    public void setpCutoff(double pCutoff) {
        this.pCutoff = pCutoff;
    }

    /**
     * @return the saveFilePath
     */
    public Path getSaveFilePath() {
        return saveFilePath;
    }

    /**
     * @param saveFilePath the saveFilePath to set
     */
    public void setSaveFilePath(Path saveFilePath) {
        this.saveFilePath = saveFilePath;
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

    /**
     * @return the sifPath
     */
    public Path getSifPath() {
        return sifPath;
    }

    /**
     * @param sifPath the sifPath to set
     */
    public void setSifPath(Path sifPath) {
        this.sifPath = sifPath;
    }

    /**
     * @return the noaPath
     */
    public Path getNoaPath() {
        return noaPath;
    }

    /**
     * @param noaPath the noaPath to set
     */
    public void setNoaPath(Path noaPath) {
        this.noaPath = noaPath;
    }

    /**
     * @return the edaPath
     */
    public Path getEdaPath() {
        return edaPath;
    }

    /**
     * @param edaPath the edaPath to set
     */
    public void setEdaPath(Path edaPath) {
        this.edaPath = edaPath;
    }

    /**
     * @return the vizPath
     */
    public Path getVizPath() {
        return vizPath;
    }

    /**
     * @param vizPath the vizPath to set
     */
    public void setVizPath(Path vizPath) {
        this.vizPath = vizPath;
    }

    /**
     * @return the logPath
     */
    public Path getLogPath() {
        return logPath;
    }

    /**
     * @param logPath the logPath to set
     */
    public void setLogPath(Path logPath) {
        this.logPath = logPath;
    }

}
