/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author sam
 */
public class DelSpeciesController implements ActionListener{

    private InpProfileModel inpProfileModel;
    private SpeciesEntryModel speciesEntryModel;

    public DelSpeciesController() {
    }

    public DelSpeciesController(InpProfileModel inpProfileModel, SpeciesEntryModel speciesEntryModel) {
        this.inpProfileModel = inpProfileModel;
        this.speciesEntryModel = speciesEntryModel;
    }


    public void setInpProfileModel(InpProfileModel inpProfileModel) {
        this.inpProfileModel = inpProfileModel;
    }

    public void setSpeciesEntryModel(SpeciesEntryModel speciesEntryModel) {
        this.speciesEntryModel = speciesEntryModel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

            if (inpProfileModel.getSpeciesEntryModels().size() == 1) {
                JOptionPane.showMessageDialog(((JComponent) ae.getSource()),
                    "You must use at least one dataset");
                return;
            }
            inpProfileModel.removeSpeciesEntryModel(speciesEntryModel);
    }
    
}