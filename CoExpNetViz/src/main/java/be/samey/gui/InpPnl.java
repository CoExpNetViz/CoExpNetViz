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
import be.samey.internal.CyAppManager;
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
import javax.swing.UIManager;

/**
 *
 * @author sam
 */
public class InpPnl extends JPanel implements Observer {

    //settings
    JPanel settingsPnl;
    JLabel profLbl;
    JButton profLoadBtn;
    JButton profSaveBtn;
    JButton profDelBtn;
    JButton resetBtn;
    //title
    JLabel titleLbl;
    JTextField titleTf;
    JPanel topPnl;
    //first tab: general settings
    JPanel tabOnePnl;
    //input baits
    JRadioButton baitInpRb;
    JRadioButton baitFileRb;
    JLabel baitInpLbl;
    JButton baitInpInfoBtn;
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
    //save
    JCheckBox saveFileChb;
    JTextField saveFileTf;
    JButton saveFileBtn;
    JPanel saveFilePnl;
    //buttons
    JButton goBtn;
    //second tab
    JPanel tabTwoPnl;
    //tabbed pane
    JTabbedPane tabbedPane;

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
        //profiles
        settingsPnl = new JPanel();
        settingsPnl.setLayout(new BoxLayout(settingsPnl, BoxLayout.LINE_AXIS));
        profLbl = new JLabel("Settings: ");
        profLoadBtn = new JButton("Load");
        profSaveBtn = new JButton("Save");
        profDelBtn = new JButton("Delete");
        resetBtn = new JButton("Reset form");
        //title
        titleLbl = new JLabel("Title (optional)");
        titleTf = new JTextField();
        topPnl =  new JPanel();
        //first tab
        tabOnePnl = new JPanel();
        //input bait genes or choose file
        baitInpRb = new JRadioButton("Input bait genes");
        baitInpRb.setName("baitInpRb");
        baitFileRb = new JRadioButton("Upload file with bait genes");
        baitFileRb.setName("baitFileRb");
        inpBaitOrfileBaitBg = new ButtonGroup();
        inpBaitOrfileBaitBg.add(baitInpRb);
        inpBaitOrfileBaitBg.add(baitFileRb);
        baitInpLbl = new JLabel("Enter bait genes");
        baitInpInfoBtn = new JButton("ID's");
        try {
            ClassLoader classLoader = CyAppManager.class.getClassLoader();
            Image img = ImageIO.read(classLoader.getResource("icons/help-icon16.png"));
            baitInpInfoBtn.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
            Logger.getLogger(InpPnl.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        //buttons
        goBtn = new JButton("Run analysis");
        //second tab
        tabTwoPnl = new JPanel();
        //tabbed pane
        tabbedPane = new JTabbedPane();

        //create the borderlayout
        setPreferredSize(new Dimension(500, 700));
        setLayout(new BorderLayout());
        //======================================================================
        //NORTH PANEL
        //create topPnl gridbaglayout and add components to it
        topPnl.setLayout(new GridBagLayout());
        GridBagConstraints cTop = new GridBagConstraints();
        cTop.anchor = GridBagConstraints.FIRST_LINE_START;
        cTop.weightx = 0.5;
        cTop.fill = GridBagConstraints.HORIZONTAL;
        //----------------------------------------------------------------------
        //settings
        cTop.insets = new Insets(0, 0, 0, 0);
        cTop.gridx = 0;
        cTop.gridy = 00;
        cTop.gridwidth = 2;
        settingsPnl.add(profLbl);
        settingsPnl.add(profLoadBtn);
        settingsPnl.add(profSaveBtn);
        settingsPnl.add(profDelBtn);
        topPnl.add(settingsPnl, cTop);
        cTop.fill = GridBagConstraints.NONE;
        cTop.anchor = GridBagConstraints.LAST_LINE_END;
        cTop.insets = new Insets(0, 0, 0, 0);
        cTop.gridx = 2;
        cTop.gridy = 00;
        cTop.gridwidth = 1;
        resetBtn.setMaximumSize(new Dimension(32, 32));
        topPnl.add(resetBtn, cTop);
        //----------------------------------------------------------------------
        //title
        cTop.anchor = GridBagConstraints.FIRST_LINE_START;
        cTop.fill = GridBagConstraints.HORIZONTAL;
        cTop.insets = new Insets(10, 0, 0, 0);
        cTop.gridx = 0;
        cTop.gridy = 10;
        cTop.gridwidth = 1;
        topPnl.add(titleLbl, cTop);
        cTop.insets = new Insets(00, 0, 0, 0);
        cTop.gridx = 0;
        cTop.gridy = 11;
        cTop.gridwidth = 3;
        topPnl.add(titleTf, cTop);
        add(topPnl, BorderLayout.PAGE_START);
        //======================================================================
        //CENTRAL PANEL
        //**********************************************************************
        //first tab
        tabOnePnl.setLayout(new GridBagLayout());
        GridBagConstraints cMid = new GridBagConstraints();
        cMid.anchor = GridBagConstraints.FIRST_LINE_START;
        cMid.weightx = 0.5;
        cMid.fill = GridBagConstraints.HORIZONTAL;
        //----------------------------------------------------------------------
        //input bait genes
        //top two radio buttons (input bait genes or file)
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 20;
        cMid.gridwidth = 1;
        tabOnePnl.add(baitInpRb, cMid);
        cMid.gridx = 1;
        cMid.gridy = 20;
        cMid.gridwidth = 1;
        tabOnePnl.add(baitFileRb, cMid);
        //input baits or choose file
        //input bait genes label
        cMid.insets = new Insets(0, 0, 0, 0);
        cMid.fill = GridBagConstraints.HORIZONTAL;
        cMid.anchor = GridBagConstraints.LAST_LINE_START;
        cMid.gridx = 0;
        cMid.gridy = 21;
        cMid.gridwidth = 1;
        tabOnePnl.add(baitInpLbl, cMid);
        //input bait genes info button
        cMid.fill = GridBagConstraints.NONE;
        cMid.anchor = GridBagConstraints.LAST_LINE_END;
        cMid.gridx = 2;
        cMid.gridy = 21;
        cMid.gridwidth = 1;
        tabOnePnl.add(baitInpInfoBtn, cMid);
        //bait genes text area
        cMid.fill = GridBagConstraints.HORIZONTAL;
        cMid.anchor = GridBagConstraints.FIRST_LINE_START;
        cMid.weighty = 1.0;
        cMid.ipady = (160);
        cMid.gridx = 0;
        cMid.gridy = 22;
        cMid.gridwidth = 3;
        tabOnePnl.add(inpBaitSp, cMid);
        //choose bait genes file label
        cMid.weighty = 0.0;
        cMid.ipady = 0;
        cMid.gridx = 0;
        cMid.gridy = 23;
        cMid.gridwidth = 3;
        tabOnePnl.add(baitFileLbl, cMid);
        //bait genes file textfield and button
        cMid.gridx = 0;
        cMid.gridy = 24;
        cMid.gridwidth = 3;
        fileBaitPnl.add(baitFileTf);
        fileBaitPnl.add(baitFileBtn);
        tabOnePnl.add(fileBaitPnl, cMid);
        //----------------------------------------------------------------------
        //choose species
        //choose species label
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 30;
        cMid.gridwidth = 3;
        tabOnePnl.add(chooseSpeciesLbl, cMid);
        //choose species scrollpane
        cMid.insets = new Insets(0, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 31;
        cMid.ipady = (160);
        cMid.gridwidth = 3;
        tabOnePnl.add(chooseSpeciesSp, cMid);
        //add species button
        cMid.insets = new Insets(0, 0, 0, 90);
        cMid.gridx = 0;
        cMid.gridy = 32;
        cMid.ipady = (0);
        cMid.gridwidth = 1;
        tabOnePnl.add(addSpeciesBtn, cMid);
        //----------------------------------------------------------------------
        //choose cutoffs
        //choose cutoff label
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 40;
        cMid.gridwidth = 3;
        tabOnePnl.add(chooseCutoffLbl, cMid);
        //cutoff labels and spinners
        cMid.insets = new Insets(0, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 41;
        cMid.gridwidth = 3;
        cutoffPnl.add(nCutoffLbl);
        cutoffPnl.add(Box.createRigidArea(new Dimension(5, 0))); //spacer
        cutoffPnl.add(nCutoffSp);
        cutoffPnl.add(Box.createRigidArea(new Dimension(10, 0)));
        cutoffPnl.add(pCutoffLbl);
        cutoffPnl.add(Box.createRigidArea(new Dimension(5, 0)));
        cutoffPnl.add(pCutoffSp);
        tabOnePnl.add(cutoffPnl, cMid);
        //----------------------------------------------------------------------
        //save file
        //checkbox
        cMid.insets = new Insets(10, 0, 0, 0);
        cMid.gridx = 0;
        cMid.gridy = 50;
        cMid.gridwidth = 1;
        tabOnePnl.add(saveFileChb, cMid);
        //save file textfield and button
        cMid.insets = new Insets(0, 0, 10, 0);
        cMid.gridx = 0;
        cMid.gridy = 51;
        cMid.gridwidth = 3;
        saveFilePnl.add(saveFileTf);
        saveFilePnl.add(saveFileBtn);
        tabOnePnl.add(saveFilePnl, cMid);
        //go button
//        cMid.fill = GridBagConstraints.NONE;
//        cMid.insets = new Insets(10, 0, 0, 0);
//        cMid.gridx = 0;
//        cMid.gridy = 52;
//        cMid.gridwidth = 3;
//        tabOnePnl.add(goBtn, cMid);
        //**********************************************************************
        //second tab
        //tabbed pane
        tabbedPane.addTab("General settings", null, tabOnePnl, "Something");
        tabbedPane.addTab("Gene family options", null, tabTwoPnl, "Somethingelse");
        add(tabbedPane, BorderLayout.CENTER);
        //======================================================================
        //SOUTH PANEL
        JPanel botPnl = new JPanel();
        botPnl.setLayout(new BoxLayout(botPnl, BoxLayout.LINE_AXIS));
        botPnl.add(goBtn);
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

        //for debugging
//        System.out.println("InpPanel" + " updated from: " + model);
        if (model instanceof InpPnlModel) {

            //for debugging
//            System.out.println("InpPanel" + " updated from inputPnlModel");
            InpPnlModel inpPnlModel = (InpPnlModel) model;

            //update: string input fields
            titleTf.setText(inpPnlModel.getTitle());

            //update: baits
            boolean useBaitFile = inpPnlModel.isUseBaitFile();
            baitInpRb.setSelected(!useBaitFile);
            baitFileRb.setSelected(useBaitFile);
            baitInpLbl.setEnabled(!useBaitFile);
//            baitInpInfoBtn.setEnabled(!useBaitFile);
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
