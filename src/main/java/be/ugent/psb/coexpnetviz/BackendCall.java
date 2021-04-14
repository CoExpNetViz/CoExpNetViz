/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2021 PSB/UGent
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

package be.ugent.psb.coexpnetviz;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskMonitor.Level;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BackendCall extends CondaCall {
	
	private static String CONDA_ARGS = String.format("run -n %s --no-capture-output coexpnetviz", CondaCall.CONDA_ENV);
	
	private File outputDir;
	private ObjectNode jsonInput;
	private JsonParserThread stdoutThread;
	
	public BackendCall(TaskMonitor monitor, Context context, File outputDir, ObjectNode jsonInput) {
		super(context, monitor, CONDA_ARGS);
		stdoutThread = new JsonParserThread("json response from backend", context.getJsonMapper());
		this.outputDir = outputDir;
		this.jsonInput = jsonInput;
	}
	
	protected AbstractReaderThread getStdoutThread() {
		return stdoutThread;
	}

	protected void writeInputToProcess(OutputStream outputStream) throws UserException {
		// Pass json input to backend process
		try {
			context.getJsonMapper().writeValue(outputStream, jsonInput);
		} catch (IOException e) {
			throw new UserException("Failed to send json input to backend: " + e.toString(), e);
		}
		super.writeInputToProcess(outputStream);
	}
	
	protected void onProcessExited() {
		// Mention the log file, if any
		Path logFile = outputDir.toPath().resolve("coexpnetviz.log");
		if (Files.exists(logFile)) {
			showMessage(Level.INFO, "Backend log file: " + logFile);
		}
	}

	public JsonNode getResponse() throws UserException {
		return stdoutThread.getOutput();
	}
	
}
