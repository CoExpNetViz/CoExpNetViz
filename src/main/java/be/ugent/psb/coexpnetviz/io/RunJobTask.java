package be.ugent.psb.coexpnetviz.io;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import be.ugent.psb.coexpnetviz.CENVContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Communication with CoExpNetViz server that handles jobs (in reality, this may be an intermediate)
 */
public class RunJobTask implements Task {

	private CENVContext context;
	private JobServer jobServer;
	private JobDescription jobDescription;
	
	public RunJobTask(CENVContext context, JobServer jobServer, JobDescription jobDescription) {
		super();
		this.context = context;
		this.jobServer = jobServer;
		this.jobDescription = jobDescription;
	}

	@Override
	public void cancel() {
		jobServer.abort();
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		tm.setStatusMessage("Running job on CoExpNetViz server");
		try {
		jobServer.runJob(jobDescription);
		}
		catch (final JobServerException e) {
			// Cytoscape's exception dialog is too small, so we show one instead
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(context.getCySwingApplication().getJFrame(), e.getMessage(), "CoExpNetViz server error", JOptionPane.ERROR_MESSAGE);
				}
			});
			
			throw new RuntimeException("CoExpNetViz job failed due to server error");
		}
	}
	
}
