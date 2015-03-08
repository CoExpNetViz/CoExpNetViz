package be.samey.gui;

import be.samey.model.Model;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author sam
 */
public class SpeciesEntry extends JPanel {

    private final Model model;

    JLabel speciesLbl;
    JTextField speciesTf;
    JButton removeBtn;
    JLabel speciesPathLbl;
    JTextField speciesPathTf;
    JButton browseBtn;

    public SpeciesEntry(Model model) {
        this.model = model;
        constructGui();
    }

    private void constructGui() {
        this.speciesLbl = new JLabel(" Species:");
        this.speciesTf = new JTextField();
        this.removeBtn = new JButton("Remove");
        removeBtn.addActionListener(new RemoveSpeciesAl(this));
        this.speciesPathLbl = new JLabel(" Path:");
        this.speciesPathTf = new JTextField();
        this.browseBtn = new JButton("Browse");
        browseBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        browseBtn.addActionListener(new BrowseAl(this, speciesPathTf, "Choose gene expression matrix", BrowseAl.FILE));

        //make layout
        setMaximumSize(new Dimension(475, 64));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.10;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        add(speciesLbl, c);
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        add(speciesTf, c);
        c.weightx = 0.04;
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        add(removeBtn, c);
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 0.10;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        add(speciesPathLbl, c);
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        add(speciesPathTf, c);
        c.weightx = 0.04;
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        add(browseBtn, c);
    }

    //created when the user click the "remove" button for a species dataset
    private class RemoveSpeciesAl implements ActionListener {

        SpeciesEntry seToRemove;

        public RemoveSpeciesAl(SpeciesEntry seToRemove) {
            this.seToRemove = seToRemove;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {

            //user must at least supply one species
            if (model.getGuiStatus().getSpeciesList().size() == 1) {
                JOptionPane.showMessageDialog(model.getGuiStatus().getRootPanelFrame(),
                    "You must use at least one dataset");
                return;
            }
            model.getGuiStatus().removeSpecies(seToRemove);
        }

    }

}
