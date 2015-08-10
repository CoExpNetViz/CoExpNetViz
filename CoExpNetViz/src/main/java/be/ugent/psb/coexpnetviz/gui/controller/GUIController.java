package be.ugent.psb.coexpnetviz.gui.controller;

import be.ugent.psb.coexpnetviz.CENVApplication;
import be.ugent.psb.coexpnetviz.gui.CENVModel;

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

import be.ugent.psb.coexpnetviz.gui.GUIConstants;
import be.ugent.psb.coexpnetviz.gui.model.InputPanelModel;
import be.ugent.psb.coexpnetviz.gui.model.OrthEntryModel;
import be.ugent.psb.coexpnetviz.gui.model.SpeciesEntryModel;
import be.ugent.psb.coexpnetviz.gui.view.JobInputPanel;
import be.ugent.psb.coexpnetviz.gui.view.OrthEntryPanel;
import be.ugent.psb.coexpnetviz.gui.view.SpeciesEntryPanel;
import be.ugent.psb.coexpnetviz.io.SettingsIO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * CENV plugin GUI controller, the root of (gui) controllers
 */
public class GUIController {

    private final CENVApplication cyAppManager;

    // views
    private final JobInputPanel inputPanel;
    private final JFrame rootFrame;

    // models
    private Path lastUsedDirPath;
    private InputPanelModel activeModel;
    private List<InputPanelModel> allModels;

    public GUIController(CENVApplication cyAppManager) {
        this.cyAppManager = cyAppManager;

        lastUsedDirPath = Paths.get(System.getProperty("user.home"));

        //load settings
        allModels = new ArrayList<InputPanelModel>();
        try {
        	SettingsIO settingsIO = new SettingsIO(cyAppManager);
            allModels = settingsIO.readAllProfiles();
        } catch (Exception ex) {
        	JOptionPane.showMessageDialog(null, "Failed to load settings: " + ex.getMessage(), GUIConstants.MESSAGE_DIALOG_TITLE, JOptionPane.WARNING_MESSAGE);
        }

        //make GUI
        inputPanel = createJobInputPanel();

        //put the GUI in a new Frame
        rootFrame = new JFrame(GUIConstants.ROOT_FRAME_TITLE);
        rootFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        rootFrame.setContentPane(inputPanel);

    }

    private JobInputPanel createJobInputPanel() {
        JobInputPanel inputPanel = new JobInputPanel();

        //attach controllers
        
        
        //buttons
        inputPanel.profileLoadButton.addActionListener(new ProfLoadBtnController(cyAppManager));
        inputPanel.profileSaveButton.addActionListener(new ProfSaveBtnController(cyAppManager));
        inputPanel.profileDeleteButton.addActionListener(new ProfDelBtnController(cyAppManager));
        inputPanel.resetButton.addActionListener(new ResetGuiController(cyAppManager));
        
        //title
        inputPanel.titleTextField.addFocusListener(new TitleTfController(cyAppManager));
        
        //baits
        inputPanel.baitInputRadioButton.addActionListener(new BaitFileOrInpController(cyAppManager));
        inputPanel.baitFileRadioButton.addActionListener(new BaitFileOrInpController(cyAppManager));
        inputPanel.baitInputInfoButton.addActionListener(new BaitInpInfoBtnController(cyAppManager));
        inputPanel.baitInputTextArea.addFocusListener(new BaitInpTaController(cyAppManager));
        inputPanel.baitFileTextField.addFocusListener(new BaitFileTfController(cyAppManager));
        inputPanel.baitFileButton.addActionListener(new BaitFileBtnController(cyAppManager));
        
        //species
        inputPanel.addSpeciesButton.addActionListener(new SpeciesAddBtnController(cyAppManager));
        
        //cutoffs
        inputPanel.negativeCutoffSpinner.addChangeListener(new CutoffController(cyAppManager));
        inputPanel.positiveCutoffSpinner.addChangeListener(new CutoffController(cyAppManager));
        
        //save
        inputPanel.saveFileCheckBox.addActionListener(new SaveFileChbController(cyAppManager));
        inputPanel.saveFileTextField.addFocusListener(new SaveFileTfController(cyAppManager));
        inputPanel.saveFileButton.addActionListener(new SaveFileBtnController(cyAppManager));
        
        //add orth groups file
        inputPanel.orthAddButton.addActionListener(new OrthEntryAddBtnController(cyAppManager));
        
        //go
        inputPanel.goButton.addActionListener(new RunAnalysisController(cyAppManager));

        //make the model for the GUI
        activeModel = makeDefaultModel();
        activeModel.addObserver(inputPanel);

        return inputPanel;

    }

    public void showRootFrame() {
        rootFrame.pack();
        rootFrame.setVisible(true);
        activeModel.triggerUpdate();
    }

