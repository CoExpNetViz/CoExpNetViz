package be.ugent.psb.coexpnetviz.gui.controller;

import be.ugent.psb.coexpnetviz.CENVApplication;
import be.ugent.psb.coexpnetviz.gui.CENVModel;

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

import be.ugent.psb.coexpnetviz.gui.model.InputPanelModel;
import be.ugent.psb.coexpnetviz.gui.model.OrthEntryModel;
import be.ugent.psb.coexpnetviz.gui.model.SpeciesEntryModel;
import be.ugent.psb.coexpnetviz.gui.view.OrthEntryPanel;
import be.ugent.psb.coexpnetviz.gui.view.SpeciesEntryPanel;

import java.util.Map;

/**
 *
 * @author sam
 */
public abstract class AbstrController {

    protected final CENVApplication cyAppManager;
    protected final CENVModel cyModel;

    public AbstrController(CENVApplication cyAppManager) {
        this.cyAppManager = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();
    }

    protected GUIController getGuiManager(){
        return cyAppManager.getGUIController();
    }

    protected InputPanelModel getActiveModel() {
        return getGuiManager().getActiveModel();
    }

    protected Map<SpeciesEntryModel, SpeciesEntryPanel> getAllSpecies(){
        return getGuiManager().getActiveModel().getAllSpecies();
    }

    protected Map<OrthEntryModel, OrthEntryPanel> getAllOrthGroups(){
        return getGuiManager().getActiveModel().getAllOrthGroups();
    }

}
