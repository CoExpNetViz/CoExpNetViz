package be.ugent.psb.coexpnetviz.gui.controller;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2016 PSB/UGent
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

import javax.swing.JFrame;

import be.ugent.psb.coexpnetviz.Context;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel;
import be.ugent.psb.coexpnetviz.gui.view.JobInput;
import be.ugent.psb.util.TCCLRunnable;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;


// TODO rm most of util.mvc if Pivot turns out successful

/**
 * Creates and controls a JFrame with a JobInput pane
 */
public class JobInputFrameController {

    private final Context context;
    private JFrame rootFrame;

    public JobInputFrameController(Context context) { // XXX only accept a presets and a JobInput model
        this.context = context;

        //load settings: presets
//        try {
//        	SettingsIO settingsIO = new SettingsIO(context);
//            allModels = settingsIO.readAllProfiles();
//        } catch (Exception ex) {
//        	JOptionPane.showMessageDialog(null, "Failed to load settings: " + ex.getMessage(), GUIConstants.MESSAGE_DIALOG_TITLE, JOptionPane.WARNING_MESSAGE);
//        }
    }
    
    /**
     * Create frame if it does not exist yet
     * 
     * Only call this on the Swing event thread
     */
    private void ensureFrameIsCreated() {
    	if (rootFrame != null)
    		return;
    	
    	rootFrame = new JFrame("Co-expression Network Visualization Tool");
        rootFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        final JFXPanel fxPanel = new JFXPanel();
        rootFrame.getContentPane().add(fxPanel);

        Platform.runLater(new TCCLRunnable() {
            @Override
            public void runInner() {
            	JobInput jobInputPane = new JobInput();
            	Scene scene = new Scene(jobInputPane);
            	jobInputPane.init(new JobInputModel(), scene.getWindow());
            	fxPanel.setScene(scene);
            }
        });
    }

    public void show() {
    	ensureFrameIsCreated();
        rootFrame.pack();
        rootFrame.setVisible(true);
    }

//    public void addCurrentModel() {
//    	assert false;
//        JobInputModel ipmToRemove = null;
//        for (JobInputModel ipm : allModels) {
//            if (ipm.getTitle().equals(activeModel.getTitle())) {
//                ipmToRemove = ipm;
//            }
//        }
//        if (ipmToRemove != null) {
//            allModels.remove(ipmToRemove);
//        }
//        allModels.add(activeModel.copy());
//    }

//    public void saveProfiles() throws IOException {
//    	SettingsIO settingsIO = new SettingsIO(context);
//        settingsIO.writeAllProfiles(allModels);
//        settingsIO.writeAllSpecies(getAllSpeciesEntryModels());
//    }

//    public List<SpeciesEntryModel> getAllSpeciesEntryModels() {
//        List<SpeciesEntryModel> sems = new ArrayList<SpeciesEntryModel>();
//        for (JobInputModel ipm : allModels) {
//            for (SpeciesEntryModel sem : ipm.getAllSpecies().keySet()) {
//                if (!sems.contains(sem)) {
//                    sems.add(sem);
//                }
//            }
//        }
//        return sems;
//    }

//    public void loadProfile(String profileName) {
//        JobInputModel ipmToLoad = null;
//        for (JobInputModel ipm : allModels) {
//            if (ipm.getTitle().equals(profileName)) {
//                ipmToLoad = ipm.copy();
//            }
//        }
//        if (ipmToLoad == null) {
//            throw new IllegalArgumentException("Could not load profile" + profileName);
//        }
//        for (SpeciesEntryModel sem : ipmToLoad.getAllSpecies().keySet()) {
//            SpeciesEntryPanel se = initSpeciesEntry(sem);
//            ipmToLoad.setSpeciesEntry(sem, se);
//        }
//        setActiveModel(ipmToLoad);
//        ipmToLoad.triggerUpdate();
//    }
//
//    public void delCurrentProfile() {
//        JobInputModel ipmToRemove = null;
//        for (JobInputModel ipm : allModels) {
//            if (ipm.getTitle().equals(activeModel.getTitle())) {
//                ipmToRemove = ipm;
//            }
//        }
//        if (ipmToRemove != null) {
//            allModels.remove(ipmToRemove);
//        }
//    }
//
//    public String[] getProfileTitles() {
//        List<String> titles = new ArrayList<String>();
//        for (JobInputModel ipm : allModels) {
//            titles.add(ipm.getTitle());
//        }
//        return titles.toArray(new String[titles.size()]);
//    }

//    public JobInputPanel getInpPnl() {
//        return inputPanel;
//    }

}
