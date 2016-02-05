package be.ugent.psb.coexpnetviz.gui.controller;

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
