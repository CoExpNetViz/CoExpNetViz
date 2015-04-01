package be.samey.gui;

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
import be.samey.gui.model.SpeciesEntryModel;
import be.samey.gui.model.InpPnlModel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

/**
 *
 * @author sam
 */
public class InpPnl extends JPanel implements Observer {

    //input baits
    JRadioButton baitInpRb;
    JRadioButton baitFileRb;
    JLabel baitInpLbl;
    JTextArea baitInpTa;
    JScrollPane inpBaitSp;
    JLabel baitFileLbl;
    JPanel fileBaitPnl;
    JTextField baitFileTf;
    JButton baitFileBtn;
    //choose species
    JLabel chooseSpeciesLbl;
    JScrollPane chooseSpeciesSp;
    JPanel chooseSpeciesPnl;
    JButton addSpeciesBtn;
    //choose cutoff
    JLabel chooseCutoffLbl;
    JPanel cutoffPnl;
    JLabel nCutoffLbl;
    JSpinner nCutoffSp;
    JLabel pCutoffLbl;
    JSpinner pCutoffSp;
    JCheckBox saveFileChb;
    JTextField saveFileTf;
    JButton saveFileBtn;
    JPanel saveFilePnl;
    JButton goBtn;
    JButton resetBtn;

    private ButtonGroup inpBaitOrfileBaitBg;
    private SpinnerModel nCutoffSm;
    private SpinnerModel pCutoffSm;

    public InpPnl() {
        constructGui();
    }

    /**
     * initializes all parts of the gui, this should only be used once and it
     * should be used only in the constructor to prevent problems with broken
     * references.
     */
    private void constructGui() {
        //input bait genes or choose file
        baitInpRb = new JRadioButton("Input bait genes");
        baitInpRb.setName("baitInpRb");
        baitFileRb = new JRadioButton("Upload file with bait genes");
        baitFileRb.setName("baitFileRb");
        inpBaitOrfileBaitBg = new ButtonGroup();
        inpBaitOrfileBaitBg.add(baitInpRb);
        inpBaitOrfileBaitBg.add(baitFileRb);
        baitInpLbl = new JLabel("Enter bait genes");
        baitInpTa = new JTextArea();
        baitInpTa.setLineWrap(true);
        baitInpTa.setToolTipText("Enter gene identifiers seperated by whitespace, eg 'Solyc02g04650'");
        inpBaitSp = new JScrollPane(baitInpTa);
        inpBaitSp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        baitFileLbl = new JLabel("Choose file with bait genes");
        fileBaitPnl = new JPanel();
        fileBaitPnl.setLayout(new BoxLayout(fileBaitPnl, BoxLayout.LINE_AXIS));
        baitFileTf = new JTextField();
        baitFileTf.setToolTipText("Path to file with gene identifiers, gene identifiers must be separated by white space");
        baitFileBtn = new JButton("Browse");
        baitFileBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        //choose species
        chooseSpeciesLbl = new JLabel("<html>Choose which data sets to use,<br>"
            + "pick one dataset for every species you have specified in the bait genes");
        chooseSpeciesPnl = new JPanel();
        chooseSpeciesPnl.setLayout(new BoxLayout(chooseSpeciesPnl, BoxLayout.Y_AXIS));
        chooseSpeciesSp = new JScrollPane();
        chooseSpeciesSp.setViewportView(chooseSpeciesPnl);
        chooseSpeciesSp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chooseSpeciesSp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chooseSpeciesSp.setAlignmentX(JScrollPane.RIGHT_ALIGNMENT);
        addSpeciesBtn = new JButton("Add species");
        //choose cutoffs
        chooseCutoffLbl = new JLabel("Choose cutoff");
        cutoffPnl = new JPanel();
        cutoffPnl.setLayout(new BoxLayout(cutoffPnl, BoxLayout.LINE_AXIS));
        nCutoffLbl = new JLabel("Neg. cutoff");
        nCutoffSm = new SpinnerNumberModel(0.0, -1.0, 0.0, 0.1);
        nCutoffSp = new JSpinner(nCutoffSm);
        nCutoffSp.setName("nCutoffSp");
        nCutoffSp.setMaximumSize(new Dimension(64, 32));
        pCutoffLbl = new JLabel("Pos. cutoff");
        pCutoffSm = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        pCutoffSp = new JSpinner(pCutoffSm);
        pCutoffSp.setName("pCutoffSp");
        pCutoffSp.setMaximumSize(new Dimension(64, 32));
        //save output
        saveFileChb = new JCheckBox("Save output");
        saveFileTf = new JTextField();
        saveFileTf.setToolTipText("Path to output directory");
        saveFilePnl = new JPanel();
        saveFilePnl.setLayout(new BoxLayout(saveFilePnl, BoxLayout.LINE_AXIS));
        saveFileBtn = new JButton("Browse");
        saveFileBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        //run analysis or reset form
        goBtn = new JButton("Run analysis");
        resetBtn = new JButton("Reset form");

        //create gridbaglayout and add components to it
        setPreferredSize(new Dimension(500, 640));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.5;
        //----------------------------------------------------------------------
        //input bait genes
        //top two radio buttons (input bait genes or file)
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 00;
        c.gridwidth = 1;
        add(baitInpRb, c);
        c.gridx = 1;
        c.gridy = 00;
        c.gridwidth = 1;
        add(baitFileRb, c);
        //input baits or choose file
        //input bait genes label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 01;
        c.gridwidth = 3;
        add(baitInpLbl, c);
        //bait genes text area
        c.insets = new Insets(0, 0, 0, 0);
        c.weighty = 1.0;
        c.ipady = (160);
        c.gridx = 0;
        c.gridy = 02;
        c.gridwidth = 3;
        add(inpBaitSp, c);
        //choose bait genes file label
        c.weighty = 0.0;
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 03;
        c.gridwidth = 3;
        add(baitFileLbl, c);
        //bait genes file textfield and button
        c.gridx = 0;
        c.gridy = 04;
        c.gridwidth = 3;
        fileBaitPnl.add(baitFileTf);
        fileBaitPnl.add(baitFileBtn);
        add(fileBaitPnl, c);
        //----------------------------------------------------------------------
        //choose species
        //choose species label
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 3;
        add(chooseSpeciesLbl, c);
        //choose species scrollpane
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 11;
        c.ipady = (160);
        c.gridwidth = 3;
        add(chooseSpeciesSp, c);
        //add species button
        c.insets = new Insets(0, 0, 0, 90);
        c.gridx = 0;
        c.gridy = 12;
        c.ipady = (0);
        c.gridwidth = 1;
        add(addSpeciesBtn, c);
        //----------------------------------------------------------------------
        //choose cutoffs
        //choose cutoff label
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 20;
        c.gridwidth = 3;
        add(chooseCutoffLbl, c);
        //cutoff labels and spinners
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 21;
        c.gridwidth = 3;
        cutoffPnl.add(nCutoffLbl);
        cutoffPnl.add(Box.createRigidArea(new Dimension(5, 0))); //spacer
        cutoffPnl.add(nCutoffSp);
        cutoffPnl.add(Box.createRigidArea(new Dimension(10, 0)));
        cutoffPnl.add(pCutoffLbl);
        cutoffPnl.add(Box.createRigidArea(new Dimension(5, 0)));
        cutoffPnl.add(pCutoffSp);
        add(cutoffPnl, c);
        //----------------------------------------------------------------------
        //save file
        //checkbox
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 30;
        c.gridwidth = 1;
        add(saveFileChb, c);
        //save file textfield and button
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 31;
        c.gridwidth = 3;
        saveFilePnl.add(saveFileTf);
        saveFilePnl.add(saveFileBtn);
        add(saveFilePnl, c);
        //go and clear buttons
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 32;
        c.gridwidth = 1;
        add(goBtn, c);
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.gridx = 2;
        c.gridy = 32;
        c.gridwidth = 1;
        add(resetBtn, c);
    }

