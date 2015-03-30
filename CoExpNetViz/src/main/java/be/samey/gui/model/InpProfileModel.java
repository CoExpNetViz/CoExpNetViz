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
import be.samey.internal.CyModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sam
 */
public class InpProfileModel extends GuiModel {

    private InpPnlModel inpPnlModel;
    private List<SpeciesEntryModel> speciesEntryModels = new ArrayList<SpeciesEntryModel>();

    public InpProfileModel() {
    }

    public InpPnlModel getInpPnlModel() {
        return inpPnlModel;
    }

    public void setInpPnlModel(InpPnlModel inpPnlModel) {
        if (inpPnlModel != this.inpPnlModel) {
            this.inpPnlModel = inpPnlModel;
            setChanged();
            notifyObservers();
        }
    }

    public SpeciesEntryModel getSpeciesEntryModel(int i) {
        return speciesEntryModels.get(i);
    }

    public void addSpeciesEntryModel(SpeciesEntryModel speciesEntryModel) {
        if (!speciesEntryModels.contains(speciesEntryModel)) {
            speciesEntryModels.add(speciesEntryModel);
            setChanged();
            notifyObservers();
        }
    }

    public void removeSpeciesEntryModel(SpeciesEntryModel sem) {
        speciesEntryModels.remove(sem);
        setChanged();
        notifyObservers();
    }

    /**
     * @return the speciesEntryModels
     */
    public List<SpeciesEntryModel> getSpeciesEntryModels() {
        return speciesEntryModels;
    }

    /**
     * @param speciesEntryModels the speciesEntryModels to set
     */
    public void setSpeciesEntryModels(List<SpeciesEntryModel> speciesEntryModels) {
        for (int i = 0; i < CyModel.MAX_SPECIES_COUNT; i++) {
            if (speciesEntryModels.get(i) != this.speciesEntryModels.get(i)) {
                this.speciesEntryModels = speciesEntryModels;
                setChanged();
                notifyObservers();
                return;
            }
        }
    }

}
