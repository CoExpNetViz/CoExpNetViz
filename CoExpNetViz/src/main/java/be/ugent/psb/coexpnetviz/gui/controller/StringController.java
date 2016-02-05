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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import be.ugent.psb.util.mvc.model.ValueModel;

/**
 * Keep a ValueModel<String> in sync with a JTextComponent.
 */
public class StringController {

    public StringController(final ValueModel<String> string, final JTextComponent text_field) {
    	assert false;
//    	string.addObserver(new Observer() {
//			@Override
//			public void update(Observable observable, Object arg) {
//				text_field.setText(((ValueModel<String>)observable).get().toString());
//			}
//    	});
//    	
//    	text_field.addFocusListener(new FocusListener() {
//    		@Override
//    	    public void focusLost(FocusEvent fe) {
//    	        string.set(((JTextField)fe.getSource()).getText());
//    	        string.notifyObservers();
//    	    }
//
//			@Override
//			public void focusGained(FocusEvent e) {
//			}
//        });
    }
    
}
