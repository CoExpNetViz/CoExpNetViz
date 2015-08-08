package be.ugent.psb.coexpnetviz.internal;

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

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;

import be.ugent.psb.coexpnetviz.gui.CENVController;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class MenuAction extends AbstractCyAction {

    //model should be created only once, keeps track of application state
    private final CyAppManager cyAppManager;

    public MenuAction(CyApplicationManager cyApplicationManager, final String menuTitle, CyAppManager cyAppManager) {
        super(menuTitle, cyApplicationManager, null, null);
        setPreferredMenu("Apps");

        this.cyAppManager = cyAppManager;
    }

    //invoked on clicking the app in the menu
    @Override
    public void actionPerformed(ActionEvent e) {

        //if there is no gui, then create it
        if (cyAppManager.getGuiManager() == null) {

            //create the Gui
            CENVController gm = new CENVController(cyAppManager);

            //rember that we have made the gui
            cyAppManager.setGuiManager(gm);
        }

        //pack and show the gui in a window
        cyAppManager.getGuiManager().showRootFrame();

    }

}
