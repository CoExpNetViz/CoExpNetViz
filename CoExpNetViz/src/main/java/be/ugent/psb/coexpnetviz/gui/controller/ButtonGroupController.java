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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import be.ugent.psb.util.ButtonGroups;
import be.ugent.psb.util.mvc.model.ValueChangeListener;
import be.ugent.psb.util.mvc.model.ValueModel;

/**
 * Keep a ValueModel<String> in sync with a ButtonGroup.
 * 
 * Strings are matched to buttons by their action command (setActionCommand).
 * The value indicates which button should be selected.
 * 
 * Additions/deletions to the ButtonGroup after controller construction will be ignored.
 * Behaviour is undefined if the action command of any of the buttons is changed. 
 */
public class ButtonGroupController {
//TODO probably don't need apache commons collections, so rm that from pom
    public ButtonGroupController(final ValueModel<String> selected, final ButtonGroup buttonGroup) {
    	// When model value changes, set selection on buttonGroup
    	selected.addListener(new ValueChangeListener<String>() {
			@Override
			public void valueChanged(ValueModel<String> source, String oldValue) {
				final String selected = source.get();
				AbstractButton button = Iterables.find(ButtonGroups.getElements(buttonGroup), new Predicate<AbstractButton>() {
					@Override
					public boolean apply(AbstractButton button) {
						return button.getActionCommand().equals(selected);
					}
				});
				button.setSelected(true);
			}
		});
    	
		// When buttonGroup selection changes, update model value
    	ActionListener updateModel = new ActionListener() {
    		@Override
			public void actionPerformed(ActionEvent e) {
				selected.set(e.getActionCommand());
			}
    	};
		for (AbstractButton button : ButtonGroups.getElements(buttonGroup)) {
			button.addActionListener(updateModel);
		}
    }
    
}
