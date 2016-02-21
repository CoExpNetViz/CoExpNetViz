package be.ugent.psb.coexpnetviz.gui;

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

import org.cytoscape.application.swing.AbstractCyAction;

import be.ugent.psb.coexpnetviz.Context;
import be.ugent.psb.coexpnetviz.gui.jobinput.JobInputFrameController;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class MenuAction extends AbstractCyAction {
	
    private final Context context;
    private JobInputFrameController jobInputFrameController;

    public MenuAction(Context context) {
        super(Context.APP_NAME, context.getCyApplicationManager(), null, null);
        setPreferredMenu("Apps");
        this.context = context;
    }

    // menu clicked
    @Override
    public void actionPerformed(ActionEvent e) {
    	if (jobInputFrameController == null) {
            jobInputFrameController = new JobInputFrameController(context);
        }
        jobInputFrameController.show();
    }

}