    @Override
    /**
     * Triggered when the {@link InpPnlModel} notifies its observers. Updates
     * all components statuses to match the {@link InpPnlModel}. Don't trigger a
     * InpPnlModel update from within this method, that would start an endless
     * loop.
     */
    public void update(Observable model, Object obj) {

        //for debugging
//        System.out.println("InpPanel" + " updated from: " + model);
        if (model instanceof InpPnlModel) {

            //for debugging
//            System.out.println("InpPanel" + " updated from inputPnlModel");
            InpPnlModel inpPnlModel = (InpPnlModel) model;

            //update: string input fields
            String jobTitle = inpPnlModel.getJobTitle();
            //TODO make jobtitle field

            //update: baits
            boolean useBaitFile = inpPnlModel.isUseBaitFile();
            baitInpRb.setSelected(!useBaitFile);
            baitFileRb.setSelected(useBaitFile);
            baitInpLbl.setEnabled(!useBaitFile);
            baitInpTa.setEnabled(!useBaitFile);
            String baits = inpPnlModel.getBaits();
            baitInpTa.setText(baits);
            baitFileLbl.setEnabled(useBaitFile);
            baitFileTf.setEnabled(useBaitFile);
            Path baitFilePath = inpPnlModel.getBaitFilePath();
            baitFileTf.setText(baitFilePath.toString());
            baitFileTf.setBackground(
                Files.isRegularFile(baitFilePath) && Files.isReadable(baitFilePath)
                    ? GuiConstants.APPROVE_COLOR : GuiConstants.DISAPPROVE_COLOR);
            baitFileBtn.setEnabled(useBaitFile);

            //update: species
            //get currently visible species
            List<SpeciesEntry> seList = new ArrayList<SpeciesEntry>();
            for (Component comp : chooseSpeciesPnl.getComponents()) {
                if (comp instanceof SpeciesEntry) {
                    SpeciesEntry se = (SpeciesEntry) comp;
                    seList.add(se);
                }
            }
            //remove species that are not in the model
            for (SpeciesEntry se : seList) {
                if (!inpPnlModel.getAllSpecies().values().contains(se)) {
                    chooseSpeciesPnl.remove(se);
                }
            }
            //add new species from the model
            for (SpeciesEntryModel sem : inpPnlModel.getAllSpecies().keySet()) {
                SpeciesEntry se = inpPnlModel.getAllSpecies().get(sem);
                if (!seList.contains(se)) {
                    se.setAlignmentX(Component.LEFT_ALIGNMENT);
                    chooseSpeciesPnl.add(se);
                    sem.triggerUpdate();
                }
            }

            chooseSpeciesPnl.revalidate();
            chooseSpeciesPnl.repaint();

            //update: cutoffs
            nCutoffSp.setValue(inpPnlModel.getNegCutoff());
            pCutoffSp.setValue(inpPnlModel.getPosCutoff());

            //update: save
            boolean saveResults = inpPnlModel.isSaveResults();
            saveFileChb.setSelected(saveResults);
            saveFileTf.setEnabled(saveResults);
            Path saveFilePath = inpPnlModel.getSaveFilePath();
            saveFileTf.setText(saveFilePath.toString());
            saveFileTf.setBackground(
                Files.isDirectory(saveFilePath)
                && Files.isReadable(saveFilePath)
                && !saveFilePath.toString().trim().isEmpty()
                    ? GuiConstants.APPROVE_COLOR : GuiConstants.DISAPPROVE_COLOR);
            saveFileBtn.setEnabled(saveResults);
        }

    }

}