    /**
     * Create speciesEntry with controllers that updates sem
     *
     * @param sem
     * @return
     */
    public SpeciesEntryPanel initSpeciesEntry(SpeciesEntryModel sem) {
        SpeciesEntryPanel se = new SpeciesEntryPanel();

        se.speciesNameTf.addFocusListener(new SpeciesNameTfController(cyAppManager, sem));
        se.speciesFileTf.addFocusListener(new SpeciesFileTfController(cyAppManager, sem));
        se.removeBtn.addActionListener(new SpeciesDelController(cyAppManager, sem));
        se.browseBtn.addActionListener(new SpeciesFileBtnController(cyAppManager, sem));

        sem.addObserver(se);

        return se;
    }

    public OrthEntryPanel initOrthEntry(OrthEntryModel oem){
        OrthEntryPanel oe = new OrthEntryPanel();

        oem.addObserver(oe);

        //add controllers for textfield, browse and remove
        oe.orthNameTf.addFocusListener(new OrthNameTfController(cyAppManager, oem));
        oe.orthRemoveBtn.addActionListener(new OrthDelController(oem, cyAppManager));
        oe.orthPathTf.addFocusListener(new OrthFileTfController(oem, cyAppManager));
        oe.orthBrowseBtn.addActionListener(new OrthFileBtnController(oem, cyAppManager));

        return oe;
    }

    public InputPanelModel makeDefaultModel() {
        SpeciesEntryModel sem = new SpeciesEntryModel();
        SpeciesEntryPanel se = initSpeciesEntry(sem);
        return new InputPanelModel(sem, se);
    }

    public void addCurrentModel() {
        InputPanelModel ipmToRemove = null;
        for (InputPanelModel ipm : allModels) {
            if (ipm.getTitle().equals(activeModel.getTitle())) {
                ipmToRemove = ipm;
            }
        }
        if (ipmToRemove != null) {
            allModels.remove(ipmToRemove);
        }
        allModels.add(activeModel.copy());
    }

    public void saveProfiles() throws IOException {
    	SettingsIO settingsIO = new SettingsIO(cyAppManager);
        settingsIO.writeAllProfiles(allModels);
        settingsIO.writeAllSpecies(getAllSpeciesEntryModels());
    }

    public List<SpeciesEntryModel> getAllSpeciesEntryModels() {
        List<SpeciesEntryModel> sems = new ArrayList<SpeciesEntryModel>();
        for (InputPanelModel ipm : allModels) {
            for (SpeciesEntryModel sem : ipm.getAllSpecies().keySet()) {
                if (!sems.contains(sem)) {
                    sems.add(sem);
                }
            }
        }
        return sems;
    }

    public void loadProfile(String profileName) {
        InputPanelModel ipmToLoad = null;
        for (InputPanelModel ipm : allModels) {
            if (ipm.getTitle().equals(profileName)) {
                ipmToLoad = ipm.copy();
            }
        }
        if (ipmToLoad == null) {
            throw new IllegalArgumentException("Could not load profile" + profileName);
        }
        for (SpeciesEntryModel sem : ipmToLoad.getAllSpecies().keySet()) {
            SpeciesEntryPanel se = initSpeciesEntry(sem);
            ipmToLoad.setSpeciesEntry(sem, se);
        }
        setActiveModel(ipmToLoad);
        ipmToLoad.triggerUpdate();
    }

    public void delCurrentProfile() {
        InputPanelModel ipmToRemove = null;
        for (InputPanelModel ipm : allModels) {
            if (ipm.getTitle().equals(activeModel.getTitle())) {
                ipmToRemove = ipm;
            }
        }
        if (ipmToRemove != null) {
            allModels.remove(ipmToRemove);
        }
    }

    public String[] getProfileTitles() {
        List<String> titles = new ArrayList<String>();
        for (InputPanelModel ipm : allModels) {
            titles.add(ipm.getTitle());
        }
        return titles.toArray(new String[titles.size()]);
    }

    public List<InputPanelModel> getAllModels() {
        return allModels;
    }

    public JobInputPanel getInpPnl() {
        return inputPanel;
    }

    public JFrame getRootFrame() {
        return rootFrame;
    }

    public Path getLastUsedDirPath() {
        return lastUsedDirPath;
    }

    public void setLastUsedDirPath(Path lastUsedDirPath) {
        this.lastUsedDirPath = lastUsedDirPath;
    }

    public void setActiveModel(InputPanelModel inpPnlModel) {
        if (inpPnlModel != this.activeModel) {
            this.activeModel.deleteObserver(inputPanel);
            this.activeModel = inpPnlModel;
            activeModel.addObserver(inputPanel);
        }
    }

    public InputPanelModel getActiveModel() {
        return activeModel;
    }

}
