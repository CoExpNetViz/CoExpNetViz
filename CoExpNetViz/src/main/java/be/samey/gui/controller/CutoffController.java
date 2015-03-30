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

import be.samey.gui.model.InpPnlModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author sam
 */
public abstract class CutoffController implements ChangeListener {

    private InpPnlModel inpPnlModel;

    public CutoffController() {
    }

    public CutoffController(InpPnlModel inpPnlModel) {
        this.inpPnlModel = inpPnlModel;
    }
    
    public void setGuiModel(InpPnlModel inpPnlModel) {
        this.inpPnlModel = inpPnlModel;
    }

    public abstract void updateModel(InpPnlModel inpPnlModel, double cutOff);

    @Override
    public void stateChanged(ChangeEvent ce) {
        JSpinner sp = (JSpinner) ce.getSource();
        double cutOff = (Double) sp.getValue();
        updateModel(inpPnlModel, cutOff);
    }

}
