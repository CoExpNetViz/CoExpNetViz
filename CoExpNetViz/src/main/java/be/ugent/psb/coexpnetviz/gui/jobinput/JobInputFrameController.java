package be.ugent.psb.coexpnetviz.gui.jobinput;

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

import javax.swing.JFrame;

import be.ugent.psb.coexpnetviz.Context;
import be.ugent.psb.util.TCCLRunnable;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;


/**
 * Creates and controls a JFrame with a JobInput pane
 */
public class JobInputFrameController {

    private final Context context;
    private JFrame rootFrame;

    public JobInputFrameController(Context context) { // XXX only accept a presets and a JobInput model
        this.context = context;
    }
    
    /**
     * Create frame if it does not exist yet
     * 
     * Only call this on the Swing event thread
     */
    private void ensureFrameIsCreated() {
    	if (rootFrame != null)
    		return;
    	
    	rootFrame = new JFrame("Co-expression Network Visualization Tool");
        rootFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        final JFXPanel fxPanel = new JFXPanel();
        rootFrame.getContentPane().add(fxPanel);

        Platform.runLater(new TCCLRunnable() {
            @Override
            public void runInner() {
            	JobInputPane jobInputPane = new JobInputPane();
            	Scene scene = new Scene(jobInputPane);
            	jobInputPane.init(context, scene.getWindow());
            	fxPanel.setScene(scene);
            }
        });
    }

    public void show() {
    	ensureFrameIsCreated();
        rootFrame.pack();
        rootFrame.setVisible(true);
    }

}
