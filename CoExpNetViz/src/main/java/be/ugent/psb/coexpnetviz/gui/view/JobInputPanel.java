package be.ugent.psb.coexpnetviz.gui.view;

import be.ugent.psb.coexpnetviz.gui.GUIConstants;
import be.ugent.psb.coexpnetviz.gui.OrthEntry;
import be.ugent.psb.coexpnetviz.gui.SpeciesEntry;

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

import be.ugent.psb.coexpnetviz.gui.model.InputPanelModel;
import be.ugent.psb.coexpnetviz.gui.model.OrthEntryModel;
import be.ugent.psb.coexpnetviz.gui.model.SpeciesEntryModel;
import be.ugent.psb.coexpnetviz.internal.CyAppManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class JobInputPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	
	//settings
    public JPanel settingsPanel; // TODO private + getter
    public JLabel profileLabel;
    public JButton profileLoadButton;
    public JButton profileSaveButton;
    public JButton profileDeleteButton;
    public JButton resetButton;
    //title
    public JLabel titleLabel;
    public JTextField titleTextField;
    public JPanel topPanel;
    //first tab: general settings
    public JPanel tabOnePanel;
    //input baits
    public JRadioButton baitInputRadioButton;
    public JRadioButton baitFileRadioButton;
    public JLabel baitInputLabel;
    public JButton baitInputInfoButton;
    public JTextArea baitInputTextArea;
    public JScrollPane inputBaitScrollPane;
    public JLabel baitFileLabel;
    public JPanel fileBaitPanel;
    public JTextField baitFileTextField;
    public JButton baitFileButton;
    //choose species
    public JLabel chooseSpeciesLabel;
    public JScrollPane chooseSpeciesScrollPane;
    public JPanel chooseSpeciesPanel;
    public JButton addSpeciesButton;
    //choose cutoff
    public JLabel chooseCutoffLabel;
    public JPanel cutoffPanel;
    public JLabel negativeCutoffLabel;
    public JSpinner negativeCutoffSpinner;
    public JLabel positiveCutoffLabel;
    public JSpinner positiveCutoffSpinner;
    //save
    public JCheckBox saveFileCheckBox;
    public JTextField saveFileTextField;
    public JButton saveFileButton;
    public JPanel saveFilePanel;
    //buttons
    public JButton goButton;
    //second tab
    public JPanel tabTwoPanel;
    public JLabel orthLabel;
    public JLabel orthNoFilesChosenLabel;
    public JScrollPane orthFilesScrollPane;
    public JPanel orthFilesPanel;
    public JButton orthAddButton;
    //tabbed pane
    public JTabbedPane tabbedPane;

    private ButtonGroup inputBaitOrfileBaitButtonGroup;
    private SpinnerModel negativeCutoffSpinnerModel;
    private SpinnerModel positiveCutoffSpinnerModel;

    public JobInputPanel() {
        constructGui();
    }

    /**
     * initializes all parts of the gui, this should only be used once and it
     * should be used only in the constructor to prevent problems with broken
     * references.
     */
    private void constructGui() {
        //profiles
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.LINE_AXIS));
        profileLabel = new JLabel("Settings: ");
        profileLoadButton = new JButton("Load");
        profileSaveButton = new JButton("Save");
        profileDeleteButton = new JButton("Delete");
        resetButton = new JButton("Reset form");
        
        //title
        titleLabel = new JLabel("Title (optional)");
        titleTextField = new JTextField();
        topPanel = new JPanel();
        
        //first tab
        tabOnePanel = new JPanel();
        
        //input bait genes or choose file
        baitInputRadioButton = new JRadioButton("Input bait genes");
        baitInputRadioButton.setName("baitInpRb");
        baitFileRadioButton = new JRadioButton("Upload file with bait genes");
        baitFileRadioButton.setName("baitFileRb");
        inputBaitOrfileBaitButtonGroup = new ButtonGroup();
        inputBaitOrfileBaitButtonGroup.add(baitInputRadioButton);
        inputBaitOrfileBaitButtonGroup.add(baitFileRadioButton);
        baitInputLabel = new JLabel("Enter bait genes");
        baitInputInfoButton = new JButton("ID's");
        try {
            ClassLoader classLoader = CyAppManager.class.getClassLoader();
            Image img = ImageIO.read(classLoader.getResource("icons/help-icon16.png"));
            baitInputInfoButton.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
            Logger.getLogger(JobInputPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        baitInputTextArea = new JTextArea();
        baitInputTextArea.setLineWrap(true);
        baitInputTextArea.setToolTipText("Enter gene identifiers seperated by whitespace, eg 'Solyc02g04650'");
        inputBaitScrollPane = new JScrollPane(baitInputTextArea);
        inputBaitScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        baitFileLabel = new JLabel("Choose file with bait genes");
        fileBaitPanel = new JPanel();
        fileBaitPanel.setLayout(new BoxLayout(fileBaitPanel, BoxLayout.LINE_AXIS));
        baitFileTextField = new JTextField();
        baitFileTextField.setToolTipText("Path to file with gene identifiers, gene identifiers must be separated by white space");
        baitFileButton = new JButton("Browse");
        baitFileButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        
        //choose species
        chooseSpeciesLabel = new JLabel("<html>Choose which data sets to use,<br>"
            + "pick one dataset for every species you have specified in the bait genes");
        chooseSpeciesPanel = new JPanel();
        chooseSpeciesPanel.setLayout(new BoxLayout(chooseSpeciesPanel, BoxLayout.Y_AXIS));
        chooseSpeciesScrollPane = new JScrollPane();
        chooseSpeciesScrollPane.setViewportView(chooseSpeciesPanel);
        chooseSpeciesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chooseSpeciesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chooseSpeciesScrollPane.setAlignmentX(JScrollPane.RIGHT_ALIGNMENT);
        addSpeciesButton = new JButton("Add species");
        
        //choose cutoffs
        chooseCutoffLabel = new JLabel("Choose cutoff");
        cutoffPanel = new JPanel();
        cutoffPanel.setLayout(new BoxLayout(cutoffPanel, BoxLayout.LINE_AXIS));
        negativeCutoffLabel = new JLabel("Neg. cutoff");
        negativeCutoffSpinnerModel = new SpinnerNumberModel(0.0, -1.0, 0.0, 0.1);
        negativeCutoffSpinner = new JSpinner(negativeCutoffSpinnerModel);
        negativeCutoffSpinner.setName("nCutoffSp");
        negativeCutoffSpinner.setMaximumSize(new Dimension(64, 32));
        positiveCutoffLabel = new JLabel("Pos. cutoff");
        positiveCutoffSpinnerModel = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
        positiveCutoffSpinner = new JSpinner(positiveCutoffSpinnerModel);
        positiveCutoffSpinner.setName("pCutoffSp");
        positiveCutoffSpinner.setMaximumSize(new Dimension(64, 32));
        
        //save output
        saveFileCheckBox = new JCheckBox("Save output");
        saveFileTextField = new JTextField();
        saveFileTextField.setToolTipText("Path to output directory");
        saveFilePanel = new JPanel();
        saveFilePanel.setLayout(new BoxLayout(saveFilePanel, BoxLayout.LINE_AXIS));
        saveFileButton = new JButton("Browse");
        saveFileButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        
        //buttons
        goButton = new JButton("Run analysis");
        
        //second tab
        tabTwoPanel = new JPanel();
        orthLabel = new JLabel("Choose orhtologous groups (optional)");
        orthNoFilesChosenLabel = new JLabel("No files chosen");
        orthNoFilesChosenLabel.setEnabled(false);
        orthNoFilesChosenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        orthFilesPanel = new JPanel();
        orthFilesPanel.setLayout(new BoxLayout(orthFilesPanel, BoxLayout.Y_AXIS));
        orthFilesScrollPane = new JScrollPane();
        orthFilesScrollPane.setViewportView(orthFilesPanel);
        orthFilesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        orthFilesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        orthFilesScrollPane.setAlignmentX(JScrollPane.RIGHT_ALIGNMENT);
        orthAddButton = new JButton("Add ortholgous groups file");
        
        //tabbed pane
        tabbedPane = new JTabbedPane();

        //create the borderlayout
        setPreferredSize(new Dimension(500, 700));
        setLayout(new BorderLayout());
        
        
        //======================================================================
        //NORTH PANEL
        
        //create topPnl gridbaglayout and add components to it
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints cTop = new GridBagConstraints();
        cTop.anchor = GridBagConstraints.FIRST_LINE_START;
        cTop.weightx = 0.5;
        cTop.fill = GridBagConstraints.HORIZONTAL;
        
        //settings
        cTop.insets = new Insets(0, 0, 0, 0);
        cTop.gridx = 0;
        cTop.gridy = 00;
        cTop.gridwidth = 2;
        settingsPanel.add(profileLabel);
        settingsPanel.add(profileLoadButton);
        settingsPanel.add(profileSaveButton);
        settingsPanel.add(profileDeleteButton);
        topPanel.add(settingsPanel, cTop);
        cTop.fill = GridBagConstraints.NONE;
        cTop.anchor = GridBagConstraints.LAST_LINE_END;
        cTop.insets = new Insets(0, 0, 0, 0);
        cTop.gridx = 2;
        cTop.gridy = 00;
        cTop.gridwidth = 1;
        resetButton.setMaximumSize(new Dimension(32, 32));
        topPanel.add(resetButton, cTop);
        
        //title
        cTop.anchor = GridBagConstraints.FIRST_LINE_START;
        cTop.fill = GridBagConstraints.HORIZONTAL;
        cTop.insets = new Insets(10, 0, 0, 0);
        cTop.gridx = 0;
        cTop.gridy = 10;
        cTop.gridwidth = 1;
        topPanel.add(titleLabel, cTop);
        cTop.insets = new Insets(00, 0, 0, 0);
        cTop.gridx = 0;
        cTop.gridy = 11;
        cTop.gridwidth = 3;
        topPanel.add(titleTextField, cTop);
        add(topPanel, BorderLayout.PAGE_START);
        
        //======================================================================
        //CENTRAL PANEL
        //**********************************************************************
        
        //first tab
        tabOnePanel.setLayout(new GridBagLayout());
        GridBagConstraints cMid = new GridBagConstraints();
        cMid.anchor = GridBagConstraints.FIRST_LINE_START;
        cMid.weightx = 0.5;
        cMid.fill = GridBagConstraints.HORIZONTAL;

        //input bait genes
        //top two radio buttons (input bait genes or file)
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 20;
        cMid.gridwidth = 1;
        tabOnePanel.add(baitInputRadioButton, cMid);
        cMid.gridx = 1;
        cMid.gridy = 20;
        cMid.gridwidth = 1;
        tabOnePanel.add(baitFileRadioButton, cMid);
        
        //input baits or choose file
        //input bait genes label
        cMid.insets = new Insets(0, 0, 0, 0);
        cMid.fill = GridBagConstraints.HORIZONTAL;
        cMid.anchor = GridBagConstraints.LAST_LINE_START;
        cMid.gridx = 0;
        cMid.gridy = 21;
        cMid.gridwidth = 1;
        tabOnePanel.add(baitInputLabel, cMid);
        
        //input bait genes info button
        cMid.fill = GridBagConstraints.NONE;
        cMid.anchor = GridBagConstraints.LAST_LINE_END;
        cMid.gridx = 2;
        cMid.gridy = 21;
        cMid.gridwidth = 1;
        tabOnePanel.add(baitInputInfoButton, cMid);
        
        //bait genes text area
        cMid.fill = GridBagConstraints.HORIZONTAL;
        cMid.anchor = GridBagConstraints.FIRST_LINE_START;
        cMid.weighty = 1.0;
        cMid.ipady = (160);
        cMid.gridx = 0;
        cMid.gridy = 22;
        cMid.gridwidth = 3;
        tabOnePanel.add(inputBaitScrollPane, cMid);
        
        //choose bait genes file label
        cMid.weighty = 0.0;
        cMid.ipady = 0;
        cMid.gridx = 0;
        cMid.gridy = 23;
        cMid.gridwidth = 3;
        tabOnePanel.add(baitFileLabel, cMid);
        
        //bait genes file textfield and button
        cMid.gridx = 0;
        cMid.gridy = 24;
        cMid.gridwidth = 3;
        fileBaitPanel.add(baitFileTextField);
        fileBaitPanel.add(baitFileButton);
        tabOnePanel.add(fileBaitPanel, cMid);
        
        //----------------------------------------------------------------------
        //choose species
        //choose species label
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 30;
        cMid.gridwidth = 3;
        tabOnePanel.add(chooseSpeciesLabel, cMid);
        //choose species scrollpane
        cMid.insets = new Insets(0, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 31;
        cMid.ipady = (160);
        cMid.gridwidth = 3;
        tabOnePanel.add(chooseSpeciesScrollPane, cMid);
        //add species button
        cMid.insets = new Insets(0, 0, 0, 90);
        cMid.gridx = 0;
        cMid.gridy = 32;
        cMid.ipady = (0);
        cMid.gridwidth = 1;
        tabOnePanel.add(addSpeciesButton, cMid);
        
        //----------------------------------------------------------------------
        //choose cutoffs
        
        //choose cutoff label
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 40;
        cMid.gridwidth = 3;
        tabOnePanel.add(chooseCutoffLabel, cMid);
        //cutoff labels and spinners
        cMid.insets = new Insets(0, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 41;
        cMid.gridwidth = 3;
        cutoffPanel.add(negativeCutoffLabel);
        cutoffPanel.add(Box.createRigidArea(new Dimension(5, 0))); //spacer
        cutoffPanel.add(negativeCutoffSpinner);
        cutoffPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        cutoffPanel.add(positiveCutoffLabel);
        cutoffPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        cutoffPanel.add(positiveCutoffSpinner);
        tabOnePanel.add(cutoffPanel, cMid);
        
        //----------------------------------------------------------------------
        //save file
        
        //checkbox
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 50;
        cMid.gridwidth = 1;
        tabOnePanel.add(saveFileCheckBox, cMid);
        
        //save file textfield and button
        cMid.insets = new Insets(0, 0, 10, 0);
        cMid.gridx = 0;
        cMid.gridy = 51;
        cMid.gridwidth = 3;
        saveFilePanel.add(saveFileTextField);
        saveFilePanel.add(saveFileButton);
        tabOnePanel.add(saveFilePanel, cMid);
        
        //**********************************************************************
        //second tab
        
        tabTwoPanel.setLayout(new GridBagLayout());
        GridBagConstraints cOrth = new GridBagConstraints();
        cOrth.anchor = GridBagConstraints.FIRST_LINE_START;
        cOrth.fill = GridBagConstraints.HORIZONTAL;
        cOrth.insets = new Insets(10, 0, 0, 0);
        cOrth.gridx = 0;
        cOrth.gridy = 0;
        tabTwoPanel.add(orthLabel, cOrth);
        cOrth.insets = new Insets(0, 0, 0, 0);
        cOrth.gridx = 0;
        cOrth.gridy = 1;
        cOrth.ipady = 324;
        tabTwoPanel.add(orthFilesScrollPane, cOrth);
        cOrth.insets = new Insets(0, 0, 10, 0);
        cOrth.fill = GridBagConstraints.NONE;
        cOrth.gridx = 0;
        cOrth.gridy = 2;
        cOrth.ipady = 0;
        tabTwoPanel.add(orthAddButton, cOrth);
        cOrth.fill = GridBagConstraints.BOTH;
        cOrth.weightx = 1.0;
        cOrth.weighty = 1.0;
        cOrth.gridx = 0;
        cOrth.gridy = 100;
        tabTwoPanel.add(Box.createGlue(), cOrth);
        
        //**********************************************************************
        //tabbed pane
        tabbedPane.addTab("General settings", tabOnePanel);
        tabbedPane.addTab("Gene family options", tabTwoPanel);
        add(tabbedPane, BorderLayout.CENTER);
        
        //======================================================================
        //SOUTH PANEL
        JPanel botPnl = new JPanel();
        botPnl.setLayout(new BoxLayout(botPnl, BoxLayout.LINE_AXIS));
        
        //go button
        botPnl.add(goButton);
        add(botPnl, BorderLayout.PAGE_END);
    }

    @Override
    /**
     * Triggered when the {@link InpPnlModel} notifies its observers. Updates
     * all components statuses to match the {@link InpPnlModel}. Don't trigger a
     * InpPnlModel update from within this method, that would start an endless
     * loop.
     */
    public void update(Observable model, Object obj) {

        if (model instanceof InputPanelModel) {

            InputPanelModel inpPnlModel = (InputPanelModel) model;

            //update: string input fields
            titleTextField.setText(inpPnlModel.getTitle());

            //update: baits
            boolean useBaitFile = inpPnlModel.isUseBaitFile();
            baitInputRadioButton.setSelected(!useBaitFile);
            baitFileRadioButton.setSelected(useBaitFile);
            baitInputLabel.setEnabled(!useBaitFile);
            baitInputTextArea.setEnabled(!useBaitFile);
            String baits = inpPnlModel.getBaits();
            baitInputTextArea.setText(baits);
            baitFileLabel.setEnabled(useBaitFile);
            baitFileTextField.setEnabled(useBaitFile);
            Path baitFilePath = inpPnlModel.getBaitFilePath();
            baitFileTextField.setText(baitFilePath.toString());
            baitFileTextField.setBackground(
                Files.isRegularFile(baitFilePath) && Files.isReadable(baitFilePath)
                    ? GUIConstants.APPROVE_COLOR : GUIConstants.DISAPPROVE_COLOR);
            baitFileButton.setEnabled(useBaitFile);

            //update: species
            //get currently visible species
            List<SpeciesEntry> seList = new ArrayList<SpeciesEntry>();
            for (Component comp : chooseSpeciesPanel.getComponents()) {
                if (comp instanceof SpeciesEntry) {
                    SpeciesEntry se = (SpeciesEntry) comp;
                    seList.add(se);
                }
            }
            //remove species that are not in the model
            for (SpeciesEntry se : seList) {
                if (!inpPnlModel.getAllSpecies().values().contains(se)) {
                    chooseSpeciesPanel.remove(se);
                }
            }
            //add new species from the model
            for (SpeciesEntryModel sem : inpPnlModel.getAllSpecies().keySet()) {
                SpeciesEntry se = inpPnlModel.getAllSpecies().get(sem);
                if (!seList.contains(se)) {
                    se.setAlignmentX(Component.LEFT_ALIGNMENT);
                    chooseSpeciesPanel.add(se);
                    sem.triggerUpdate();
                }
            }

            chooseSpeciesPanel.revalidate();
            chooseSpeciesPanel.repaint();

            //update: cutoffs
            negativeCutoffSpinner.setValue(inpPnlModel.getNegCutoff());
            positiveCutoffSpinner.setValue(inpPnlModel.getPosCutoff());

            //update: save
            boolean saveResults = inpPnlModel.isSaveResults();
            saveFileCheckBox.setSelected(saveResults);
            saveFileTextField.setEnabled(saveResults);
            Path saveFilePath = inpPnlModel.getSaveFilePath();
            saveFileTextField.setText(saveFilePath.toString());
            saveFileTextField.setBackground(
                Files.isDirectory(saveFilePath)
                && Files.isReadable(saveFilePath)
                && !saveFilePath.toString().trim().isEmpty()
                    ? GUIConstants.APPROVE_COLOR : GUIConstants.DISAPPROVE_COLOR);
            saveFileButton.setEnabled(saveResults);

            //update: orthGroups
            if (inpPnlModel.getAllOrthGroups().isEmpty()) {
                orthFilesScrollPane.setViewportView(orthNoFilesChosenLabel);
            } else {
                orthFilesScrollPane.setViewportView(orthFilesPanel);

                //get currently visible orthGroups
                List<OrthEntry> oeList = new ArrayList<OrthEntry>();
                for (Component comp : orthFilesPanel.getComponents()) {
                    if (comp instanceof OrthEntry) {
                        OrthEntry oe = (OrthEntry) comp;
                        oeList.add(oe);
                    }
                }
                //remove orthGroups that are not in the model
                for (OrthEntry oe : oeList) {
                    if (!inpPnlModel.getAllOrthGroups().values().contains(oe)) {
                        orthFilesPanel.remove(oe);
                    }
                }
                //add new orthGroups from the model
                for (OrthEntryModel oem : inpPnlModel.getAllOrthGroups().keySet()) {
                    OrthEntry oe = inpPnlModel.getOrthEntry(oem);
                    if (!oeList.contains(oe)) {
                        oe.setAlignmentX(Component.LEFT_ALIGNMENT);
                        orthFilesPanel.add(oe);
                        oem.triggerUpdate();
                    }
                }
            }

            orthFilesPanel.revalidate();
            orthFilesPanel.repaint();
        }

    }

}
