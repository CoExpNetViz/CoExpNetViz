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
import be.samey.gui.model.OrthEntryModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author sam
 */
public class OrthEntry extends JPanel implements Observer {

    JTextField orthTf;
    JButton orthBrowseBtn;
    JButton orthRemoveBtn;

    public OrthEntry() {
        constructGui();
    }

    private void constructGui() {
        orthTf = new JTextField();
        orthBrowseBtn = new JButton("Browse");
        orthBrowseBtn.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        orthRemoveBtn = new JButton("Remove");

        setMaximumSize(new Dimension(495, 64));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1.0;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        add(orthTf, c);
        c.weightx = 0.05;
        c.gridx = 1;
        c.gridy = 0;
        add(orthBrowseBtn, c);
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        add(orthRemoveBtn, c);
    }

    @Override
    public void update(Observable model, Object object) {

        if (model instanceof OrthEntryModel) {
            OrthEntryModel oem = (OrthEntryModel) model;

            Path orthFilePath = oem.getOrthEntryPath();
            orthTf.setText(orthFilePath.toString());
            orthTf.setBackground(
                Files.isRegularFile(orthFilePath) && Files.isReadable(orthFilePath)
                    ? GuiConstants.APPROVE_COLOR : GuiConstants.DISAPPROVE_COLOR);
        }

    }

}
