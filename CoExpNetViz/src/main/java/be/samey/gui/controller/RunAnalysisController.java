package be.samey.gui.controller;

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
import be.samey.gui.model.InpPnlModel;
import be.samey.gui.model.InpProfileModel;
import be.samey.gui.model.SpeciesEntryModel;
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author sam
 */
public class RunAnalysisController implements ActionListener {

    private CyAppManager cyAppManager;
    private InpProfileModel inpProfileModel;

    private final String[] numbers = new String[]{"first", "second", "third", "fourth", "fith"};
    private final IllegalArgumentException invalidModelException = new IllegalArgumentException("RunAnalysisController: GuiModel has invalid fields");

    public RunAnalysisController(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
    }

    public RunAnalysisController(InpProfileModel inpProfileModel) {
        this.inpProfileModel = inpProfileModel;
    }

    public void setInpProfileModel(InpProfileModel inpProfileModel) {
        this.inpProfileModel = inpProfileModel;
    }

    @Override
    /**
     * Invoked when the user clicks the "Run analysis" button. All fields from
     * the models are checked for correctness and passed on to the
     * {@link CoreStatus} model. If all checks are OK, then the analysis is run.
     */
    public void actionPerformed(ActionEvent ae) {
        CyModel cyModel = cyAppManager.getCyModel();
        InpPnlModel inpPnlModel = inpProfileModel.getInpPnlModel();
        JPanel parent = cyAppManager.getGuiManager().getInpPnl();

        try {
            sendBaits(inpPnlModel, parent, cyModel);
            sendSpecies(parent, cyModel);
            sendCutOffs(inpPnlModel, cyModel);
            sendSaveFilePath(parent, inpPnlModel, cyModel);
        } catch (IllegalArgumentException ex) {
            return;
        }

        //if all checks were ok, we can run the analysis
        cyAppManager.runAnalysis();
    }

    /**
     * Send baits to CyModel
     */
    private void sendBaits(InpPnlModel inpPnlModel, JPanel parent, CyModel cyModel) throws IllegalArgumentException {
        if (inpPnlModel.isUseBaitFile()) {
            //The user choose to upload a file with bait genes
            //see if the file path is provided
            if (inpPnlModel.getBaitFilePath().toString().trim().length() == 0) {
                JOptionPane.showMessageDialog(parent,
                    "Please enter a baits file or input your baits manually",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                throw invalidModelException;
            }
            //read the baits file
            try {
                Path baitPath = inpPnlModel.getBaitFilePath().toRealPath();
                Charset charset = Charset.forName("UTF-8");
                BufferedReader reader = Files.newBufferedReader(baitPath, charset);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } finally {
                    reader.close();
                }
                cyModel.setBaits(sb.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent,
                    "There was an error while reading the baits file\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                throw invalidModelException;
            }
        } else {
            //The user chose to enter the baits manually
            //TODO: better format checking
            if (inpPnlModel.getBaits().trim().length() == 0) {
                JOptionPane.showMessageDialog(parent,
                    "Please specify your bait genes",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                throw invalidModelException;
            } else {
                cyModel.setBaits(inpPnlModel.getBaits());
            }
        }
    }

    /**
     * Send species data to cyModel
     */
    private void sendSpecies(JPanel parent, CyModel cyModel) {
        List<String> speciesNames = new ArrayList<String>();
        List<Path> speciesPaths = new ArrayList<Path>();

        for (int i = 0; i < inpProfileModel.getSpeciesEntryModels().size(); i++) {
            SpeciesEntryModel sem = inpProfileModel.getSpeciesEntryModel(i);

            //check if species name is given
            if (sem.getSpeciesName().trim().length() == 0) {
                JOptionPane.showMessageDialog(parent,
                    String.format("No species name was given for the %s species", numbers[i]),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                throw invalidModelException;
            }
            speciesNames.add(sem.getSpeciesName());

            //check if path is correct
            if (sem.getSpeciesExprDataPath().toString().trim().length() == 0) {
                JOptionPane.showMessageDialog(parent,
                    String.format("Please specify a path for the %s species", numbers[i]),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                throw invalidModelException;
            }
            Path speciesPath;
            //TODO: better format checking
            try {
                speciesPath = sem.getSpeciesExprDataPath().toRealPath();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent,
                    String.format("There was an error while reading the gene expression file for the %s species\n"
                        + "%s", numbers[i], ex.getMessage()),
                    "Warning",
                    JOptionPane.ERROR_MESSAGE);
                throw invalidModelException;
            }
            speciesPaths.add(speciesPath);
        }

        cyModel.setSpeciesNames(speciesNames.toArray(new String[speciesNames.size()]));
        cyModel.setSpeciesPaths(speciesPaths.toArray(new Path[speciesPaths.size()]));
    }

    /**
     * Send cutoffs to cyModel
     */
    private void sendCutOffs(InpPnlModel inpPnlModel, CyModel cyModel) {
        cyModel.setnCutoff(inpPnlModel.getNegCutoff());
        cyModel.setpCutoff(inpPnlModel.getPosCutoff());
    }

    /**
     * Send output directory to model
     */
    private void sendSaveFilePath(JPanel parent, InpPnlModel inpPnlModel, CyModel cyModel) {
        if (inpPnlModel.isSaveResults()) {
            //the user chose to save the results
            //check if path is correct
            if (inpPnlModel.getSaveFilePath().toString().trim().length() == 0) {
                JOptionPane.showMessageDialog(parent,
                    "No output direcory was given",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                throw invalidModelException;
            }
            Path saveFilePath;
            try {
                saveFilePath = inpPnlModel.getSaveFilePath().toRealPath();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent,
                    "There was an error while accessing the output directory\n" + ex.getMessage(),
                    "Warning",
                    JOptionPane.ERROR_MESSAGE);
                throw invalidModelException;
            }
            cyModel.setSaveFilePath(saveFilePath);
        } else {
            cyModel.setSaveFilePath(null);
        }
    }
}
