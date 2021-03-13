package be.ugent.psb.coexpnetviz.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import org.cytoscape.work.TaskMonitor.Level;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import be.ugent.psb.coexpnetviz.CENVContext;
import be.ugent.psb.coexpnetviz.InputError;
import be.ugent.psb.coexpnetviz.JsonParserThread;
import be.ugent.psb.coexpnetviz.ReaderThread;
import be.ugent.psb.coexpnetviz.UserException;

/* Create a co-expression network starting from an empty network  
 * 
 * Tunables notes: tunables are only picked up when public field/property in a public class.
 * Tunable supports File, but not Path, so we use File here. A File with
 * input=True will browse to a file for reading, else for writing (e.g.
 * the latter will ask to confirm to overwrite a file); the former still
 * lets you open files which do not exist though. Tunable(tooltip) only shows
 * up in the GUI, Tunable(longDescription) only shows up on the CLI.
 * Tunable(exampleStringValue) and Tunable(required) do not seem to do anything
 * in cytoscape 3.8.2.
 * 
 * Task notes: Always prefer using showMessage as it works for both CLI/GUI.
 * Task javadoc says all information regarding an exception should be contained
 * in the exception and indeed setStatusMessage should not be used for extra
 * information as e.g. in the GUI that just disappears but showMessage always
 * ends up in the CLI or task history (GUI). Exceptions also end up in CLI and
 * task history. Task history is hidden by default so better include all info
 * in the exception. CLI expects <br> as line ending, GUI expects \n.
 */
public class RunJobTask implements Task, TunableValidator {

	private CENVContext context;
	
	/* Can't 'dependsOn=' for enabling when the other is empty, so we use a
	 * source field and depend on that. xorKey is also an option but it requires
	 * using groups which makes for a more cluttered dialog.
	 * 
	 * Would have liked this to appear before the other baitGroup options in CLI
	 * help but Tunable(gravity) has no effect on it.
	 */
	@Tunable(description = "Bait group source", tooltip = "Provide baits inline in a text field or use a baits file.", longDescription="Whether you'll provide baits inline with baitGroupText or as a baitGroupFile")
	public ListSingleSelection<String> baitGroupSource = new ListSingleSelection<>("Inline", "File");

	private static final String baitGroupTextHelp = "Bait genes separated by ',' or ';'. E.g. AT2G03340;AT2G03341;AT2G03342";
	
	@Tunable(description = "Bait names", dependsOn = "baitGroupSource=Inline", tooltip = baitGroupTextHelp, longDescription = baitGroupTextHelp)
	public String baitGroupText;
	
	private static final String baitGroupFileHelp = "Path to file containing bait genes";  // TODO which format?

	@Tunable(description = "Bait group file", dependsOn = "baitGroupSource=File", params="input=true", tooltip = baitGroupFileHelp, longDescription = baitGroupFileHelp)
	public File baitGroupFile;

	private static final String expressionMatricesHelp = "One or more matrix files separated by ',' or ';'. E.g. /home/user/matrix.txt;/data/matrix2.txt on a unix-like OS or C:\\matrix.txt on Windows.";
	
	/* List<T> is not supported by Tunable; ListMultiSelection is for selecting
	 * multiple values from a list, not building a list of values. Cytoscape google
	 * groups did not mention a way of doing List<File>; you could implement your
	 * own GUITunableHandler and ..Factory for it or maybe some other app already
	 * offers one, seems quite doable but this will do. Cytoscape's FileHandler
	 * implementation is a good start probably.
	 */
	@Tunable(description = "Expression matrices", tooltip = expressionMatricesHelp, longDescription = expressionMatricesHelp)
	public String expressionMatrices;
	
	private static final String geneFamiliesFileHelp = "Optionally, file containing gene families to group genes by.";
	
	@Tunable(description = "Gene families file", params="input=true", tooltip = geneFamiliesFileHelp, longDescription = geneFamiliesFileHelp)
	public File geneFamiliesFile;

	private static final String percentilesHelp = "For each expression matrix, consider genes co-expressed if their correlation is less or equal to the lower percentile or greater or equal to the upper percentile of the correlations between a sample of genes of the expression matrix.";

	@Tunable(description = "Lower percentile rank", tooltip = percentilesHelp, longDescription = percentilesHelp)
	public BoundedDouble lowerPercentileRank = new BoundedDouble(0.0, 5.0, 100.0, false, false);

