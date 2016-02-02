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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JToggleButton;

import be.ugent.psb.coexpnetviz.CENVApplication;
import be.ugent.psb.coexpnetviz.gui.CENVModel;

/**
 *
 * @author sam
 */
public class BaitFileOrInpController extends AbstrController implements ActionListener {

    public BaitFileOrInpController(CENVApplication cyAppManager) {
        super(cyAppManager);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JToggleButton) {

            JToggleButton jtb = ((JToggleButton) ae.getSource());

            if (jtb.getName().equals("baitFileRb")) {
                boolean useBaitFile = jtb.isSelected();
                getActiveModel().setUseBaitsFile(useBaitFile);
                return;
            }

            if (jtb.getName().equals("baitInpRb")) {
                boolean inpBaits = jtb.isSelected();
                getActiveModel().setUseBaitsFile(!inpBaits);
                return;
            }
        }

        if (application.getGUIController() != null) {
            System.err.println(CENVApplication.APP_NAME + ": Failed to update " + getActiveModel() + " from " + ae.getSource());
        }
    }

}
