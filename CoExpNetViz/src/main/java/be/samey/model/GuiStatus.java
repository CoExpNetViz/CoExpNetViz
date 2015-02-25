package be.samey.model;

import java.util.Observable;

/**
 *
 * @author sam
 */
public class GuiStatus extends Observable {

    //tab 1: one species
    private boolean chooseSpeciesSelected;
    private boolean inpBaitSelected;
    private boolean saveFileSelected;

    public void setChooseSpeciesSelected(boolean chooseSpeciesSelected) {
        //TODO: only do this part if there was actually a change
        this.chooseSpeciesSelected = chooseSpeciesSelected;
        setChanged();
        notifyObservers();
    }

    public boolean isChooseSpeciesSelected() {
        return chooseSpeciesSelected;
    }

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

    //all tabs
    private double defaultNegCutoff = -0.6;
    private double defaultPosCutoff = 0.8;

    public double getDefaultNegCutoff() {
        return defaultNegCutoff;
    }

    public double getDefaultPosCutoff() {
        return defaultPosCutoff;
    }
}
