package be.samey.gui.model;

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

import be.samey.gui.GuiConstants;
import be.samey.gui.model.GuiModel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author sam
 *
 * Corresponds to one input settings profile
 */
public class InpPnlModel extends GuiModel {

    private String jobTitle;
    private boolean useBaitFile;
    private String baits;
    private Path baitFilePath;
    private double negCutoff;
    private double posCutoff;
    private boolean saveResults;
    private Path saveFilePath;

    public InpPnlModel() {
        jobTitle = "";
        useBaitFile = false;
        baits = "";
        baitFilePath = Paths.get("");
        negCutoff = GuiConstants.DEFAULT_NEG_CUTOFF;
        posCutoff = GuiConstants.DEFAULT_POS_CUTOFF;
        saveResults = false;
        saveFilePath = Paths.get("");
    }

    /**
     * @return the jobTitle
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * @param jobTitle the jobTitle to set
     */
    public void setJobTitle(String jobTitle) {
        if (!this.jobTitle.equals(jobTitle) ){
            this.jobTitle = jobTitle;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return the useBaitFile
     */
    public boolean isUseBaitFile() {
        return useBaitFile;
    }

    /**
     * @param useBaitFile the useBaitFile to set
     */
    public void setUseBaitFile(boolean useBaitFile) {
        if (this.useBaitFile != useBaitFile ){
            this.useBaitFile = useBaitFile;
            setChanged();
            notifyObservers();
        }
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
        if (!this.baits.equals(baits) ){
            this.baits = baits;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return the baitFilePath
     */
    public Path getBaitFilePath() {
        return baitFilePath;
    }

    /**
     * @param baitFilePath the baitFilePath to set
     */
    public void setBaitFilePath(Path baitFilePath) {
        if (this.baitFilePath != baitFilePath ){
            this.baitFilePath = baitFilePath;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return the negCutoff
     */
    public double getNegCutoff() {
        return negCutoff;
    }

    /**
     * @param NegCutoff the negCutoff to set
     */
    public void setNegCutoff(double NegCutoff) {
        if (this.negCutoff != NegCutoff ){
            this.negCutoff = NegCutoff;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return the posCutoff
     */
    public double getPosCutoff() {
        return posCutoff;
    }

    /**
     * @param PosCutoff the posCutoff to set
     */
    public void setPosCutoff(double PosCutoff) {
        if (this.posCutoff != PosCutoff ){
            this.posCutoff = PosCutoff;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return the saveResults
     */
    public boolean isSaveResults() {
        return saveResults;
    }

    /**
     * @param saveResults the saveResults to set
     */
    public void setSaveResults(boolean saveResults) {
        if (this.saveResults != saveResults ){
            this.saveResults = saveResults;
            setChanged();
            notifyObservers();
        }
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
        if (this.saveFilePath != saveFilePath ){
            this.saveFilePath = saveFilePath;
            setChanged();
            notifyObservers();
        }
    }

}
