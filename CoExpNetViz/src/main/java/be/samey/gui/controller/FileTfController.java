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
import be.samey.gui.model.GuiModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.text.JTextComponent;

/**
 *
 * @author sam
 */
public abstract class FileTfController extends FocusAdapter {

    private GuiModel guiModel;

    public FileTfController() {
    }

    public FileTfController(GuiModel guiModel) {
        this.guiModel = guiModel;
    }

    public GuiModel getGuiModel() {
        return guiModel;
    }

    public void setGuiModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }

    public abstract void updateModel(GuiModel guiModel, Path path);

    @Override
    public void focusLost(FocusEvent fe) {
        super.focusLost(fe);
        JTextComponent tc = (JTextComponent) fe.getSource();
        Path path = Paths.get(tc.getText());
        updateModel(guiModel, path);

    }

}
