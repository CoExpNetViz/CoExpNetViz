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
