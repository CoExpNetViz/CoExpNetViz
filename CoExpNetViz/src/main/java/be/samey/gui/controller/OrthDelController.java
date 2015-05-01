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

import be.samey.gui.model.OrthEntryModel;
import be.samey.internal.CyAppManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author sam
 */
public class OrthDelController extends AbstrController implements ActionListener {
    
    private OrthEntryModel oem;
    
    public OrthDelController(OrthEntryModel oem, CyAppManager cyAppManager) {
        super(cyAppManager);
        this.oem = oem;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        getActiveModel().removeOrthGroup(oem);
    }
    
}
