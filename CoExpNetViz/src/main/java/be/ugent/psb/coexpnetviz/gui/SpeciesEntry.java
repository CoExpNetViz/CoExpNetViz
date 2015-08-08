package be.ugent.psb.coexpnetviz.gui;

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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import be.ugent.psb.coexpnetviz.gui.model.SpeciesEntryModel;

/**
 *
 * @author sam
 */
public class SpeciesEntry extends JPanel implements Observer {

    JLabel speciesLbl;
    JTextField speciesNameTf;
    JButton removeBtn;
    JLabel speciesPathLbl;
    JTextField speciesFileTf;
    JButton browseBtn;

    public SpeciesEntry() {
        constructGui();
    }

    private void constructGui() {
        this.speciesLbl = new JLabel(" Species:");
        this.speciesNameTf = new JTextField();
        speciesNameTf.setToolTipText("Enter an arbitrary name for this dataset");
        this.removeBtn = new JButton("Remove");
        this.speciesPathLbl = new JLabel(" Path:");
        this.speciesFileTf = new JTextField();
        speciesFileTf.setToolTipText("Enter the path to the expression data file");
        this.browseBtn = new JButton("Browse");
        browseBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));

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
        add(speciesNameTf, c);
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
        add(speciesFileTf, c);
        c.weightx = 0.04;
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        add(browseBtn, c);
    }

    @Override
    public void update(Observable model, Object obj) {

        //for debugging
//        System.out.println("SpeciesEntry" + " updated from: " + model);

        if (model instanceof SpeciesEntryModel) {

            //for debugging
//            System.out.println("SpeciesEntry" + " updated from SpeciesEntryModel");

            SpeciesEntryModel sem = (SpeciesEntryModel) model;

            //update: species Name
            String speciesName = sem.getSpeciesName();
            speciesNameTf.setText(speciesName);

            //update: species dataset path
            Path speciesExprDataPath = sem.getSpeciesFilePath();
            speciesFileTf.setText(speciesExprDataPath.toString());
            speciesFileTf.setBackground(
                Files.isRegularFile(speciesExprDataPath) && Files.isReadable(speciesExprDataPath)
                    ? GUIConstants.APPROVE_COLOR : GUIConstants.DISAPPROVE_COLOR);
        }
    }

}
