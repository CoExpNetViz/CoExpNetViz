package be.samey.model;

import be.samey.gui.RootPanel;
import be.samey.gui.SpeciesEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author sam
 */
public class GuiStatus extends Observable {

    //these are not declared final because I don't want them to be loaded if
    //the cytoscape user is not currently using this app. But these should be
    //initialized only once.
    private JFrame rootPanelFrame;
    private JPanel rootPanel;

    private boolean inpBaitSelected;
    private boolean saveFileSelected;
    private final double defaultNegCutoff = -0.6;
    private final double defaultPosCutoff = 0.8;

    private final List<SpeciesEntry> speciesList = new ArrayList<SpeciesEntry>();

    public void setRootPanelFrame(JFrame rootPanelFrame) {
        this.rootPanelFrame = rootPanelFrame;
    }

    public JFrame getRootPanelFrame() {
        return rootPanelFrame;
    }

    /**
     * Sets the panel which will be notified by the guiStatus on state changes
     * 
     * @param rootPanel 
     */
    public void setRootPanel(RootPanel rootPanel) {
        this.rootPanel = rootPanel;
        this.addObserver(rootPanel);
        setChanged();
        notifyObservers();
    }

    public JPanel getRootPanel() {
        return rootPanel;
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

    public List<SpeciesEntry> getSpeciesList() {
        return speciesList;
    }

    public double getDefaultNegCutoff() {
        return defaultNegCutoff;
    }

    public double getDefaultPosCutoff() {
        return defaultPosCutoff;
    }

}
