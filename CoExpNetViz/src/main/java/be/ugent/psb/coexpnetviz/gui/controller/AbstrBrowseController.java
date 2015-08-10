package be.ugent.psb.coexpnetviz.gui.controller;

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

import java.awt.event.ActionListener;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import be.ugent.psb.coexpnetviz.CENVApplication;

/**
 *
 * @author sam
 */
public abstract class AbstrBrowseController extends AbstrController implements ActionListener {

    protected static final int DIRECTORY = JFileChooser.DIRECTORIES_ONLY;
    protected static final int FILE = JFileChooser.FILES_ONLY;

    public AbstrBrowseController(CENVApplication cyAppManager) {
        super(cyAppManager);
    }

    /**
     * Show a file chooser which defaults to the last used directory (retrieved
     * from the cyAppManagers guiManager). Returns the file chosen by the user
     * or defaultPath if no file was chosen.
     *
     * @param windowTitle
     * @param mode FILE or DIRECTORY
     * @param defaultPath the Path to return if no file was chosen
     * @return the Path chosen by the user or defaultPath
     */
    protected Path showFileChooser(String windowTitle, int mode, Path defaultPath) {
        Path path = defaultPath;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(mode);
        chooser.setDialogTitle(windowTitle);
        chooser.setCurrentDirectory(getGuiManager().getLastUsedDirPath().toFile());
        JPanel parent = getGuiManager().getInpPnl();
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            path = chooser.getSelectedFile().toPath();
            getGuiManager().setLastUsedDirPath(path);
        }
        return path;
    }

}
