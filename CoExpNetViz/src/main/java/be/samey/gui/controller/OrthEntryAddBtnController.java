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
import be.samey.gui.OrthEntry;
import be.samey.gui.model.OrthEntryModel;
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author sam
 */
public class OrthEntryAddBtnController extends AbstrController implements ActionListener {

    public OrthEntryAddBtnController(CyAppManager cyAppManager) {
        super(cyAppManager);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (getActiveModel().getAllOrthGroups().size() >= CyModel.MAX_ORTHGROUP_COUNT) {

            JOptionPane.showMessageDialog(((JComponent) ae.getSource()),
                String.format("No more than %d orthologous group files are supported", CyModel.MAX_ORTHGROUP_COUNT));
            return;
            
        }
        OrthEntryModel oem = new OrthEntryModel();
        OrthEntry oe = getGuiManager().initOrthEntry(oem);
        getActiveModel().addOrthGroup(oem, oe);
    }

}
