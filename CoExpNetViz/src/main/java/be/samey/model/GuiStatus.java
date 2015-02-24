/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.model;

import java.util.Observable;

/**
 *
 * @author sam
 */
public class GuiStatus extends Observable {

    private final Model model;

    public GuiStatus(Model model) {
        this.model = model;
    }

    //tab 1: one species
    private boolean chooseSpecies;
    private boolean inpBait;
    private boolean saveFile;

    public void setChooseSpecies(boolean chooseSpecies) {
        //TODO: only do this part if there was actually a change
        this.chooseSpecies = chooseSpecies;
        setChanged();
        notifyObservers();
    }

    public boolean getChooseSpecies() {
        return chooseSpecies;
    }

    public void setInpBait(boolean inpBait) {
        this.inpBait = inpBait;
        setChanged();
        notifyObservers();
    }

    public boolean getInpBait() {
        return inpBait;
    }

    public void setSaveFile(boolean saveFile) {
        this.saveFile = saveFile;
        setChanged();
        notifyObservers();
    }

    public boolean getSaveFile() {
        return saveFile;
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
