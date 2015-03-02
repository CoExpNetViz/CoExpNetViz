package be.samey.model;

import be.samey.gui.SpeciesEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author sam
 */
public class GuiStatus extends Observable {

    private boolean inpBaitSelected;
    private boolean saveFileSelected;
    private double defaultNegCutoff = -0.6;
    private double defaultPosCutoff = 0.8;

    private List<SpeciesEntry> speciesList = new ArrayList<SpeciesEntry>();

    public void setInpBaitSelected(boolean inpBaitSelected) {
        //TODO: only do this part if there was actually a change
        this.inpBaitSelected = inpBaitSelected;
        setChanged();
        notifyObservers();
    }

    public boolean isInpBaitSelected() {
        return inpBaitSelected;
    }

    public void setSaveFileSelected(boolean saveFileSelected) {
        //TODO: only do this part if there was actually a change
        this.saveFileSelected = saveFileSelected;
        setChanged();
        notifyObservers();
    }

    public boolean isSaveFileSelected() {
        return saveFileSelected;
    }

    public void addSpecies(SpeciesEntry se) {
        //only do something if list changed
        if (speciesList.add(se)) {
            setChanged();
            notifyObservers();
        }
    }

    public void removeSpecies(SpeciesEntry se) {
        //only do something if list changed
        if (speciesList.remove(se)) {
            setChanged();
            notifyObservers();
        }
    }

    public void removeAllSpecies() {
        //only do something if list changed
        if (speciesList.removeAll(speciesList)) {
            setChanged();
            notifyObservers();
        }
    }

    public Iterator<SpeciesEntry> getSpeciesIterator() {
        return speciesList.iterator();
    }

    public double getDefaultNegCutoff() {
        return defaultNegCutoff;
    }

    public double getDefaultPosCutoff() {
        return defaultPosCutoff;
    }
}
