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
import javax.swing.JOptionPane;

import be.ugent.psb.coexpnetviz.internal.CyAppManager;

/**
 *
 * @author sam
 */
public class BaitInpInfoBtnController extends AbstrController implements ActionListener {

    public BaitInpInfoBtnController(CyAppManager cyAppManager) {
        super(cyAppManager);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String message
            = "<html>"
            + "Supported Gene ID symbols:"
            + "<br>"
            + "<br>"
            + "All default Plaza gene ID's are supported, some examples are:"
            + "<ul style=\"list-style: none;\">"
            + makeListEntry("Arabidopsis thaliana", "AT#G#####")
            + makeListEntry("Oriza sativa", "Os##g#######")
            + makeListEntry("Solanum lycopersicon", "Solyc##g######")
            + makeListEntry("Solanum tuberosum", "Sotub##g######")
            + "</ul>"
            + "<br>"
            + "Other supported ID's are:"
            + "<ul style=\"list-style: none;\">"
            + makeListEntry("Malus domestica", "MDP##########")
            + makeListEntry("Oriza sativa", "LOC_Os##g#######")
            + makeListEntry("Solanum tuberosum", "PGSC####DMP#########")
            + "</ul>"
            + "<br>"
            + "Any gene ID's are supported if they are also present"
            + "<br>"
            + "in user supplied gene family files"
            + "</html>";

        JOptionPane.showMessageDialog(getGuiManager().getInpPnl(), message);
    }

    private String makeListEntry(String species, String pattern) {
        pattern = pattern.replaceAll("(#+)", "<font color=\"blue\">$1</font>");
        return String.format("<li><pre><i>%-22s</i>: %s</pre></li>", species, pattern);
    }

}
