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
import be.samey.gui.model.SpeciesEntryModel;
import be.samey.internal.CyAppManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

/**
 *
 * @author sam
 */
public class SpeciesFileBtnController extends AbstrBrowseController implements ActionListener {

    private SpeciesEntryModel sem;

    public SpeciesFileBtnController(CyAppManager cyAppManager, SpeciesEntryModel sem) {
        super(cyAppManager);
        this.sem = sem;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Path path = showFileChooser(
            "Choose file with expression data",
            FILE,
            sem.getSpeciesFilePath());
        sem.setSpeciesExprDataPath(path);

        if (sem.getSpeciesName().trim().isEmpty()) {
            String name = path.getFileName().toString().split("\\.")[0];
            sem.setSpeciesName(name);
        }

    }
}
