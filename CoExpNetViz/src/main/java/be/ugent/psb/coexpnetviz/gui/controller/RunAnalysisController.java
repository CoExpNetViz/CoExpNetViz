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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author sam
 */
public class RunAnalysisController implements ActionListener {

    private final IllegalArgumentException invalidModelException = new IllegalArgumentException("RunAnalysisController: GuiModel has invalid fields");

    public RunAnalysisController() {
    }

    @Override
    /**
     * Invoked when the user clicks the "Run analysis" button. All fields from
     * the models are checked for correctness and passed on to the
     * {@link CoreStatus} model. If all checks are OK, then the analysis is run.
     */
    public void actionPerformed(ActionEvent ae) {
    	assert false;
//        JPanel parent = getGuiManager().getInpPnl();
//
//        try {
//            sendTitle();
//            sendBaits(parent);
//            sendSpecies(parent);
//            sendCutOffs();
//            sendSaveFilePath(parent);
//            sendOrthGroups(parent);
//        } catch (IllegalArgumentException ex) {
//            return;
//        }
//
//        // Run the analysis
//        new RunAnalysisTaskController(application);
    }

//    private void sendTitle() {
//        if (getActiveModel().getTitle().trim().isEmpty()) {
//            cyModel.setTitle(CENVContext.APP_NAME + "_" + CENVContext.getTimeStamp());
//        } else {
//            cyModel.setTitle(getActiveModel().getTitle());
//        }
//    }
//
//    /**
//     * Send baits to CyModel
//     */
//    private void sendBaits(JPanel parent) throws IllegalArgumentException {
//        if (getActiveModel().isUseBaitsFile()) {
//            //The user choose to upload a file with bait genes
//            //see if the file path is provided
//            if (getActiveModel().getBaitsFilePath().toString().trim().length() == 0) {
//                JOptionPane.showMessageDialog(parent,
//                    "Please enter a baits file or input your baits manually",
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            }
//            cyModel.setBaitsFilePath(getActiveModel().getBaitsFilePath());
//        } else {
//            //The user chose to enter the baits manually
//            //TODO: better format checking
//            if (getActiveModel().getBaits().trim().length() == 0) {
//                JOptionPane.showMessageDialog(parent,
//                    "Please specify your bait genes",
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            } else {
//                cyModel.setBaits(getActiveModel().getBaits());
//            }
//        }
//    }
//
//    /**
//     * Send species data to cyModel
//     */
//    private void sendSpecies(JPanel parent) {
//        Map<String, Path> expressionMatrices = new HashMap<String, Path>();
//    	SpeciesEntryModel[] SpeciesEntryModels = getAllSpecies().keySet().toArray(new SpeciesEntryModel[getAllSpecies().size()]);
//        for (SpeciesEntryModel matrix : SpeciesEntryModels) {
//            //check if species name is given
//            String speciesName = matrix.getSpeciesName().trim();
//        	if (speciesName.isEmpty()) {
//                JOptionPane.showMessageDialog(parent,
//                    "No species name was given",
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            }
//            
//            // check whether the name is unique
//            if (expressionMatrices.containsKey(speciesName)) {
//	        	JOptionPane.showMessageDialog(parent,
//	                    String.format("The species name, '%s', is not unique", speciesName),
//	                    "Warning",
//	                    JOptionPane.WARNING_MESSAGE);
//	                throw invalidModelException;
//            }
//
//            //check if path is correct
//            if (matrix.getSpeciesFilePath().toString().trim().length() == 0) {
//                JOptionPane.showMessageDialog(parent,
//                    String.format("Please specify a path for species '%s'", speciesName),
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            }
//            Path speciesPath;
//            try {
//                speciesPath = matrix.getSpeciesFilePath().toRealPath(); //TODO: better format checking
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(parent,
//                    String.format("Could not read the gene expression file for species '%s'\n"
//                        + "%s", speciesName, ex.getMessage()),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//                throw invalidModelException;
//            }
//            
//            expressionMatrices.put(speciesName, speciesPath);
//        }
//
//        cyModel.setExpressionMatrices(expressionMatrices);
//    }
//
//    /**
//     * Send cutoffs to cyModel
//     */
//    private void sendCutOffs() {
//        cyModel.setNegativeCutoff(getActiveModel().getNegCutoff());
//        cyModel.setPositiveCutoff(getActiveModel().getPosCutoff());
//    }
//
//    /**
//     * Send output directory to model
//     */
//    private void sendSaveFilePath(JPanel parent) {
//        if (getActiveModel().isSaveResults()) {
//            //the user chose to save the results
//            //check if path is correct
//            if (getActiveModel().getSaveFilePath().toString().trim().length() == 0) {
//                JOptionPane.showMessageDialog(parent,
//                    "No output direcory was given",
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            }
//            Path saveFilePath;
//            try {
//                saveFilePath = getActiveModel().getSaveFilePath().toRealPath();
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(parent,
//                    "There was an error while accessing the output directory\n" + ex.getMessage(),
//                    "Warning",
//                    JOptionPane.ERROR_MESSAGE);
//                throw invalidModelException;
//            }
//            cyModel.setSaveFilePath(saveFilePath);
//        } else {
//            cyModel.setSaveFilePath(null);
//        }
//    }
//
//    private void sendOrthGroups(JPanel parent) {
//        Map<String, Path> geneFamilies = new HashMap<String, Path>();
//
//        OrthEntryModel[] orthEntryModels = getAllOrthGroups().keySet().toArray(new OrthEntryModel[getAllOrthGroups().size()]);
//        for (OrthEntryModel oem : orthEntryModels) {
//            //check if orthGroup name is given
//            String name = oem.getOrthGroupName().trim();
//        	if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(parent,
//                    "No name was given for the orthologous group file",
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            }
//        	
//        	// Check for unique name
//        	if (geneFamilies.containsKey(name)) {
//                JOptionPane.showMessageDialog(parent,
//                    String.format("'%s' is not a unique name for the orthologous group file", name),
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            }
//
//            //check if path is correct
//            if (oem.getOrthEntryPath().toString().trim().length() == 0) {
//                JOptionPane.showMessageDialog(parent,
//                    String.format("No path was given for '%s', the orthologous group file", name),
//                    "Warning",
//                    JOptionPane.WARNING_MESSAGE);
//                throw invalidModelException;
//            }
//            
//            Path orthPath;
//            try {
//                orthPath = oem.getOrthEntryPath().toRealPath(); //TODO: better format checking
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(parent,
//                    String.format("Could not read orthologous group file, corresponding to '%s'\n"
//                        + "%s", name, ex.getMessage()),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//                throw invalidModelException;
//            }
//            geneFamilies.put(name, orthPath);
//        }
//
//        cyModel.setGeneFamilies(geneFamilies);
//
//    }

}
