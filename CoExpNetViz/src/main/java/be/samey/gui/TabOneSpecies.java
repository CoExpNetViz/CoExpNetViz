/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.gui;

import be.samey.cynetw.CevNetworkCreator;
import be.samey.io.CevNetworkReader;
import be.samey.io.CevTableReader;
import be.samey.io.CevVizmapReader;
import be.samey.model.CoreStatus;
import be.samey.model.GuiStatus;
import be.samey.model.Model;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import org.cytoscape.model.CyNetwork;

/**
 *
 * @author sam
 */
public class TabOneSpecies extends JPanel implements Observer {

    private final Model model;
    private final GuiStatus guiStatus;
    private final CoreStatus coreStatus;

    private JRadioButton chooseSpeciesRb;
    private JRadioButton ownDataRb;
    private JLabel chooseSpeciesLbl;
    private JComboBox chooseSpeciesCb;
    private JLabel ownDataLbl;
    private JPanel ownDataExpPnl;
    private JTextField ownDataExpTf;
    private JButton ownDataExpBtn;
    private JPanel ownDataDescPnl;
    private JTextField ownDataDescTf;
    private JButton ownDataDescBtn;
    private JRadioButton inpBaitRb;
    private JRadioButton fileBaitRb;
    private JLabel inpBaitLbl;
    private JTextArea inpBaitTa;
    private JScrollPane inpBaitSp;
    private JLabel fileBaitLbl;
    private JPanel fileBaitPnl;
    private JTextField fileBaitTf;
    private JButton fileBaitBtn;
    private JLabel chooseCutoffLbl;
    private JPanel cutoffPnl;
    private JLabel nCutoffLbl;
    private JSpinner nCutoffSp;
    private JLabel pCutoffLbl;
    private JSpinner pCutoffSp;
    private JCheckBox saveFileChb;
    private JTextField saveFileTf;
    private JButton saveFileBtn;
    private JPanel saveFilePnl;
    private JButton goBtn;
    private JButton resetBtn;

    private ButtonGroup inpBaitOrfileBaitBg;
    private ButtonGroup chooseSpeciesOrOwnDataBg;
    private SpinnerModel nCutoffSm;
    private SpinnerModel pCutoffSm;

    public TabOneSpecies(Model model) {
        this.model = model;
        this.guiStatus = model.getGuiStatus();
        this.coreStatus = model.getCoreStatus();
        guiStatus.addObserver(this);
        constructGui();
        refreshGui();
    }

