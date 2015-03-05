package be.samey.gui;

import be.samey.model.CoreStatus;
import be.samey.model.GuiStatus;
import be.samey.model.Model;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class RootPanel extends JPanel implements Observer {

    private final Model model;
    private final GuiStatus guiStatus;
    private final CoreStatus coreStatus;

    //input baits
    private JRadioButton inpBaitRb;
    private JRadioButton fileBaitRb;
    private JLabel inpBaitLbl;
    private JTextArea inpBaitTa;
    private JScrollPane inpBaitSp;
    private JLabel fileBaitLbl;
    private JPanel fileBaitPnl;
    private JTextField fileBaitTf;
    private JButton fileBaitBtn;
    //choose species
    private JLabel chooseSpeciesLbl;
    private JScrollPane chooseSpeciesSp;
    private JPanel chooseSpeciesPnl;
    private JButton addSpeciesBtn;
    //choose cutoff
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
    private SpinnerModel nCutoffSm;
    private SpinnerModel pCutoffSm;
    private String[] numbers = new String[]{"first", "second", "third", "fourth", "fith"};

    public RootPanel(Model model) {
        this.model = model;
        this.guiStatus = model.getGuiStatus();
        this.coreStatus = model.getCoreStatus();
        guiStatus.addObserver(this);
        setPreferredSize(new Dimension(500, 640));
        constructGui();
        refreshGui();
    }

    private void constructGui() {
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
        //choose species
        chooseSpeciesLbl = new JLabel("<html>Choose which data sets to use,<br>"
            + "pick one dataset for every species you have specified in the bait genes");
        chooseSpeciesPnl = new JPanel();
        chooseSpeciesPnl.setLayout(new BoxLayout(chooseSpeciesPnl, BoxLayout.Y_AXIS));
        chooseSpeciesSp = new JScrollPane(chooseSpeciesPnl);
        chooseSpeciesSp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chooseSpeciesSp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        addSpeciesBtn = new JButton("Add species");
        addSpeciesBtn.addActionListener(new addSpeciesAl());
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
        c.weightx = 0.5;
        //----------------------------------------------------------------------
        //input bait genes
        //top two radio buttons (input bait genes or file)
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 00;
        c.gridwidth = 1;
        add(inpBaitRb, c);
        c.gridx = 1;
        c.gridy = 00;
        c.gridwidth = 1;
        add(fileBaitRb, c);
        //input baits or choose file
        //input bait genes label
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 01;
        c.gridwidth = 3;
        add(inpBaitLbl, c);
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
        add(fileBaitLbl, c);
        //bait genes file textfield and button
        c.gridx = 0;
        c.gridy = 04;
        c.gridwidth = 3;
        fileBaitPnl.add(fileBaitTf);
        fileBaitPnl.add(fileBaitBtn);
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

    //resets all options on the tab and clears all input fields
    private void refreshGui() {
        //reset species
        guiStatus.removeAllSpecies();
        guiStatus.addSpecies(new SpeciesEntry(model));

        //reset radiobuttons
        guiStatus.setInpBaitSelected(true);
        guiStatus.setSaveFileSelected(false);

        //reset cutoffs
        nCutoffSp.setValue(guiStatus.getDefaultNegCutoff());
        pCutoffSp.setValue(guiStatus.getDefaultPosCutoff());

        //clear fields
        inpBaitTa.setText("");
        fileBaitTf.setText("");
        saveFileTf.setText(System.getProperty("user.dir"));

        //for debugging
        testWithDefaults();
    }

    @Override
    //called whenever the model notify's its observers
    //watch out to not trigger an update from the model from within this method.
    public void update(Observable o, Object arg) {

        //update: input bait genes or upload a file
        boolean inpBaitSelected = guiStatus.isInpBaitSelected();
        inpBaitRb.setSelected(inpBaitSelected);
        inpBaitLbl.setEnabled(inpBaitSelected);
        inpBaitTa.setEnabled(inpBaitSelected);
        fileBaitLbl.setEnabled(!inpBaitSelected);
        fileBaitTf.setEnabled(!inpBaitSelected);
        fileBaitBtn.setEnabled(!inpBaitSelected);

        //update: choose species
        chooseSpeciesPnl.removeAll();
        for (SpeciesEntry se : guiStatus.getSpeciesList()) {
            se.setAlignmentY(TOP_ALIGNMENT);
            se.setAlignmentX(LEFT_ALIGNMENT);
            chooseSpeciesPnl.add(se);
        }
        chooseSpeciesPnl.revalidate();
        chooseSpeciesPnl.repaint();

        //update: save file or not
        boolean saveFileSelected = guiStatus.isSaveFileSelected();
        saveFileChb.setSelected(saveFileSelected);
        saveFileTf.setEnabled(saveFileSelected);
        saveFileBtn.setEnabled(saveFileSelected);
    }

    //some action listeners
    //created when the user chooses to input baits directly or with a file
    private class ChooseSpeciesOrOwnDataBgAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiStatus.setInpBaitSelected(inpBaitRb.isSelected());
        }

    }

    //created when the user chooses to input baits directly or with a file
    private class addSpeciesAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //do not allow more species than what is supported
            if (guiStatus.getSpeciesList().size() >= Model.MAX_SPECIES_COUNT) {
                JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                    String.format("No more than %d species are supported", Model.MAX_SPECIES_COUNT));
                return;
            }
            SpeciesEntry se = new SpeciesEntry(model);
            guiStatus.addSpecies(se);
        }

    }

    //created when the user checks/unchecks the save file checkbox
    private class SaveFileAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            guiStatus.setSaveFileSelected(saveFileChb.isSelected());
        }

    }

    //created when the user clicks the "reset" button
    private class ResetAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            refreshGui();
        }

    }

    /**
     * Created when the user clicks the "Run analysis" button (this.goBtn). All
     * input fields are checked for correctness and passed on to the core model
     */
    private class GoAl implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Path baitPath, outPath;
            /*
             read in bait genes
             */
            if (!guiStatus.isInpBaitSelected()) {
                if (fileBaitTf.getText().trim().length() == 0) {
                    JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                        "Please enter a baits file or input your baits manually",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    baitPath = Paths.get(fileBaitTf.getText().trim()).toRealPath();
                    Charset charset = Charset.forName("UTF-8");
                    BufferedReader reader = Files.newBufferedReader(baitPath, charset);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    coreStatus.setBaits(sb.toString());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                        "There was an error while reading the baits file\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                //TODO: better format checking
                if (inpBaitTa.getText().trim().length() == 0) {
                    JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                        "Please specify your bait genes",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    coreStatus.setBaits(inpBaitTa.getText());
                }
            }
            /*
             get Species paths
             */
            List<SpeciesEntry> sel = guiStatus.getSpeciesList();
            for (int i = 0; i < 5; i++) {
                if (i < sel.size()) {
                    SpeciesEntry se = sel.get(i);
                    //check if names are supplied
                    if (se.speciesTf.getText().trim().length() == 0) {
                        JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                            String.format("No species name was given for the %s species", numbers[i]),
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    coreStatus.getNames()[i] = se.speciesTf.getText();
                    //check if path is correct
                    if (se.speciesPathTf.getText().trim().length() == 0) {
                        JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                            String.format("Please specify a path for the %s species", numbers[i]),
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    try {
                        Path speciesPath = Paths.get(se.speciesPathTf.getText().trim()).toRealPath();
                        coreStatus.getFilePaths()[i] = speciesPath;
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                            String.format("There was an error while reading the gene expression file for the %s species\n"
                                + "%s", numbers[i], ex.getMessage()),
                            "Warning",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    coreStatus.getNames()[i] = null;
                    coreStatus.getFilePaths()[i] = null;
                }
            }
            /*
             check if output path is correct
             */
            if (guiStatus.isSaveFileSelected()) {
                if (saveFileTf.getText().trim().length() == 0) {
                    JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                        "No output direcory was given",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    outPath = Paths.get(saveFileTf.getText().trim()).toRealPath();
                    coreStatus.setOutPath(outPath);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(guiStatus.getRootPanelFrame(),
                        "There was an error while accessing the output directory" + ex.getMessage(),
                        "Warning",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            /*
             get cutoffs
             */
            double nCutoff = (Double) nCutoffSp.getValue();
            coreStatus.setNCutoff(nCutoff);
            double pCutoff = (Double) pCutoffSp.getValue();
            coreStatus.setPCutoff(pCutoff);

            /*
             survived all checks, run the analysis
             */
            coreStatus.runAnalysis();
        }

    }

    //for debugging
    private void testWithDefaults() {
        guiStatus.setInpBaitSelected(!inpBaitRb.isSelected());
        fileBaitTf.setText("/home/sam/Documents/uma1_s2-mp2-data/CexpNetViz_web-interface/baits.txt");
        guiStatus.getSpeciesList().get(0).speciesTf.setText("Tomato");
        guiStatus.getSpeciesList().get(0).speciesPathTf.setText("/home/sam/favs/uma1_s2-mp2-data/CexpNetViz_web-interface/datasets/Tomato_dataset.txt");
        guiStatus.addSpecies(new SpeciesEntry(model));
        guiStatus.getSpeciesList().get(1).speciesTf.setText("Apple");
        guiStatus.getSpeciesList().get(1).speciesPathTf.setText("/home/sam/Documents/uma1_s2-mp2-data/CexpNetViz_web-interface/datasets/Apple_dataset.txt");
        guiStatus.addSpecies(new SpeciesEntry(model));
        guiStatus.getSpeciesList().get(2).speciesTf.setText("Arabidopsis");
        guiStatus.getSpeciesList().get(2).speciesPathTf.setText("/home/sam/Documents/uma1_s2-mp2-data/CexpNetViz_web-interface/datasets/Arabidopsis_dataset.txt");
        guiStatus.addSpecies(new SpeciesEntry(model));
        guiStatus.getSpeciesList().get(3).speciesTf.setText("Patato");
        guiStatus.getSpeciesList().get(3).speciesPathTf.setText("/home/sam/Documents/uma1_s2-mp2-data/CexpNetViz_web-interface/datasets/Potato_dataset.txt");
    }

}
