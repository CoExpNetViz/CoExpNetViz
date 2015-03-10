package be.samey.internal;

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

import be.samey.gui.RootPanel;
import be.samey.model.Model;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class CevMenuAction extends AbstractCyAction {

    private final Model model;
    JFrame rootPanelFrame;

    public CevMenuAction(CyApplicationManager cyApplicationManager, final String menuTitle, Model model) {

        super(menuTitle, cyApplicationManager, null, null);
        setPreferredMenu("Apps");
        this.model = model;
    }

    //invoked on clicking the app in the menu
    @Override
    public void actionPerformed(ActionEvent e) {
        //only make a new window if the app is launched for the first time
        if (model.getGuiStatus().getRootPanelFrame() == null) {
            //create a new window
            rootPanelFrame = new JFrame("Co-expression Network Visualization Tool");
            rootPanelFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            //root panel for the main window, contains the tabs with options
            RootPanel rootPanel = new RootPanel(model);
            model.getGuiStatus().setRootPanel(rootPanel);
            rootPanelFrame.setContentPane(rootPanel);
            model.getGuiStatus().setRootPanelFrame(rootPanelFrame);
        } else {
            rootPanelFrame = model.getGuiStatus().getRootPanelFrame();
        }
        //pack and show the window
        rootPanelFrame.pack();
        rootPanelFrame.setVisible(true);
    }
}
