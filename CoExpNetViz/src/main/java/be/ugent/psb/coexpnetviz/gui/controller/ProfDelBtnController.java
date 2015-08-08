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
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JOptionPane;

import be.ugent.psb.coexpnetviz.internal.CyAppManager;

/**
 *
 * @author sam
 */
public class ProfDelBtnController extends AbstrController implements ActionListener {

    public ProfDelBtnController(CyAppManager cyAppManager) {
        super(cyAppManager);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (Arrays.asList(getGuiManager().getProfileTitles()).contains(getActiveModel().getTitle())) {
            getGuiManager().delCurrentProfile();
            JOptionPane.showMessageDialog(getGuiManager().getInpPnl(),
                "\"" + getActiveModel().getTitle() + "\" was removed from your settings",
                "Profile saved",
                JOptionPane.PLAIN_MESSAGE);
            try {
                getGuiManager().saveProfiles();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(getGuiManager().getInpPnl(),
                    "There was an error while saving the profile\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(getGuiManager().getInpPnl(),
                "The profile \"" + getActiveModel().getTitle() + "\" has never been saved before\n",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        }
    }

}
