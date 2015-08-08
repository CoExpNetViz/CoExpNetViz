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

import be.ugent.psb.coexpnetviz.gui.model.OrthEntryModel;

/**
 *
 * @author sam
 */
public class OrthEntry extends JPanel implements Observer {

    JLabel orthNameLbl;
    JTextField orthNameTf;
    JButton orthRemoveBtn;
    JLabel orthPathLbl;
    JTextField orthPathTf;
    JButton orthBrowseBtn;


    public OrthEntry() {
        constructGui();
    }

    private void constructGui() {
        orthNameLbl = new JLabel("Name:");
        orthNameTf = new JTextField();
        orthRemoveBtn = new JButton("Remove");
        orthPathLbl = new JLabel("Path:");
        orthPathTf = new JTextField();
        orthBrowseBtn = new JButton("Browse");
        orthBrowseBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));

        setMaximumSize(new Dimension(495, 64));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        //
        c.insets = new Insets(10, 0, 0, 0);
        c.weightx = 0.05;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        add(orthNameLbl, c);
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        add(orthNameTf, c);
        c.weightx = 0.05;
        c.gridx = 2;
        c.gridy = 0;
        add(orthRemoveBtn, c);
        //
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 0.05;
        c.gridx = 0;
        c.gridy = 1;
        add(orthPathLbl, c);
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridy = 1;
        add(orthPathTf, c);
        c.weightx = 0.05;
        c.gridx = 2;
        c.gridy = 1;
        add(orthBrowseBtn, c);
    }

    @Override
    public void update(Observable model, Object object) {

        if (model instanceof OrthEntryModel) {
            OrthEntryModel oem = (OrthEntryModel) model;

            String orthGroupName = oem.getOrthGroupName();
            orthNameTf.setText(orthGroupName);
            
            Path orthFilePath = oem.getOrthEntryPath();
            orthPathTf.setText(orthFilePath.toString());
            orthPathTf.setBackground(
                Files.isRegularFile(orthFilePath) && Files.isReadable(orthFilePath)
                    ? GUIConstants.APPROVE_COLOR : GUIConstants.DISAPPROVE_COLOR);
        }

    }

}