    private void constructGui() {
        //make all Jcomponents
        //top two radio buttons
        chooseSpeciesRb = new JRadioButton("Choose species");
        chooseSpeciesRb.addActionListener(new ChooseSpeciesOrOwnDataAl());
        ownDataRb = new JRadioButton("Upload own data");
        ownDataRb.addActionListener(new ChooseSpeciesOrOwnDataAl());
        chooseSpeciesOrOwnDataBg = new ButtonGroup();
        chooseSpeciesOrOwnDataBg.add(chooseSpeciesRb);
        chooseSpeciesOrOwnDataBg.add(ownDataRb);
        //choose species label and combobox
        chooseSpeciesLbl = new JLabel("Choose species");
        chooseSpeciesCb = new JComboBox(Model.SPECIES_CHOICES);
        //upload own data label, textfield and button
        ownDataLbl = new JLabel("Upload own data");
        ownDataExpPnl = new JPanel();
        ownDataExpPnl.setLayout(new BoxLayout(ownDataExpPnl, BoxLayout.LINE_AXIS));
        ownDataExpTf = new JTextField();
        ownDataExpTf.setToolTipText("Path to expression data file");
        ownDataExpBtn = new JButton("Browse");
        ownDataExpBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        ownDataExpBtn.addActionListener(new BrowseAl(this, ownDataExpTf, "Choose expression data file", BrowseAl.FILE));
        ownDataDescPnl = new JPanel();
        ownDataDescPnl.setLayout(new BoxLayout(ownDataDescPnl, BoxLayout.LINE_AXIS));
        ownDataDescTf = new JTextField();
        ownDataDescTf.setToolTipText("Path to gene description data file (optional)");
        ownDataDescBtn = new JButton("Browse");
        ownDataDescBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        ownDataDescBtn.addActionListener(new BrowseAl(this, ownDataDescTf, "Choose gene description file", BrowseAl.FILE));
        //input bait genes or choose file
        inpBaitRb = new JRadioButton("Input bait genes");
        inpBaitRb.addActionListener(new ChooseSpeciesOrOwnDataBgAl());
        fileBaitRb = new JRadioButton("Upload file with bait genes");
        fileBaitRb.addActionListener(new ChooseSpeciesOrOwnDataBgAl());
        inpBaitOrfileBaitBg = new ButtonGroup();
        inpBaitOrfileBaitBg.add(inpBaitRb);
        inpBaitOrfileBaitBg.add(fileBaitRb);
        inpBaitLbl = new JLabel("Enter bait genes");
        inpBaitTa = new JTextArea();
        inpBaitTa.setToolTipText("Enter gene identifiers seperated by whitespace, eg 'Solyc02g04650'");
        inpBaitSp = new JScrollPane(inpBaitTa);
        inpBaitSp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fileBaitLbl = new JLabel("Choose file with bait genes");
        fileBaitPnl = new JPanel();
        fileBaitPnl.setLayout(new BoxLayout(fileBaitPnl, BoxLayout.LINE_AXIS));
        fileBaitTf = new JTextField();
        fileBaitTf.setToolTipText("Path to file with gene identifiers, gene identifiers must be separated by white space");
        fileBaitBtn = new JButton("Browse");
        fileBaitBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        fileBaitBtn.addActionListener(new BrowseAl(this, fileBaitTf, "Choose file with bait genes", BrowseAl.FILE));
        //choose cutoffs
        chooseCutoffLbl = new JLabel("Choose cutoff");
        cutoffPnl = new JPanel();
        cutoffPnl.setLayout(new BoxLayout(cutoffPnl, BoxLayout.LINE_AXIS));
        nCutoffLbl = new JLabel("Neg. cutoff");
        nCutoffSm = new SpinnerNumberModel(guiStatus.getDefaultNegCutoff(), -1.0, 0.0, 0.1);
        nCutoffSp = new JSpinner(nCutoffSm);
        nCutoffSp.setMaximumSize(new Dimension(64, 32));
        pCutoffLbl = new JLabel("Pos. cutoff");
        pCutoffSm = new SpinnerNumberModel(guiStatus.getDefaultPosCutoff(), 0.0, 1.0, 0.1);
        pCutoffSp = new JSpinner(pCutoffSm);
        pCutoffSp.setMaximumSize(new Dimension(64, 32));
        //save output
        saveFileChb = new JCheckBox("Save output");
        saveFileChb.addActionListener(new SaveFileAl());
        saveFileTf = new JTextField();
        saveFileTf.setToolTipText("Path to output directory");
        saveFilePnl = new JPanel();
        saveFilePnl.setLayout(new BoxLayout(saveFilePnl, BoxLayout.LINE_AXIS));
        saveFileBtn = new JButton("Browse");
        saveFileBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        saveFileBtn.addActionListener(new BrowseAl(this, saveFileTf, "Choose output directory", BrowseAl.DIRECTORY));
        //run analysis or reset form
        goBtn = new JButton("Run analysis");
        goBtn.addActionListener(new GoAl());
        resetBtn = new JButton("Reset form");
        resetBtn.addActionListener(new ResetAl());

        //create gridbaglayout and add components to it
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 0, 0, 0);
        c.weightx = 0.5;
        //top two radio buttons (choose species or own data)
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        add(chooseSpeciesRb, c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        add(ownDataRb, c);
        //choose species label above combobox
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        add(chooseSpeciesLbl, c);
        //choose species combobox
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        add(chooseSpeciesCb, c);
        //upload own data
        //Use own data label
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        add(ownDataLbl, c);
        //textfield and button for co-expression data
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        ownDataExpPnl.add(ownDataExpTf);
        ownDataExpPnl.add(ownDataExpBtn);
        add(ownDataExpPnl, c);
        //textfield and button for gene description data
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 3;
        ownDataDescPnl.add(ownDataDescTf);
        ownDataDescPnl.add(ownDataDescBtn);
        add(ownDataDescPnl, c);
        //bottom two radio buttons (input bait genes or file)
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        add(inpBaitRb, c);
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 1;
        add(fileBaitRb, c);
        //input baits or choose file
        //input bait genes label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 3;
        add(inpBaitLbl, c);
        //bait genes text area
        c.insets = new Insets(0, 0, 0, 0);
        c.weighty = 1.0;
        c.ipady = (160);
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 3;
        add(inpBaitSp, c);
        //choose bait genes file label
        c.weighty = 0.0;
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = 3;
        add(fileBaitLbl, c);
        //bait genes file textfield and button
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 3;
        fileBaitPnl.add(fileBaitTf);
        fileBaitPnl.add(fileBaitBtn);
        add(fileBaitPnl, c);
        //choose cutoff label
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 11;
        c.gridwidth = 3;
        add(chooseCutoffLbl, c);
        //cutoff labels and spinners
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 3;
        cutoffPnl.add(nCutoffLbl);
        cutoffPnl.add(Box.createRigidArea(new Dimension(5, 0))); //spacer
        cutoffPnl.add(nCutoffSp);
        cutoffPnl.add(Box.createRigidArea(new Dimension(10, 0)));
        cutoffPnl.add(pCutoffLbl);
        cutoffPnl.add(Box.createRigidArea(new Dimension(5, 0)));
        cutoffPnl.add(pCutoffSp);
        add(cutoffPnl, c);
        //save file
        //checkbox
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 13;
        c.gridwidth = 1;
        add(saveFileChb, c);
        //save file textfield and button
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 14;
        c.gridwidth = 3;
        saveFilePnl.add(saveFileTf);
        saveFilePnl.add(saveFileBtn);
        add(saveFilePnl, c);
        //go and clear buttons
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 15;
        c.gridwidth = 1;
        add(goBtn, c);
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.gridx = 2;
        c.gridy = 15;
        c.gridwidth = 1;
        add(resetBtn, c);
    }

