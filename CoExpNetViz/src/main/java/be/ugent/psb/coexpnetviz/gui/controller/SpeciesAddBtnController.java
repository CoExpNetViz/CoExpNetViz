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

import be.ugent.psb.coexpnetviz.gui.SpeciesEntry;
import be.ugent.psb.coexpnetviz.gui.model.SpeciesEntryModel;
import be.ugent.psb.coexpnetviz.internal.CyAppManager;
import be.ugent.psb.coexpnetviz.internal.CyModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author sam
 */
public class SpeciesAddBtnController extends AbstrController implements ActionListener {

    public SpeciesAddBtnController(CyAppManager cyAppManager) {
        super(cyAppManager);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (getActiveModel().getAllSpecies().size() >= CyModel.MAX_SPECIES_COUNT) {

            JOptionPane.showMessageDialog(((JComponent) ae.getSource()),
                String.format("No more than %d species are supported", CyModel.MAX_SPECIES_COUNT));
            return;
        }
        SpeciesEntryModel sem = new SpeciesEntryModel();
        SpeciesEntry se = getGuiManager().initSpeciesEntry(sem);
        getActiveModel().addSpecies(sem, se);
    }

}