	@Tunable(description = "Upper percentile rank", tooltip = percentilesHelp, longDescription = percentilesHelp)
	public BoundedDouble upperPercentileRank = new BoundedDouble(0.0, 95.0, 100.0, false, false);

	// TODO put next to cyto session file instead
	@Tunable(description = "Output dir")
	public File outputDir;
	
	// These are sets to remove duplicates
	private Set<File> expressionMatrixFiles;
	private Set<String> cleanedBaits;

	// volatile allows multiple threads to access it
	private volatile boolean cancelled = false;
	
	// The thread that called run(), if any
	private volatile Thread thread;
	
	public RunJobTask(CENVContext context) {
		super();
		this.context = context;
		baitGroupSource.setSelectedValue("File");
	}
	
	@Override
	public ValidationState getValidationState(Appendable msg) {
		try {
			if (baitGroupSource.getSelectedValue() == "Inline") {
				cleanBaits();
			} else {
				baitGroupFile = cleanInputFile("Bait group file", baitGroupFile, true);
			}
			
			cleanExpressionMatrices();
			
			geneFamiliesFile = cleanInputFile("Gene families", geneFamiliesFile, false);
			
			// BoundedDouble already checks bounds and ensures a valid is entered
			// (despite Tunable(required=false) being the default)
			if (lowerPercentileRank.getValue() > upperPercentileRank.getValue()) {
				throw new InputError("Lower percentile rank must be less or equal to upper percentile rank.");
			}
		} catch (InputError e) {
			try {
				msg.append(e.getMessage());
				return ValidationState.INVALID;
			} catch (IOException e2) {
				// msg.append throws it for some reason, better make sure the user finds
				// out, so rethrow instead of silencing it here
				throw new RuntimeException(e2);
			}
		}
		
		return ValidationState.OK;
	}
	
	private void cleanBaits() throws InputError {
		String[] baits = baitGroupText.split("[,;]");
		cleanedBaits = new HashSet<String>();
		for (String bait : baits) {
			bait = bait.trim();
			if (bait.isEmpty()) {
				continue;
			}
			cleanedBaits.add(bait);
		}
		if (cleanedBaits.size() < 2) {
			throw new InputError("At least 2 bait names are required.");
		}
	}
	
	private void cleanExpressionMatrices() throws InputError {
		String[] paths = expressionMatrices.split("[,;]");
		expressionMatrixFiles = new HashSet<File>();
		for (String path : paths) {
			path = path.trim();
			if (path.isEmpty()) {
				continue;
			}
			File file = new File(path);
			file = cleanInputFile(path, file, true);
			expressionMatrixFiles.add(file);
		}
		
		if (expressionMatrixFiles.isEmpty()) {
			throw new InputError("At least 1 expression matrix is required.");
		}
	}

	private File cleanInputFile(String name, File file, boolean required) throws InputError {
		// File tunable lets you enter files which do not exist, so we still 
		// need to check everything
		if (file == null) {
			if (required) {
				throw new InputError(name + " is required.");
			}
			else {
				return file;
			}
		} else if (!file.exists()) {
			throw new InputError(name + " does not exist.");
		} else if (!file.isFile()) {
			throw new InputError(name + " is not a (regular) file.");
		} else if (!file.canRead()) {
			throw new InputError(name + " is not readable.");
		}
		return file.getAbsoluteFile();
	}

	@Override
	public void cancel() {
		/* 99.999999999% of the time there is a Thread, in the off chance someone
		 * manages to cancel the task before it runs its first statement, there's
		 * the cancelled var.
		 */
		if (thread == null) {
			cancelled = true;
		} else {
			/* The thread will get interrupted the next time it blocks on
			 * something (or right away if already blocked). It can also
			 * check thread.interrupted() when doing non-blocking stuff to
			 * see whether it was cancelled; though next time you call
			 * interrupted() it will return false again!
			 */
			thread.interrupt();
		}
	}
	
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		/* Tasks are single use so there's no need to unset the thread var. 
		 * Unsetting would cause a race condition in cancel() where it gets
		 * past the if, thread gets unset and then it tries to call
		 * `null.interrupt`.
		 * 
		 * We only need to check `cancelled` once here, beyond that you can
		 * forget about it.
		 */
		thread = Thread.currentThread();
		if (cancelled) {
			return;
		}
		
