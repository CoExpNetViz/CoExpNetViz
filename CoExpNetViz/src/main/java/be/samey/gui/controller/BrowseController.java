package be.samey.gui.controller;

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
import be.samey.gui.model.GuiModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 *
 * @author sam
 */
public abstract class BrowseController implements ActionListener {

    private GuiModel guiModel;
    private JComponent parent;

    public static final int DIRECTORY = JFileChooser.DIRECTORIES_ONLY;
    public static final int FILE = JFileChooser.FILES_ONLY;

    private final String windowTitle;
    private final int mode;

    public BrowseController(String windowTitle, int mode) {
        this.windowTitle = windowTitle;
        this.mode = mode;
    }

    public BrowseController(JComponent parent, String windowTitle, int mode) {
        this.parent = parent;
        this.windowTitle = windowTitle;
        this.mode = mode;
    }

    public BrowseController(GuiModel guiModel, JComponent parent, String windowTitle, int mode) {
        this.guiModel = guiModel;
        this.parent = parent;
        this.windowTitle = windowTitle;
        this.mode = mode;
    }

    public void setGuiModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }

    public void setParent(JComponent parent) {
        this.parent = parent;
    }

    public abstract void updateModel(GuiModel guiModel, Path path);

    @Override
    public void actionPerformed(ActionEvent e) {
        //Execute when button is pressed
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setFileSelectionMode(mode);
        chooser.setDialogTitle(windowTitle);
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            Path p = Paths.get(chooser.getSelectedFile().toString());
            //TODO: validate path
            updateModel(guiModel, p);
        }
    }
}
