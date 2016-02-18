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

import java.io.IOException;

import javax.swing.JFrame;

import be.ugent.psb.coexpnetviz.CENVContext;
import be.ugent.psb.coexpnetviz.gui.GUIConstants;
import be.ugent.psb.coexpnetviz.gui.model.JobInputModel;
import be.ugent.psb.coexpnetviz.gui.view.JobInput;
import be.ugent.psb.util.TCCLRunnable;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;


// TODO rm most of util.mvc if Pivot turns out successful

/**
 * CENV plugin GUI controller, the root of (gui) controllers
 */
public class GUIController {

    private final CENVContext context;

    // views
    private JFrame rootFrame;

    public GUIController(CENVContext context) {
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
    private void ensureFrameIsInitialised() {
    	if (rootFrame != null)
    		return;
    	
    	// This method is invoked on the EDT thread
    	rootFrame = new JFrame(GUIConstants.ROOT_FRAME_TITLE);
        rootFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        final JFXPanel fxPanel = new JFXPanel();
        rootFrame.getContentPane().add(fxPanel);

        Platform.runLater(new TCCLRunnable() {
            @Override
            public void runInner() {
            	JobInput jobInputPane = new JobInput();
            	jobInputPane.init(new JobInputModel());
            	Scene scene = new Scene(jobInputPane);
                fxPanel.setScene(scene);
            }
        });
    }

    // There is the Swing event dispatcher thread (SwingUtils.run*) and the JavaFX thread (Platform.run*). Be careful to run the right things on the right thread

    public void showRootFrame() {
    	ensureFrameIsInitialised();
        rootFrame.pack();
        rootFrame.setVisible(true);
//        activeModel.triggerUpdate(); TODO
    }
    
//    private JobInputPanel createJobInputPanel() {// TODO extract to another Controller
    	// Note: The functional style for MVC leads to a lot of boilerplate but we need
    	// the functional approach in order to have reusable controllers. In Java 8 the
    	// amount of boiler plate will be far less as there are lambdas.
        
        // Done:
        // - Centralise duplicated code
        // - Inline small non-reusable controllers
        // - Localise data where possible
        // - Dethrone GUIController, a god class
        // TODO check these indeed are done and work
        // - Single gene families file
        // - No names for imported files
        // - Rename profile to preset, replace UI with combo box based one, refactor the underlying controllers into PresetsController
        // - Percentiles as cutoffs instead of raw correlation values TODO
        
        // TODO
        // 3 options for gene families: radios
        
        // TODO
        // Between runs, by default take last used preset
        
        // TODO
        // baits text area: StringController -> baits
        // baits file text field + btn: replace by FilePanel, then attach FileController
        // replace the panel of multi gene fams files with a single panel
        
        // TODO for cutoffs, set min max, no need for a model, can store directly in there, though I saw a SpinnerModel. Then validation should check that lower < upper
        // ChangeListener
//        @Override
//        public void stateChanged(ChangeEvent event) {
//            JSpinner sp = (JSpinner) event.getSource();
//            double cutOff = (Double) sp.getValue();
        
        // TODO rename SaveFile* to OutputDirectory* 
        
        // TODO species: ListAnonymousController: Controls a list of anonymous items ordered by first to last added.
        
        // TODO cleanup models and unused Components
        
//    	final ValueModel<JobInputModel> currentJobInput = new DefaultValueModel<JobInputModel>();
//        final JobInputPanel inputPanel = new JobInputPanel();
//        
//        // Presets
//        /*new PresetsController
//        inputPanel.presetLoadButton.addActionListener(new ProfLoadBtnController(cyAppManager));
//        inputPanel.presetSaveButton.addActionListener(new ProfSaveBtnController(cyAppManager));
//        inputPanel.presetDeleteButton.addActionListener(new PresetsController(cyAppManager));*/
//        
//        // When clear form button, clear form
//        inputPanel.clearFormButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				currentJobInput.set(new JobInputModel());
//			}
//        });
//        
//        
//        // Sync baitGroupSource with ButtonGroup
//        inputPanel.baitGroupOptionText.setActionCommand(BaitGroupSource.TEXT.toString());
//        inputPanel.baitGroupOptionFile.setActionCommand(BaitGroupSource.FILE.toString());
//		ValueModel<BaitGroupSource> currentBaitGroupSource = new IndirectedValueModel<BaitGroupSource>( // XXX a chained style would look how much better?
//			new TransformedValueModel<JobInputModel, ValueModel<BaitGroupSource>>(currentJobInput) {
//	        	@Override
//				protected ValueModel<BaitGroupSource> transform(JobInputModel value) {
//					return value.getBaitGroupSource();
//				}
//
//				@Override
//				protected JobInputModel transformInverse(ValueModel<BaitGroupSource> value) {
//					throw new UnsupportedOperationException();
//				}
//			}
//		);
//		ValueModel<String> currentBaitGroupSourceString = new TransformedValueModel<BaitGroupSource, String>(currentBaitGroupSource) {
//			@Override
//			protected String transform(BaitGroupSource value) {
//				return value.toString();
//			}
//
//			@Override
//			protected BaitGroupSource transformInverse(String value) {
//				return BaitGroupSource.valueOf(value);
//			}
//		};
//        new ButtonGroupController(currentBaitGroupSourceString, inputPanel.baitGroupOptions);
//        
//        // When selected bait group input type changes, show the right input components
//        baitGroupSourceModel.addListener(new ValueChangeListener<String>() {
//			@Override
//			public void valueChanged(ValueModel<String> source_, String oldValue) {
//				BaitGroupSource source = currentJobInput.get().getBaitGroupSource();
//				inputPanel.baitGroupTextArea.setVisible(false);
//				inputPanel.baitFilePanel.setVisible(false);
//				if (source.equals(BaitGroupSource.FILE)) {
//					inputPanel.baitFilePanel.setVisible(true);
//				}
//				else if (source.equals(BaitGroupSource.TEXT)) {
//					inputPanel.baitGroupTextArea.setVisible(true);
//				}
//			}
//		});
//        
//        // Sync bait group text area
//        TransformedValueModel<String, String> currentBaitGroupText = new TransformedValueModel<String, String>(currentJobInput) {
//        	@Override
//			protected String transform(String value) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			protected String transformInverse(String value) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
//        new StringController(currentBaitGroupText, inputPanel.baitGroupTextArea);
//        
//        // Bait file controller + sync
//        inputPanel.baitGroupFileTextField.addFocusListener(new BaitFileTfController(context));
//        inputPanel.baitFileButton.addActionListener(new BaitFileBtnController(context));
//        inputPanel.baitInputInfoButton.addActionListener(new ActionListener() {
//        	@Override
//            public void actionPerformed(ActionEvent ae) {
//                String message
//                    = "<html>"
//                    + "Supported Gene ID symbols:"
//                    + "<br>"
//                    + "<br>"
//                    + "All default Plaza gene ID's are supported, some examples are:"
//                    + "<ul style=\"list-style: none;\">"
//                    + makeListEntry("Arabidopsis thaliana", "AT#G#####")
//                    + makeListEntry("Oriza sativa", "Os##g#######")
//                    + makeListEntry("Solanum lycopersicon", "Solyc##g######")
//                    + makeListEntry("Solanum tuberosum", "Sotub##g######")
//                    + "</ul>"
//                    + "<br>"
//                    + "Other supported ID's are:"
//                    + "<ul style=\"list-style: none;\">"
//                    + makeListEntry("Malus domestica", "MDP##########")
//                    + makeListEntry("Oriza sativa", "LOC_Os##g#######")
//                    + makeListEntry("Solanum tuberosum", "PGSC####DMP#########")
//                    + "</ul>"
//                    + "<br>"
//                    + "Any gene ID's are supported if they are also present"
//                    + "<br>"
//                    + "in user supplied gene family files"
//                    + "</html>";
//
//                JOptionPane.showMessageDialog(inputPanel, message);
//            }
//
//            private String makeListEntry(String species, String pattern) {
//                pattern = pattern.replaceAll("(#+)", "<font color=\"blue\">$1</font>");
//                return String.format("<li><pre><i>%-22s</i>: %s</pre></li>", species, pattern);
//            }
//        });
//        
//        //species
//        inputPanel.addSpeciesButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				assert false;
////				SpeciesEntryModel sem = new SpeciesEntryModel();
////		        SpeciesEntryPanel se = getGuiManager().initSpeciesEntry(sem);
////		        getActiveModel().addSpecies(sem, se);
//			}
//		});
//        
//        //cutoffs
//        inputPanel.negativeCutoffSpinner.addChangeListener(new CutoffController(context));
//        inputPanel.positiveCutoffSpinner.addChangeListener(new CutoffController(context));
//        
//        //save
//        inputPanel.saveFileCheckBox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				assert false;
////				if (ae.getSource() instanceof JToggleButton) {
////		            boolean saveResults = ((JToggleButton) ae.getSource()).isSelected();
////		            getActiveModel().setSaveResults(saveResults);
////		        }
//			}
//		});
//        inputPanel.saveFileTextField.addFocusListener(new FocusListener() {
//			@Override
//			public void focusLost(FocusEvent e) {
//				assert false;
////				getActiveModel().setSaveFilePath(Paths.get(getText(fe)));
//			}
//			
//			@Override
//			public void focusGained(FocusEvent e) {
//			}
//		});
//        inputPanel.saveFileButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				assert false;
////				getActiveModel().setSaveFilePath(showFileChooser(
////			            "Choose output directory",
////			            DIRECTORY,
////			            getActiveModel().getSaveFilePath()));
//			}
//		});
//        
//        //go
//        inputPanel.goButton.addActionListener(new RunAnalysisController(context));

//        return inputPanel;

//    }

//    /**
//     * Create speciesEntry with controllers that updates sem
//     *
//     * @param sem
//     * @return
//     */
//    public SpeciesEntryPanel initSpeciesEntry(SpeciesEntryModel sem) {
//        SpeciesEntryPanel se = new SpeciesEntryPanel();
//
//        assert false;
////        se.speciesFileTf.addFocusListener(new SpeciesFileTfController(context, sem));
////        se.browseBtn.addActionListener(new SpeciesFileBtnController(context, sem));
//        se.removeBtn.addActionListener(new ActionListener() {
////        	private final SpeciesEntryModel sem;
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//            	assert false;
////                if (getActiveModel().getAllSpecies().size() == 1) {
////                    JOptionPane.showMessageDialog(getGuiManager().getInpPnl(),
////                        "You must use at least one dataset");
////                    return;
////                }
////                getActiveModel().removeSpecies(sem);
//            }
//		});
//        
//
//        sem.addObserver(se);
//
//        return se;
//    }

//    public FilePanel createOrthEntry(ValueModel<String> oem){
//        FilePanel oe = new FilePanel();
//
//        //add controllers for textfield, browse and remove
//        new FileController("TODO", oem, oe.getPathTextField(), oe.getBrowseButton(), inputPanel);
//
//        return oe;
//    }

    public JobInputModel makeDefaultModel() { // TODO
        return new JobInputModel();
    }

    public void addCurrentModel() {
    	assert false;
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
    }

    public void saveProfiles() throws IOException {
    	assert false;
//    	SettingsIO settingsIO = new SettingsIO(context);
//        settingsIO.writeAllProfiles(allModels);
//        settingsIO.writeAllSpecies(getAllSpeciesEntryModels());
    }

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
