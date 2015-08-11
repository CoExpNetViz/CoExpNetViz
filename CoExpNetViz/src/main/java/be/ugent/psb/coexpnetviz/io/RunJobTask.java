package be.ugent.psb.coexpnetviz.io;

import java.nio.file.Path;

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

/**
 * Communication with CoExpNetViz server that handles jobs (in reality, this may be an intermediate)
 */
public class RunJobTask implements Task {

	private JobServer jobServer;
	private JobDescription jobDescription;
	private Path unpackedResult;
	
	public RunJobTask(JobServer jobServer, JobDescription jobDescription) {
		super();
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
		unpackedResult = jobServer.runJob(jobDescription);
	}
	
	public Path getUnpackedResult() {
		return unpackedResult;
	}
	
}
