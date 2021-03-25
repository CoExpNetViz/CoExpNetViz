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

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskMonitor.Level;

public class CondaCall {

	public static final String CONDA_ENV = Context.NAMESPACE;
	
	private TaskMonitor monitor;
	private String condaArgs;
	private AbstractReaderThread stdoutThread;

	public CondaCall(TaskMonitor monitor, String condaArgs, AbstractReaderThread stdoutThread) {
		super();
		this.monitor = monitor;
		this.condaArgs = condaArgs;
		this.stdoutThread = stdoutThread;
	}
	
	protected CondaCall(TaskMonitor monitor, String condaArgs) {
		super();
		this.monitor = monitor;
		this.condaArgs = condaArgs;
		this.stdoutThread = null;
	}
	
	protected AbstractReaderThread getStdoutThread() {
		return stdoutThread;
	}

	public void run() throws UserException, InterruptedException {
		// Conda does not pass on stdin to coexpnetviz unless --no-capture-output is specified.
		final String command = "conda " + condaArgs;
		showMessage(Level.INFO, "Executing: " + command);
		
		Process process = null;
		AbstractReaderThread stdoutThread = getStdoutThread();
		TextReaderThread stderrThread = null;
		int exitCode;
		try {
			// Start conda process
			try {
				process = Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				throw new UserException("Failed to execute conda: " + e.toString(), e);
			}
			
			/* Read stdout/err in a separate thread to avoid blocking the backend process.
			 * If we wait for the backend process to exit before reading, the backend process
			 * may fill up its output buffer and start waiting for us to read it; i.e. deadlock.
			 */
			stdoutThread.setInputStream(process.getInputStream());
			stderrThread = new TextReaderThread("stderr from conda", process.getErrorStream());
			stdoutThread.start();
			stderrThread.start();
			
			writeInputToProcess(process.getOutputStream());
			
			/* Wait for backend process and our reader threads to exit
			 * 
			 * Simply waiting for it to close its stdout is not enough and that also
			 * wouldn't allow cancelling the task.
			 */
			exitCode = process.waitFor();
			stdoutThread.join();
			stderrThread.join();
		} catch (InterruptedException e) {
			showMessage(Level.WARN, "Cancelled");
			
			if (process != null) {
				monitor.setStatusMessage("Killing conda process");
				process.destroy(); // TODO backend probably does not respond to sigterm, it should.
				try {
					process.waitFor(5, TimeUnit.SECONDS);
				} catch (InterruptedException e1) {
				}
				process.destroyForcibly();
			}
			
			monitor.setStatusMessage("Killing reader threads");
			if (stdoutThread != null) {
				stdoutThread.interrupt();
			}
			if (stderrThread != null) {
				stderrThread.interrupt();
			}
			
			throw e;
		}
		
		onProcessExited();
		
		// Report crash to user, if any.
		if (exitCode != 0) {
			final int BROKEN_PIPE = 120;
			if (exitCode == BROKEN_PIPE) {
				/* The command encounters a broken pipe when we stop reading stdout/err
				 * before it closes those streams itself. This happens when our stdout/err
				 * thread dies due to an exception. E.g. most likely stdoutThread failed to
				 * parse stdout. In that case, we show stdout's exception here, otherwise we
				 * just mention it exited non-zero. Either way we'll show a message when
				 * something went wrong with stderr.   
				 */
				stdoutThread.throwIfCaughtException();
			}
			throwExitedNonZero(stderrThread, exitCode);
		}
	}

	protected void writeInputToProcess(OutputStream outputStream) throws UserException {
		try {
			outputStream.close();
		} catch (IOException e) {
			throw new UserException("Failed to close input stream of conda: " + e.toString(), e);
		}
	}

	protected void onProcessExited() {
	}
	
	private void throwExitedNonZero(TextReaderThread stderrThread, int exitCode) throws UserException {
		StringBuilder msg = new StringBuilder();
		msg.append("Conda exited non-zero: ").append(exitCode).append("\n");
		
		try {
			msg.append("stderr: ").append(stderrThread.getOutput());
		} catch (UserException e) {
			showMessage(Level.WARN, e.getMessage());
		}
		
		throw new UserException(msg.toString());
	}
	
	protected void showMessage(Level level, String msg) {
		// TODO discern between GUI/CLI. CLI only understands <br>, GUI only understands \n.
		// Or report upstream, maybe they should fix it.
		monitor.showMessage(level, msg.replaceAll("\n", "<br>\n"));
	}

}
