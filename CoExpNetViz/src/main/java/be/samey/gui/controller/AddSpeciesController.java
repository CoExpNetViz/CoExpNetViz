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

import be.samey.gui.model.InpProfileModel;
import be.samey.gui.model.SpeciesEntryModel;
import be.samey.internal.CyModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author sam
 */
public class AddSpeciesController implements ActionListener{

    private InpProfileModel inpProfileModel;

    public AddSpeciesController() {
    }

    public AddSpeciesController(InpProfileModel inpProfileModel) {
        this.inpProfileModel = inpProfileModel;
    }

    public void setInpProfileModel(InpProfileModel inpProfileModel) {
        this.inpProfileModel = inpProfileModel;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (inpProfileModel.getSpeciesEntryModels().size() >= CyModel.MAX_SPECIES_COUNT){
            
                JOptionPane.showMessageDialog(((JComponent) ae.getSource()),
                    String.format("No more than %d species are supported", CyModel.MAX_SPECIES_COUNT));
                return;
        }
        SpeciesEntryModel sem = new SpeciesEntryModel();
        inpProfileModel.addSpeciesEntryModel(sem);
    }
    
}