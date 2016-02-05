package be.ugent.psb.coexpnetviz.gui.view;

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

import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;

import be.ugent.psb.util.mvc.view.FilePanel;

//TODO clear form button should be on presets row probably

public class JobInputPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	
	// Presets
    public JPanel presetsPanel; // TODO private + getter
    public JLabel presetsLabel;
    public JComboBox<String> presetComboBox;
    public JButton presetLoadButton;
    public JButton presetSaveButton;
    public JButton presetDeleteButton;
    public JButton clearFormButton;
    
    //
    public JPanel topPanel;
    //first tab: general settings
    public JPanel tabOnePanel;

    // Bait group input
    public JRadioButton baitGroupOptionText;
    public JRadioButton baitGroupOptionFile;
    public ButtonGroup baitGroupOptions; // TODO add to group
    public JLabel baitInputLabel;
    public JButton baitInputInfoButton;
    public JTextArea baitGroupTextArea;
    public FilePanel baitFilePanel;
    public JScrollPane inputBaitScrollPane;
    
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
    //tabbed pane
    public JTabbedPane tabbedPane;

    private ButtonGroup inputBaitOrfileBaitButtonGroup;
    private SpinnerModel negativeCutoffSpinnerModel;
    private SpinnerModel positiveCutoffSpinnerModel;

    public JobInputPanel() {
        //profiles
//        presetsPanel = new JPanel();
//        presetsPanel.setLayout(new BoxLayout(presetsPanel, BoxLayout.LINE_AXIS)); // XXX flowlayout not good enough?
//        presetsLabel = new JLabel("Presets: ");
//        presetLoadButton = new JButton("Load");
//        presetSaveButton = new JButton("Save");
//        presetDeleteButton = new JButton("Delete current");
//        clearFormButton = new JButton("Clear form");
//        
//        //
//        topPanel = new JPanel();
//        
//        //first tab
//        tabOnePanel = new JPanel();
//        
//        //input bait genes or choose file
//        baitGroupOptionText = new JRadioButton("Input bait genes");
//        baitGroupOptionText.setName("baitInpRb");
//        baitGroupOptionFile = new JRadioButton("Upload file with bait genes");
//        baitGroupOptionFile.setName("baitFileRb");
//        inputBaitOrfileBaitButtonGroup = new ButtonGroup();
//        inputBaitOrfileBaitButtonGroup.add(baitGroupOptionText);
//        inputBaitOrfileBaitButtonGroup.add(baitGroupOptionFile);
//        baitInputLabel = new JLabel("Enter bait genes");
//        baitInputInfoButton = new JButton("ID's");
//        try {
//            ClassLoader classLoader = CENVContext.class.getClassLoader();
//            Image img = ImageIO.read(classLoader.getResource("icons/help-icon16.png"));
//            baitInputInfoButton.setIcon(new ImageIcon(img));
//        } catch (IOException ex) {
//            Logger.getLogger(JobInputPanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        baitGroupTextArea = new JTextArea();
//        baitGroupTextArea.setLineWrap(true);
//        baitGroupTextArea.setToolTipText("Enter gene identifiers seperated by whitespace, eg 'Solyc02g04650'");
//        inputBaitScrollPane = new JScrollPane(baitGroupTextArea);
//        inputBaitScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        baitFileLabel = new JLabel("Choose file with bait genes");
//        fileBaitPanel = new JPanel();
//        fileBaitPanel.setLayout(new BoxLayout(fileBaitPanel, BoxLayout.LINE_AXIS));
//        baitGroupFileTextField = new JTextField();
//        baitGroupFileTextField.setToolTipText("Path to file with gene identifiers, gene identifiers must be separated by white space");
//        baitFileButton = new JButton("Browse");
//        baitFileButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
//        
//        //choose species
//        chooseSpeciesLabel = new JLabel("<html>Choose which data sets to use,<br>"
//            + "pick one dataset for every species you have specified in the bait genes");
//        chooseSpeciesPanel = new JPanel();
//        chooseSpeciesPanel.setLayout(new BoxLayout(chooseSpeciesPanel, BoxLayout.Y_AXIS));
//        chooseSpeciesScrollPane = new JScrollPane();
//        chooseSpeciesScrollPane.setViewportView(chooseSpeciesPanel);
//        chooseSpeciesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        chooseSpeciesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        chooseSpeciesScrollPane.setAlignmentX(JScrollPane.RIGHT_ALIGNMENT);
//        addSpeciesButton = new JButton("Add species");
//        
//        //choose cutoffs
//        chooseCutoffLabel = new JLabel("Choose cutoff");
//        cutoffPanel = new JPanel();
//        cutoffPanel.setLayout(new BoxLayout(cutoffPanel, BoxLayout.LINE_AXIS));
//        negativeCutoffLabel = new JLabel("Neg. cutoff");
//        negativeCutoffSpinnerModel = new SpinnerNumberModel(0.0, -1.0, 0.0, 0.1);
//        negativeCutoffSpinner = new JSpinner(negativeCutoffSpinnerModel);
//        negativeCutoffSpinner.setName("nCutoffSp");
//        negativeCutoffSpinner.setMaximumSize(new Dimension(64, 32));
//        positiveCutoffLabel = new JLabel("Pos. cutoff");
//        positiveCutoffSpinnerModel = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
//        positiveCutoffSpinner = new JSpinner(positiveCutoffSpinnerModel);
//        positiveCutoffSpinner.setName("pCutoffSp");
//        positiveCutoffSpinner.setMaximumSize(new Dimension(64, 32));
//        
//        //save output
//        saveFileCheckBox = new JCheckBox("Save output");
//        saveFileTextField = new JTextField();
//        saveFileTextField.setToolTipText("Path to output directory");
//        saveFilePanel = new JPanel();
//        saveFilePanel.setLayout(new BoxLayout(saveFilePanel, BoxLayout.LINE_AXIS));
//        saveFileButton = new JButton("Browse");
//        saveFileButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
//        
//        //buttons
//        goButton = new JButton("Run analysis");
//        
//        //second tab
//        tabTwoPanel = new JPanel();
//        orthLabel = new JLabel("Choose orhtologous groups (optional)");
//        orthNoFilesChosenLabel = new JLabel("No files chosen");
//        orthNoFilesChosenLabel.setEnabled(false);
//        orthNoFilesChosenLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        orthFilesPanel = new JPanel();
//        orthFilesPanel.setLayout(new BoxLayout(orthFilesPanel, BoxLayout.Y_AXIS));
//        orthFilesScrollPane = new JScrollPane();
//        orthFilesScrollPane.setViewportView(orthFilesPanel);
//        orthFilesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        orthFilesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//        orthFilesScrollPane.setAlignmentX(JScrollPane.RIGHT_ALIGNMENT);
//        orthAddButton = new JButton("Add ortholgous groups file");
//        
//        //tabbed pane
//        tabbedPane = new JTabbedPane();
//
//        //create the borderlayout
//        setPreferredSize(new Dimension(500, 700));
//        setLayout(new BorderLayout());
//        
//        
//        //======================================================================
//        //NORTH PANEL
//        
//        //create topPnl gridbaglayout and add components to it
//        topPanel.setLayout(new GridBagLayout());
//        GridBagConstraints cTop = new GridBagConstraints();
//        cTop.anchor = GridBagConstraints.FIRST_LINE_START;
//        cTop.weightx = 0.5;
//        cTop.fill = GridBagConstraints.HORIZONTAL;
//        
//        //settings
//        cTop.insets = new Insets(0, 0, 0, 0);
//        cTop.gridx = 0;
//        cTop.gridy = 00;
//        cTop.gridwidth = 2;
//        presetsPanel.add(presetsLabel);
//        presetsPanel.add(presetLoadButton);
//        presetsPanel.add(profileSaveButton);
//        presetsPanel.add(presetDeleteButton);
//        topPanel.add(presetsPanel, cTop);
//        cTop.fill = GridBagConstraints.NONE;
//        cTop.anchor = GridBagConstraints.LAST_LINE_END;
//        cTop.insets = new Insets(0, 0, 0, 0);
//        cTop.gridx = 2;
//        cTop.gridy = 00;
//        cTop.gridwidth = 1;
//        clearFormButton.setMaximumSize(new Dimension(32, 32));
//        topPanel.add(clearFormButton, cTop);
//        
//        //title
//        cTop.anchor = GridBagConstraints.FIRST_LINE_START;
//        cTop.fill = GridBagConstraints.HORIZONTAL;
//        cTop.insets = new Insets(10, 0, 0, 0);
//        cTop.gridx = 0;
//        cTop.gridy = 10;
//        cTop.gridwidth = 1;
//        topPanel.add(titleLabel, cTop);
//        cTop.insets = new Insets(0, 0, 0, 0);
//        cTop.gridx = 0;
//        cTop.gridy = 11;
//        cTop.gridwidth = 3;
//        topPanel.add(titleTextField, cTop);
//        add(topPanel, BorderLayout.PAGE_START);
//        
//        //======================================================================
//        //CENTRAL PANEL
//        //**********************************************************************
//        
//        //first tab
//        tabOnePanel.setLayout(new GridBagLayout());
//        GridBagConstraints cMid = new GridBagConstraints();
//        cMid.anchor = GridBagConstraints.FIRST_LINE_START;
//        cMid.weightx = 0.5;
//        cMid.fill = GridBagConstraints.HORIZONTAL;
//
//        //input bait genes
//        //top two radio buttons (input bait genes or file)
//        cMid.insets = new Insets(10, 0, 0, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 20;
//        cMid.gridwidth = 1;
//        tabOnePanel.add(baitGroupOptionText, cMid);
//        cMid.gridx = 1;
//        cMid.gridy = 20;
//        cMid.gridwidth = 1;
//        tabOnePanel.add(baitGroupOptionFile, cMid);
//        
//        //input baits or choose file
//        //input bait genes label
//        cMid.insets = new Insets(0, 0, 0, 0);
//        cMid.fill = GridBagConstraints.HORIZONTAL;
//        cMid.anchor = GridBagConstraints.LAST_LINE_START;
//        cMid.gridx = 0;
//        cMid.gridy = 21;
//        cMid.gridwidth = 1;
//        tabOnePanel.add(baitInputLabel, cMid);
//        
//        //input bait genes info button
//        cMid.fill = GridBagConstraints.NONE;
//        cMid.anchor = GridBagConstraints.LAST_LINE_END;
//        cMid.gridx = 2;
//        cMid.gridy = 21;
//        cMid.gridwidth = 1;
//        tabOnePanel.add(baitInputInfoButton, cMid);
//        
//        //bait genes text area
//        cMid.fill = GridBagConstraints.HORIZONTAL;
//        cMid.anchor = GridBagConstraints.FIRST_LINE_START;
//        cMid.weighty = 1.0;
//        cMid.ipady = (160);
//        cMid.gridx = 0;
//        cMid.gridy = 22;
//        cMid.gridwidth = 3;
//        tabOnePanel.add(inputBaitScrollPane, cMid);
//        
//        //choose bait genes file label
//        cMid.weighty = 0.0;
//        cMid.ipady = 0;
//        cMid.gridx = 0;
//        cMid.gridy = 23;
//        cMid.gridwidth = 3;
//        tabOnePanel.add(baitFileLabel, cMid);
//        
//        //bait genes file textfield and button
//        cMid.gridx = 0;
//        cMid.gridy = 24;
//        cMid.gridwidth = 3;
//        fileBaitPanel.add(baitGroupFileTextField);
//        fileBaitPanel.add(baitFileButton);
//        tabOnePanel.add(fileBaitPanel, cMid);
//        
//        //----------------------------------------------------------------------
//        //choose species
//        //choose species label
//        cMid.insets = new Insets(10, 0, 0, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 30;
//        cMid.gridwidth = 3;
//        tabOnePanel.add(chooseSpeciesLabel, cMid);
//        //choose species scrollpane
//        cMid.insets = new Insets(0, 0, 0, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 31;
//        cMid.ipady = (160);
//        cMid.gridwidth = 3;
//        tabOnePanel.add(chooseSpeciesScrollPane, cMid);
//        //add species button
//        cMid.insets = new Insets(0, 0, 0, 90);
//        cMid.gridx = 0;
//        cMid.gridy = 32;
//        cMid.ipady = (0);
//        cMid.gridwidth = 1;
//        tabOnePanel.add(addSpeciesButton, cMid);
//        
//        //----------------------------------------------------------------------
//        //choose cutoffs
//        
//        //choose cutoff label
//        cMid.insets = new Insets(10, 0, 0, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 40;
//        cMid.gridwidth = 3;
//        tabOnePanel.add(chooseCutoffLabel, cMid);
//        //cutoff labels and spinners
//        cMid.insets = new Insets(0, 0, 0, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 41;
//        cMid.gridwidth = 3;
//        cutoffPanel.add(negativeCutoffLabel);
//        cutoffPanel.add(Box.createRigidArea(new Dimension(5, 0))); //spacer
//        cutoffPanel.add(negativeCutoffSpinner);
//        cutoffPanel.add(Box.createRigidArea(new Dimension(10, 0)));
//        cutoffPanel.add(positiveCutoffLabel);
//        cutoffPanel.add(Box.createRigidArea(new Dimension(5, 0)));
//        cutoffPanel.add(positiveCutoffSpinner);
//        tabOnePanel.add(cutoffPanel, cMid);
//        
//        //----------------------------------------------------------------------
//        //save file
//        
//        //checkbox
//        cMid.insets = new Insets(10, 0, 0, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 50;
//        cMid.gridwidth = 1;
//        tabOnePanel.add(saveFileCheckBox, cMid);
//        
//        //save file textfield and button
//        cMid.insets = new Insets(0, 0, 10, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 51;
//        cMid.gridwidth = 3;
//        saveFilePanel.add(saveFileTextField);
//        saveFilePanel.add(saveFileButton);
//        tabOnePanel.add(saveFilePanel, cMid);
//        
//        //**********************************************************************
//        //second tab
//        
//        tabTwoPanel.setLayout(new GridBagLayout());
//        GridBagConstraints cOrth = new GridBagConstraints();
//        cOrth.anchor = GridBagConstraints.FIRST_LINE_START;
//        cOrth.fill = GridBagConstraints.HORIZONTAL;
//        cOrth.insets = new Insets(10, 0, 0, 0);
//        cOrth.gridx = 0;
//        cOrth.gridy = 0;
//        tabTwoPanel.add(orthLabel, cOrth);
//        cOrth.insets = new Insets(0, 0, 0, 0);
//        cOrth.gridx = 0;
//        cOrth.gridy = 1;
//        cOrth.ipady = 324;
//        tabTwoPanel.add(orthFilesScrollPane, cOrth);
//        cOrth.insets = new Insets(0, 0, 10, 0);
//        cOrth.fill = GridBagConstraints.NONE;
//        cOrth.gridx = 0;
//        cOrth.gridy = 2;
//        cOrth.ipady = 0;
//        tabTwoPanel.add(orthAddButton, cOrth);
//        cOrth.fill = GridBagConstraints.BOTH;
//        cOrth.weightx = 1.0;
//        cOrth.weighty = 1.0;
//        cOrth.gridx = 0;
//        cOrth.gridy = 100;
//        tabTwoPanel.add(Box.createGlue(), cOrth);
//        
//        //**********************************************************************
//        //tabbed pane
//        tabbedPane.addTab("General settings", tabOnePanel);
//        tabbedPane.addTab("Gene family options", tabTwoPanel);
//        add(tabbedPane, BorderLayout.CENTER);
//        
//        //======================================================================
//        //SOUTH PANEL
//        JPanel botPnl = new JPanel();
//        botPnl.setLayout(new BoxLayout(botPnl, BoxLayout.LINE_AXIS));
//        
//        //go button
//        botPnl.add(goButton);
//        add(botPnl, BorderLayout.PAGE_END);
    }

    @Override
    /**
     * Triggered when the {@link InpPnlModel} notifies its observers. Updates
     * all components statuses to match the {@link InpPnlModel}. Don't trigger a
     * InpPnlModel update from within this method, that would start an endless
     * loop.
     */
    public void update(Observable model, Object obj) {
    	assert false;
//
//        if (model instanceof JobInputModel) {
//
//            JobInputModel inpPnlModel = (JobInputModel) model;
//
//            //update: string input fields
//            titleTextField.setText(inpPnlModel.getTitle());
//
//            //update: baits
//            boolean useBaitFile = inpPnlModel.isUseBaitsFile();
//            baitGroupOptionText.setSelected(!useBaitFile);
//            baitGroupOptionFile.setSelected(useBaitFile);
//            baitInputLabel.setEnabled(!useBaitFile);
//            baitGroupTextArea.setEnabled(!useBaitFile);
//            String baits = inpPnlModel.getBaits();
//            baitGroupTextArea.setText(baits);
//            baitFileLabel.setEnabled(useBaitFile);
//            baitGroupFileTextField.setEnabled(useBaitFile);
//            Path baitFilePath = inpPnlModel.getBaitsFilePath();
//            baitGroupFileTextField.setText(baitFilePath.toString());
//            baitGroupFileTextField.setBackground(
//                Files.isRegularFile(baitFilePath) && Files.isReadable(baitFilePath)
//                    ? GUIConstants.APPROVE_COLOR : GUIConstants.DISAPPROVE_COLOR);
//            baitFileButton.setEnabled(useBaitFile);
//
//            //update: species
//            //get currently visible species
//            List<SpeciesEntryPanel> seList = new ArrayList<SpeciesEntryPanel>();
//            for (Component comp : chooseSpeciesPanel.getComponents()) {
//                if (comp instanceof SpeciesEntryPanel) {
//                    SpeciesEntryPanel se = (SpeciesEntryPanel) comp;
//                    seList.add(se);
//                }
//            }
//            //remove species that are not in the model
//            for (SpeciesEntryPanel se : seList) {
//                if (!inpPnlModel.getAllSpecies().values().contains(se)) {
//                    chooseSpeciesPanel.remove(se);
//                }
//            }
//            //add new species from the model
//            for (SpeciesEntryModel sem : inpPnlModel.getAllSpecies().keySet()) {
//                SpeciesEntryPanel se = inpPnlModel.getAllSpecies().get(sem);
//                if (!seList.contains(se)) {
//                    se.setAlignmentX(Component.LEFT_ALIGNMENT);
//                    chooseSpeciesPanel.add(se);
//                    sem.triggerUpdate();
//                }
//            }
//
//            chooseSpeciesPanel.revalidate();
//            chooseSpeciesPanel.repaint();
//
//            //update: cutoffs
//            negativeCutoffSpinner.setValue(inpPnlModel.getNegCutoff());
//            positiveCutoffSpinner.setValue(inpPnlModel.getPosCutoff());
//
//            //update: save
//            boolean saveResults = inpPnlModel.isSaveResults();
//            saveFileCheckBox.setSelected(saveResults);
//            saveFileTextField.setEnabled(saveResults);
//            Path saveFilePath = inpPnlModel.getSaveFilePath();
//            saveFileTextField.setText(saveFilePath.toString());
//            saveFileTextField.setBackground(
//                Files.isDirectory(saveFilePath)
//                && Files.isReadable(saveFilePath)
//                && !saveFilePath.toString().trim().isEmpty()
//                    ? GUIConstants.APPROVE_COLOR : GUIConstants.DISAPPROVE_COLOR);
//            saveFileButton.setEnabled(saveResults);
//
//            //update: orthGroups
//            if (inpPnlModel.getAllOrthGroups().isEmpty()) {
//                orthFilesScrollPane.setViewportView(orthNoFilesChosenLabel);
//            } else {
//                orthFilesScrollPane.setViewportView(orthFilesPanel);
//
//                //get currently visible orthGroups
//                List<FilePanel> oeList = new ArrayList<FilePanel>();
//                for (Component comp : orthFilesPanel.getComponents()) {
//                    if (comp instanceof FilePanel) {
//                        FilePanel oe = (FilePanel) comp;
//                        oeList.add(oe);
//                    }
//                }
//                //remove orthGroups that are not in the model
//                for (FilePanel oe : oeList) {
//                    if (!inpPnlModel.getAllOrthGroups().values().contains(oe)) {
//                        orthFilesPanel.remove(oe);
//                    }
//                }
//                //add new orthGroups from the model
//                for (OrthEntryModel oem : inpPnlModel.getAllOrthGroups().keySet()) {
//                    FilePanel oe = inpPnlModel.getOrthEntry(oem);
//                    if (!oeList.contains(oe)) {
//                        oe.setAlignmentX(Component.LEFT_ALIGNMENT);
//                        orthFilesPanel.add(oe);
//                        oem.triggerUpdate();
//                    }
//                }
//            }
//
//            orthFilesPanel.revalidate();
//            orthFilesPanel.repaint();
//        }

    }

}
