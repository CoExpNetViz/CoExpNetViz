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

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import be.ugent.psb.coexpnetviz.CENVApplication;
import be.ugent.psb.coexpnetviz.gui.CENVModel;

/**
 *
 * @author sam
 */
public class CutoffController extends AbstrController implements ChangeListener {

    public CutoffController(CENVApplication cyAppManager) {
        super(cyAppManager);
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        if (ce.getSource() instanceof JSpinner) {

            JSpinner sp = (JSpinner) ce.getSource();
            double cutOff = (Double) sp.getValue();

            if (sp.getName().equals("nCutoffSp")) {
                getActiveModel().setNegCutoff(cutOff);
                return;
            }
            if (sp.getName().equals("pCutoffSp")) {
                getActiveModel().setPosCutoff(cutOff);
                return;
            }
        }

        if (cyAppManager.getGUIController() != null) {
            System.err.println(CENVModel.APP_NAME + ": Failed to update " + getActiveModel() + " from " + ce.getSource());
        }

    }

}