    //resets all options on the tab and clears all input fields
    private void refreshGui() {
        //reset species choice
        chooseSpeciesCb.setSelectedIndex(0);

        //reset radiobuttons
        chooseSpeciesRb.setSelected(true);
        guiStatus.setChooseSpecies(chooseSpeciesRb.isSelected());
        inpBaitRb.setSelected(true);
        guiStatus.setInpBait(inpBaitRb.isSelected());
        saveFileChb.setSelected(false);
        guiStatus.setSaveFile(saveFileChb.isSelected());

        //reset cutoffs
        nCutoffSp.setValue(guiStatus.getDefaultNegCutoff());
        pCutoffSp.setValue(guiStatus.getDefaultPosCutoff());

        //clear fields
        ownDataExpTf.setText("");
        ownDataDescTf.setText("");
        inpBaitTa.setText("");
        fileBaitTf.setText("");
        saveFileTf.setText("");

    }

    @Override
    //called whenever the model notify's its observers
    //watch out to not trigger an update from the model from within this method.
    public void update(Observable o, Object arg) {

        //update: choose species or use own data
        boolean chooseSpecies = guiStatus.getChooseSpecies();
        chooseSpeciesLbl.setEnabled(chooseSpecies);
        chooseSpeciesCb.setEnabled(chooseSpecies);
        ownDataLbl.setEnabled(!chooseSpecies);
        ownDataExpTf.setEnabled(!chooseSpecies);
        ownDataExpBtn.setEnabled(!chooseSpecies);
        ownDataDescTf.setEnabled(!chooseSpecies);
        ownDataDescBtn.setEnabled(!chooseSpecies);

        //update: input bait genes or upload a file
        boolean inpBait = guiStatus.getInpBait();
        inpBaitLbl.setEnabled(inpBait);
        inpBaitTa.setEnabled(inpBait);
        fileBaitLbl.setEnabled(!inpBait);
        fileBaitTf.setEnabled(!inpBait);
        fileBaitBtn.setEnabled(!inpBait);

        //update: save file or not
        boolean saveFile = guiStatus.getSaveFile();
        saveFileTf.setEnabled(saveFile);
        saveFileBtn.setEnabled(saveFile);
    }

    //some action listeners
    //created when the user chooses to use a species or to upload his own data
    private class ChooseSpeciesOrOwnDataAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiStatus.setChooseSpecies(chooseSpeciesRb.isSelected());
        }

    }

    //created when the user chooses to input baits directly or with a file
    private class ChooseSpeciesOrOwnDataBgAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiStatus.setInpBait(inpBaitRb.isSelected());
        }

    }

    //created when the user checks/unchecks the save file checkbox
    private class SaveFileAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiStatus.setSaveFile(saveFileChb.isSelected());
        }

    }

    //created when the user clicks the "reset" button
    private class ResetAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            refreshGui();
        }

    }

    //created when the user clicks the "Run analysis" button (this.goBtn)
    private class GoAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //check if all paths entered by the user are correct
            Path p1, p2, p3, p4;
            p1 = Paths.get(ownDataExpTf.getText());
            try {
                if (!guiStatus.getChooseSpecies()) {
                    p1 = Paths.get(ownDataExpTf.getText()).toRealPath();
                    p2 = Paths.get(ownDataDescTf.getText()).toRealPath();
                }
                if (!guiStatus.getInpBait()) {
                    p3 = Paths.get(fileBaitTf.getText()).toRealPath();
                }
                if (!guiStatus.getSaveFile()) {
                    p4 = Paths.get(saveFileTf.getText()).toRealPath();
                }
            } catch (IOException ex) {
                System.out.format("Coult not resolve file %s%n", ex);
                //TODO: warn the user somehow
            }

            //for debugging
            test();
        }

    }

    //for debugging
    private void test() {
        try {
            Path sifPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/Test_Network_File.sif");
            Path noaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/nodeattr.txt");
            Path edaPath = Paths.get("/home/sam/favs/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/edgeattr.txt");
            Path vizPath = Paths.get("/home/sam/Documents/uma1_s2-mp2-data/CoExpNetViz4Sam/OUTPUT/Example_Sorghum_Sam/BaitStyles.xml");

            CevNetworkCreator cnc = new CevNetworkCreator(model);
            CyNetwork cn = cnc.createSubNetwork();
            CevNetworkReader.readSIF(sifPath, cn);
            CevTableReader.readNOA(noaPath, cn, model);
            CevTableReader.readEDA(edaPath, cn, model);

            model.getServices().getCyNetworkManager().addNetwork(cn);
        } catch (IOException ex) {
            Logger.getLogger(TabOneSpecies.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
