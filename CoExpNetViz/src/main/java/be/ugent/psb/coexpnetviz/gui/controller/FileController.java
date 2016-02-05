package be.ugent.psb.coexpnetviz.gui.controller;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2016 PSB/UGent
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import be.ugent.psb.coexpnetviz.gui.model.StringModel;

/**
 * Controls text field with path and browse button to browse for a file
 */
public class FileController {

    public FileController(final String browseDialogTitle, final StringModel path, final JTextField path_text_field, final JButton browse_button, final Component dialogParent) {
    	new StringController(path, path_text_field);
        
        browse_button.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent event) {
        		JFileChooser fileChooser = new JFileChooser(path.get());
        		fileChooser.setDialogTitle(browseDialogTitle);
        		fileChooser.setMultiSelectionEnabled(false);
        		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        		if (fileChooser.showOpenDialog(dialogParent) == JFileChooser.APPROVE_OPTION) {
        			path.set(fileChooser.getSelectedFile().toString());
        			path.notifyObservers();
        		}
            }
        });
    }
    
    // TODO add validation to path, icon goes on the text field.
    //      It must be a file, it must exist, it should be plain text and have read permissions (Files.isRegularFile(orthFilePath) && Files.isReadable(orthFilePath))
    
}