		try {
			JsonNode response = callBackend(monitor);
			
			// TODO turn into a network
			monitor.setStatusMessage("Bla bla next step");
			monitor.setProgress(0.5);
		} catch (InterruptedException e) {
			/* Happens when cancelled, just ignore it and stop running.
			 * If we let it bubble up, Cytoscape will show an error.
			 */
		}
	}

	private void showMessage(TaskMonitor monitor, Level level, String msg) {
		// TODO discern between GUI/CLI. CLI wants <br>, GUI doesn't. Or report upstream, maybe they should fix it.
		monitor.showMessage(level, msg.replaceAll("\n", "<br>\n"));
	}

	private JsonNode callBackend(TaskMonitor monitor) throws UserException, InterruptedException {
		// conda does not pass on stdin to coexpnetviz unless --no-capture-output is specified.
		final String command = "conda run -n coexpnetviz --no-capture-output coexpnetviz";
		monitor.setStatusMessage("Running python backend: " + command);
		
		Process process = null;
		JsonParserThread stdoutThread = null;
		ReaderThread stderrThread = null;
		int exitCode;
		try {
			// Start backend process
			try {
				process = Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				throw new UserException("Failed to exec backend command", e);
			}
			
			/* Read stdout/err in a separate thread to avoid blocking the backend process.
			 * If we wait for the backend process to exit before reading, the backend process
			 * may fill up its output buffer and start waiting for us to read it; i.e. deadlock.
			 */
			stdoutThread = new JsonParserThread(process.getInputStream());
			stderrThread = new ReaderThread(process.getErrorStream());
			stdoutThread.start();
			stderrThread.start();
			
			// Pass json input to backend process
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonInput = createJsonInput(mapper);
			try {
				mapper.writeValue(process.getOutputStream(), jsonInput);
				process.getOutputStream().close();
			} catch (IOException e) {
				throw new UserException("Failed to send json input to backend", e);
			}
			
			/* Wait for backend process and our reader threads to exit
			 * 
			 * Simply waiting for it to close its stdout is not enough and that also
			 * wouldn't allow cancelling the task.
			 */
			exitCode = process.waitFor();
			stdoutThread.join();
			stderrThread.join();
		} catch (InterruptedException e) {
			showMessage(monitor, Level.WARN, "Cancelled");
			
			if (process != null) {
				monitor.setStatusMessage("Killing backend process");
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
		
		// Mention the log file, if any
		Path logFile = outputDir.toPath().resolve("coexpnetviz.log");
		if (Files.exists(logFile)) {
			showMessage(monitor, Level.INFO, "Backend log file: " + logFile);
		}
		
		// Report crash to user, if any
		if (exitCode != 0) {
			StringBuilder msg = new StringBuilder();
			msg.append("python backend exited non-zero: ").append(process.exitValue()).append("\n");
			
			try {
				msg.append("stderr: ").append(stderrThread.getOutput());
			} catch (IOException e) {
				showMessage(monitor, Level.WARN, "Failed to read stderr from backend process: " + e.toString());
				e.printStackTrace();
			}
			
			throw new UserException(msg.toString());
		}
		
		try {
			return stdoutThread.getResponse();
		} catch (IOException e) {
			throw new UserException("Failed to read or parse response from backend process.", e);
		}
	}
	
	/*
	 * Formulate json for a call to coexpnetviz-python
	 */
	private ObjectNode createJsonInput(ObjectMapper mapper) {
		ObjectNode root = mapper.createObjectNode();
		
		if (baitGroupSource.getSelectedValue() == "Inline") {
			ArrayNode baitsArray = root.putArray("baits");
			for (String bait : cleanedBaits) {
				baitsArray.add(bait);
			}
		} else {
			root.put("baits", baitGroupFile.toString());
		}
		
		ArrayNode matrixArray = root.putArray("expression_matrices");
		for (File matrix : expressionMatrixFiles) {
			matrixArray.add(matrix.toString());
		}
		
		if (geneFamiliesFile != null) {
			root.put("gene_families", geneFamiliesFile.toString());
		}
		
		root.put("lower_percentile_rank", lowerPercentileRank.getValue());
		root.put("upper_percentile_rank", upperPercentileRank.getValue());
		root.put("output_dir", outputDir.toString());
		
		return root;
	}
	
}
