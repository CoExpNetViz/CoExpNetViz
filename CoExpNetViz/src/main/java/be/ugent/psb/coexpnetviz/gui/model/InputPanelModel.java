package be.ugent.psb.coexpnetviz.gui.model;

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
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import be.ugent.psb.coexpnetviz.gui.GUIConstants;
import be.ugent.psb.coexpnetviz.gui.OrthEntry;
import be.ugent.psb.coexpnetviz.gui.SpeciesEntry;

/**
 *
 * @author sam
 *
 * Corresponds to one input settings profile
 */
public class InputPanelModel extends AbstrModel {

    private String title;
    private boolean useBaitFile;
    private String baits;
    private Path baitFilePath;
    private double negCutoff;
    private double posCutoff;
    private boolean saveResults;
    private Path saveFilePath;

    private Map<SpeciesEntryModel, SpeciesEntry> species;
    private Map<OrthEntryModel, OrthEntry> orthGroups;

    public InputPanelModel(SpeciesEntryModel sem, SpeciesEntry se) {
        title = "";
        useBaitFile = false;
        baits = "";
        baitFilePath = Paths.get("");
        negCutoff = GUIConstants.DEFAULT_NEG_CUTOFF;
        posCutoff = GUIConstants.DEFAULT_POS_CUTOFF;
        saveResults = false;
        saveFilePath = Paths.get(System.getProperty("user.home"));

        species = new LinkedHashMap<SpeciesEntryModel, SpeciesEntry>();
        orthGroups = new LinkedHashMap<OrthEntryModel, OrthEntry>();

        species.put(sem, se);
    }

    public InputPanelModel() {
        title = "";
        useBaitFile = false;
        baits = "";
        baitFilePath = Paths.get("");
        negCutoff = GUIConstants.DEFAULT_NEG_CUTOFF;
        posCutoff = GUIConstants.DEFAULT_POS_CUTOFF;
        saveResults = false;
        saveFilePath = Paths.get(System.getProperty("user.home"));

        species = new LinkedHashMap<SpeciesEntryModel, SpeciesEntry>();
        orthGroups = new LinkedHashMap<OrthEntryModel, OrthEntry>();
    }

    public InputPanelModel copy() {
        InputPanelModel inpPnlModel = new InputPanelModel();
        inpPnlModel.setTitle(title);
        inpPnlModel.setUseBaitFile(useBaitFile);
        inpPnlModel.setBaits(baits);
        inpPnlModel.setBaitFilePath(baitFilePath);
        inpPnlModel.setNegCutoff(negCutoff);
        inpPnlModel.setPosCutoff(posCutoff);
        inpPnlModel.setSaveResults(saveResults);
        inpPnlModel.setSaveFilePath(saveFilePath);

        LinkedHashMap<SpeciesEntryModel, SpeciesEntry> newSpecies = new LinkedHashMap<SpeciesEntryModel, SpeciesEntry>();
        for (SpeciesEntryModel sem : species.keySet()) {
            newSpecies.put(sem, species.get(sem));
        }
        inpPnlModel.setAllSpecies(newSpecies);

        LinkedHashMap<OrthEntryModel, OrthEntry> newOrthGroups = new LinkedHashMap<OrthEntryModel, OrthEntry>();
        for (OrthEntryModel oem : orthGroups.keySet()) {
            newOrthGroups.put(oem, orthGroups.get(oem));
        }
        inpPnlModel.setAllOrthGroups(newOrthGroups);

        return inpPnlModel;
    }

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
        if (!this.title.equals(title)) {
            this.title = title;
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
        if (this.useBaitFile != useBaitFile) {
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
        if (!this.baits.equals(baits)) {
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
        if (this.baitFilePath != baitFilePath) {
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
        if (this.negCutoff != NegCutoff) {
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
        if (this.posCutoff != PosCutoff) {
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
        if (this.saveResults != saveResults) {
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
        if (this.saveFilePath != saveFilePath) {
            this.saveFilePath = saveFilePath;
            setChanged();
            notifyObservers();
        }
    }

    public SpeciesEntry getSpeciesEntry(SpeciesEntryModel sem) {
        return species.get(sem);
    }

    public void setSpeciesEntry(SpeciesEntryModel sem, SpeciesEntry se) {
        species.put(sem, se);
        setChanged();
        notifyObservers();
    }

    public void addSpecies(SpeciesEntryModel sem, SpeciesEntry se) {
        if (!species.containsKey(sem)) {
            species.put(sem, se);
            setChanged();
            notifyObservers();
        }
    }

    public void removeSpecies(SpeciesEntryModel sem) {
        if (species.containsKey(sem)) {
            species.remove(sem);
            setChanged();
            notifyObservers();
        }
    }

    public Map<SpeciesEntryModel, SpeciesEntry> getAllSpecies() {
        return species;
    }

    public void setAllSpecies(Map<SpeciesEntryModel, SpeciesEntry> species) {
        this.species = species;
    }

    public OrthEntry getOrthEntry(OrthEntryModel oem) {
        return orthGroups.get(oem);
    }

    public void setOrthEntry(OrthEntryModel oem, OrthEntry oe) {
        orthGroups.put(oem, oe);
        setChanged();
        notifyObservers();
    }

    public void addOrthGroup(OrthEntryModel oem, OrthEntry oe) {
        if (!orthGroups.containsKey(oem)) {
            orthGroups.put(oem, oe);
            setChanged();
            notifyObservers();
        }
    }

    public void removeOrthGroup(OrthEntryModel oem) {
        if (orthGroups.containsKey(oem)) {
            orthGroups.remove(oem);
            setChanged();
            notifyObservers();
        }
    }

    public Map<OrthEntryModel, OrthEntry> getAllOrthGroups() {
        return orthGroups;
    }

    public void setAllOrthGroups(LinkedHashMap<OrthEntryModel, OrthEntry> orthGroups) {
        this.orthGroups = orthGroups;
    }

}
