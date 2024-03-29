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

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.values.LineType;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskMonitor.Level;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;

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
public class CreateNetworkTask extends AbstractTask implements TunableValidator {

	private Context context;
	
	/*
	 * Empty network to turn into a co-expression network
	 */
	private CyNetwork network;
	
	private static final String networkNameHelp = "Name of the co-expression network to create";
	
	@Tunable(description = "Network name", tooltip = networkNameHelp, longDescription = networkNameHelp)
	public String networkName;
	
	/* Can't 'dependsOn=' for enabling when the other is empty, so we use a
	 * source field and depend on that. xorKey is also an option but it requires
	 * using groups which makes for a more cluttered dialog.
	 * 
	 * Would have liked this to appear before the other baitGroup options in CLI
	 * help but Tunable(gravity) has no effect on it.
	 */
	@Tunable(description = "Baits source", tooltip = "Provide baits inline in a text field or use a baits file.", longDescription="Whether you'll provide baits inline with baitGroupText or as a baitGroupFile")
	public ListSingleSelection<String> baitsSource = new ListSingleSelection<>("Inline", "File");

	private static final String commonBaitsHelp = "Please provide at least 1 bait from each expression matrix and at least 2 baits in total.";
	private static final String baitGroupTextHelp = "Bait genes separated by ',' or ';'. " + commonBaitsHelp + " E.g. AT2G03340;AT2G03341;AT2G03342";
	
	@Tunable(description = "Bait names", dependsOn = "baitsSource=Inline", tooltip = baitGroupTextHelp, longDescription = baitGroupTextHelp)
	public String baitsText;
	
	private static final String baitGroupFileHelp = "Path to file containing bait genes separated by ';', whitespace or ','." + commonBaitsHelp;

	@Tunable(description = "Baits file", dependsOn = "baitsSource=File", params="input=true", tooltip = baitGroupFileHelp, longDescription = baitGroupFileHelp)
	public File baitsFile;

	private static final String expressionMatricesHelp = "One expression matrix file per species separated by ',' or ';'. E.g. /home/user/matrix.txt;/data/matrix2.txt on a unix-like OS or C:\\matrix.txt on Windows.";
	private static final String expressionMatrixHelp = "Path to expression matrix file of a species.";
	
	@Tunable(description = "Expression matrix 1", params="input=true", tooltip = expressionMatrixHelp, longDescription = expressionMatrixHelp)
	public File expressionMatrix1;
	
	@Tunable(description = "Expression matrix 2", params="input=true", tooltip = expressionMatrixHelp, longDescription = expressionMatrixHelp)
	public File expressionMatrix2;
	
	@Tunable(description = "Expression matrix 3", params="input=true", tooltip = expressionMatrixHelp, longDescription = expressionMatrixHelp)
	public File expressionMatrix3;
	
	@Tunable(description = "Expression matrix 4", params="input=true", tooltip = expressionMatrixHelp, longDescription = expressionMatrixHelp)
	public File expressionMatrix4;
	
	@Tunable(description = "Expression matrix 5", params="input=true", tooltip = expressionMatrixHelp, longDescription = expressionMatrixHelp)
	public File expressionMatrix5;
	
	/* List<T> is not supported by Tunable; ListMultiSelection is for selecting
	 * multiple values from a list, not building a list of values. Cytoscape google
	 * groups did not mention a way of doing List<File>; you could implement your
	 * own GUITunableHandler and ..Factory for it or maybe some other app already
	 * offers one, seems quite doable but this and the above 5 fields will do.
	 * Cytoscape's FileHandler implementation is a good start probably.
	 */
	@Tunable(description = "Expression matrices", tooltip = expressionMatricesHelp, longDescription = expressionMatricesHelp)
	public String expressionMatrices;
	
	private static final String geneFamiliesFileHelp = "Optionally, file containing gene families to group genes by.";
	
	@Tunable(description = "Gene families file", params="input=true", tooltip = geneFamiliesFileHelp, longDescription = geneFamiliesFileHelp)
	public File geneFamiliesFile;

	private static final String percentilesHelp = "For each expression matrix, consider genes co-expressed if their correlation is less or equal to the lower percentile or greater or equal to the upper percentile of the correlations between a sample of genes of the expression matrix.";

	@Tunable(description = "Lower percentile", tooltip = percentilesHelp, longDescription = percentilesHelp)
	public BoundedDouble lowerPercentile = new BoundedDouble(0.0, 5.0, 100.0, false, false);

	@Tunable(description = "Upper percentile", tooltip = percentilesHelp, longDescription = percentilesHelp)
	public BoundedDouble upperPercentile = new BoundedDouble(0.0, 95.0, 100.0, false, false);

	private static final String outputDirHelp = "Directory in which to write additional info which does not fit in Cytoscape, e.g. correlation matrices and the log file. Defaults to the location of the cytoscape session + the network name + the current date and time.";
	
	private File outputDir;

	// These are sets to remove duplicates
	private Set<File> expressionMatrixFiles;
	private Set<String> cleanedBaits;

	// volatile allows multiple threads to access it
	private volatile boolean cancelled = false;
	
	// The thread that called run(), if any
	private volatile Thread thread;
	
	public CreateNetworkTask(Context context) {
		super();
		this.context = context;
		baitsSource.setSelectedValue("File");
	}
	
	@ProvidesTitle
	public String getTitle() {
		return "CoExpNetViz (" + context.APP_VERSION + ") parameters";
	}
	
	@Tunable(description = "Output directory", tooltip = outputDirHelp, longDescription = outputDirHelp, listenForChange="networkName")
	public File getOutputDir() {
		/*
		 * There's no need to set outputDir here in response to networkName getting updated.
		 * Cytoscape will call the setter eventually for us with the right value.
		 * A getter with side effects is probably bad design anyway.
		 * 
		 * All listenForChange does is call this getter again when networkName changes.
		 * 
		 * Session manager returns a path, not just the file name. I don't think it ever
		 * returns empty str, but not a huge problem if it does (it would default to CWD).
		 */
		String sessionPath = context.getSessionManager().getCurrentSessionFileName();
		
		if (sessionPath == null) {
			return outputDir;
		}
		
		File dir = new File(sessionPath).getAbsoluteFile().getParentFile();
		if (networkName != null && !networkName.isEmpty()) {
			String name = networkName + "_" + formatCurrentDateTime();
			dir = dir.toPath().resolve(name).toFile();
		}
		return dir;
	}
	
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}
	
	private String formatCurrentDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_n");
		return formatter.format(now);
	}
	
	@Override
	public ValidationState getValidationState(Appendable msg) {
		try {
			cleanNetworkName();
			
			if (baitsSource.getSelectedValue().equals("Inline")) {
				cleanBaits();
			} else {
				baitsFile = cleanInputFile("Baits file", baitsFile, true);
			}
			
			cleanExpressionMatrices();
			
			geneFamiliesFile = cleanInputFile("Gene families", geneFamiliesFile, false);
			
			// BoundedDouble already checks bounds and ensures a valid is entered
			// (despite Tunable(required=false) being the default)
			if (lowerPercentile.getValue() > upperPercentile.getValue()) {
				throw new InputError("Lower percentile must be less or equal to upper percentile.");
			}
			
			cleanOutputDir();
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
	
	private void cleanNetworkName() throws InputError {
		// Note: CyNetwork names don't have to be unique
		if (networkName == null || networkName.isBlank()) {
			throw new InputError("Network name is required.");
		}
		networkName = networkName.trim();
	}
	
	private void cleanBaits() throws InputError {
		String[] baits = baitsText.split("[,;]");
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
		expressionMatrixFiles = new HashSet<File>();
		
		// Add files from single value fields
		File[] singleFieldFiles = {
			expressionMatrix1,
			expressionMatrix2,
			expressionMatrix3,
			expressionMatrix4,
			expressionMatrix5
		};
		for (File file : singleFieldFiles) {
			if (file != null) {
				expressionMatrixFiles.add(cleanInputFile(file.toString(), file, true));
			}
		}
		
		// Add from multivalue field
		String[] paths = expressionMatrices.split("[,;]");
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
		
		// Make sure it's not an excel file.
		// Alternatively we could check it looks like a CSV file but I fear that would be too restrictive.
		for (File file : expressionMatrixFiles) {
			try {
				String mimeType = Files.probeContentType(file.toPath());
				if (mimeType.contains("excel") || mimeType.contains("xls") || mimeType.contains("spreadsheet")) {
					throw new InputError(String.format(
						"Expression matrix seems to be a spreadsheet file, which is not supported.\n" +
						"Please open the spreadsheet and export it to a CSV file.\n" +
						"For more info on supported input formats, see https://github.com/CoExpNetViz/CoExpNetViz/wiki/Input-formats\n" +
						"Expression matrix: %s\nDetected mime type: %s",
						file.toString(),
						mimeType
					));
				}
			} catch (IOException e) {
				// Only raised when the file type is unknown, we're optimistic and assume it's
				// a valid CSV file then.
			}
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
				return null;
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
	
	private void cleanOutputDir() throws InputError {
		if (outputDir == null) {
			/* Try to provide default value for CLI
			 * 
			 * A CLI user does not get to see the default as this code only runs after
			 * they run the command, not when they ask for help on the command. But at
			 * least this way we do provide a default on CLI as well.
			 * 
			 * The CLI never calls getOutputDir or setOutputDir when the outputDir arg
			 * is not provided. When the arg isn't provided, it is null, so try to set
			 * a default value for it. It can also be null when GUI, but in that case
			 * getOutputDir will just return null again, so this is a noop in GUI context.
			 */
			outputDir = getOutputDir();
		}
		if (outputDir == null) {
			throw new InputError("Output directory is required.");
		} else if (outputDir.exists()) {
			if (!outputDir.isDirectory()) {
				throw new InputError("Output directory is not a directory.");
			} else if (!outputDir.canWrite()) {
				throw new InputError("Output directory is not writeable.");
			}
		} else {
			// Backend expects the dir to exist
			outputDir.mkdirs();
		}
		outputDir = outputDir.getAbsoluteFile();
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
		
		/* This title is shown in the task log and should make it easy to identify
		 * the task and roughly which params though I don't go as far as including
		 * those. 
		 */
		monitor.setTitle("Create co-expression network '" + networkName + "'");
		
		try {
			JsonNode response = callBackend(monitor);
			
			monitor.setStatusMessage("Creating network");
			monitor.setProgress(0.6);
			createNetwork();
			Map<Long, CyNode> nodes = createNodes(response);
			createEdges(response, nodes);
			CyNetworkView networkView = createNetworkView();
			
			// Finally, reveal the created network to the user in the GUI. Doing this earlier risks
			// them editing it while we're still working on it.
			context.getNetworkManager().addNetwork(network, true);
			context.getNetworkViewManager().addNetworkView(networkView);
			
			// Apply our layout to the network in a next task
			insertTasksAfterCurrentTask(context.getLayoutAlgorithm().createTaskIterator(
				networkView, context.getLayoutAlgorithm().createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null
			));
		} catch (InterruptedException e) {
			/* Happens when cancelled, just ignore it and stop running.
			 * If we let it bubble up, Cytoscape will show an error.
			 */
		} catch (UserException e) {
			// Already formatted nicely
			throw e;
		} catch (Exception e) {
			/* Cytoscape only shows e.getMessage() but for unexpected errors like
			 * these we want to provide full details so they can open an issue for
			 * it. Also helpful to us when debugging.
			 */
			throw new UserException("Internal CoExpNetViz error: " + e.getMessage() + "\n" + Throwables.getStackTraceAsString(e), e);
		}
	}

	private JsonNode callBackend(TaskMonitor monitor) throws UserException, InterruptedException {
		monitor.setStatusMessage("Checking conda --version");
		checkCondaVersion(monitor);
		
		monitor.setStatusMessage("Writing system info");
		writeSystemInfo(monitor);
		
		monitor.setStatusMessage("Exporting conda env");
		exportCondaEnv(monitor);

		monitor.setStatusMessage("Updating '" + CondaCall.CONDA_ENV + "' conda env");
		updateCondaEnv(monitor);
		
		monitor.setStatusMessage("Running python backend");
		return condaCallBackend(monitor);
	}
	
	private void checkCondaVersion(TaskMonitor monitor) throws UserException, InterruptedException {
		int[] condaVersion = getCondaVersion(monitor);
		int[] minVersion = {4, 9, 0};
		if (Arrays.compare(condaVersion, minVersion) < 0) {
			throw new UserException("Please update conda to at least v4.9.0. E.g. execute `conda install conda` (in a conda shell, without the quotes).");
		}
	}

	private int[] getCondaVersion(TaskMonitor monitor) throws UserException, InterruptedException {
		TextReaderThread stdoutThread = new TextReaderThread("stdout from conda");
		String[] args = {"--version"};
		new CondaCall(context, monitor, args, stdoutThread).run();
		String output = stdoutThread.getOutput();
		return parseCondaVersionOutput(monitor, output);
	}

	private int[] parseCondaVersionOutput(TaskMonitor monitor, String output) throws UserException {
		Pattern pattern = Pattern.compile("conda ([0-9]+).([0-9]+).([0-9]+)");
		Matcher matcher = pattern.matcher(output);
		if (!matcher.find()) {
			monitor.showMessage(Level.ERROR, "Could not parse conda --version output: " + output);
			throw new UserException("Failed to parse conda --version output, please send a bug report.");
		}
		int[] versionParts = {
			Integer.parseInt(matcher.group(1)),
			Integer.parseInt(matcher.group(2)),
			Integer.parseInt(matcher.group(3))
		};
		return versionParts;
	}
	
	private void writeSystemInfo(TaskMonitor monitor) {
		FileWriter writer;
		try {
			writer = new FileWriter(outputDir.toPath().resolve("system_info.txt").toFile());
			writer.write(String.format(
				"os.name: %s\nos.version: %s\njava.version: %s\ncytoscape version: %s\ncoexpnetviz app version: %s\n",
				System.getProperty("os.name"),
				System.getProperty("os.version"),
				System.getProperty("java.version"),
				context.getCyVersion().getVersion(),
				context.APP_VERSION
			));
			writer.close();
		} catch (IOException e) {
			// System info is only used for bug reports, ignore if it fails to write the file
			monitor.showMessage(
				Level.WARN,
				"Failed to write system info to file, but you don't need it unless you are reporting a bug: " + e.toString()
			);
		}
	}
	
	private void exportCondaEnv(TaskMonitor monitor) throws UserException, InterruptedException {
		VoidReaderThread stdoutThread = new VoidReaderThread("stdout from conda");
		String envFile = outputDir.toPath().resolve("conda_env.yaml").toString();
		String[] args = {
			"env",
			"export",
			"-n",
			CondaCall.CONDA_ENV,
			"-f",
			envFile
		};
		new CondaCall(context, monitor, args, stdoutThread).run();
	}

	private void updateCondaEnv(TaskMonitor monitor) throws UserException, InterruptedException {
		boolean update = condaEnvExists(monitor);
		List<String> args = new ArrayList<String>();
		if (update) {
			if (context.isCondaEnvUpToDate()) {
				String msg = "Conda update skipped. Already updated once since cytoscape started (or since app was restarted).";
				monitor.showMessage(Level.INFO, msg);
				return;
			}
			
			/* `update --update-all` does not support the `coexpnetviz=={major}.*` bit.
			 * If you were to just `update --update-all` it would update coexpnetviz to
			 * a higher major version even if available. `install --update-all` does
			 * support `coexpnetviz==...` and so won't update it too far, but does update
			 * everything in the env including coexpnetviz itself.
			 */
			args.add("install");
			args.add("--update-all");
		} else {
			args.add("create");
		}
			
		args.add("-n");
		args.add(CondaCall.CONDA_ENV);
		args.add("--channel");
		args.add("anaconda");
		args.add("--channel");
		args.add("timdiels");
		args.add("--channel");
		args.add("coexpnetviz");
		args.add("python==3.8.*");
		args.add(String.format("coexpnetviz==%d.*", CondaCall.BACKEND_MAJOR_VERSION));
			
		TextReaderThread stdoutThread = new TextReaderThread("stdout from conda");
		try {
			new CondaCall(context, monitor, args, stdoutThread).run();
		} catch (UserException e) {
			if (!update) {
				throw e;
			}
			
			/* Just warn on update failure; we can still run the backend without updating.
			 * E.g. user might be offline.
			 */
			Context.showTaskMessage(monitor, Level.WARN, e.getMessage());
		}
		
		context.setCondaEnvUpToDate();
	}
	
	private boolean condaEnvExists(TaskMonitor monitor) throws UserException, InterruptedException {
		JsonParserThread stdoutThread = new JsonParserThread("json env list from conda", context.getJsonMapper());
		String[] args = new String[] {"env", "list", "--json"};
		new CondaCall(context, monitor, args, stdoutThread).run();
		JsonNode output = stdoutThread.getOutput();
		JsonNode envs = output.get("envs");
		assert envs.isArray();
		for (JsonNode env : envs) {
			assert env.isTextual();
			// Path('/foo/bar').endsWith('r') would be false, it only returns true when it matches
			// the full file name
			System.out.println(env.textValue());
			if (Path.of(env.textValue()).endsWith(CondaCall.CONDA_ENV)) {
				return true;
			}
		}
		return false;
	}
	
	private JsonNode condaCallBackend(TaskMonitor monitor) throws UserException, InterruptedException {
		File jsonInputFile = outputDir.toPath().resolve("input.json").toFile();
		writeJsonInput(createJsonInput(), jsonInputFile);
		JsonParserThread stdoutThread = new JsonParserThread("json response from backend", context.getJsonMapper());
		String[] args = {
			"run",
			"-n",
			CondaCall.CONDA_ENV,
			"--no-capture-output",
			"coexpnetviz",
			jsonInputFile.toString()
		};
		new CondaCall(context, monitor, args, stdoutThread).run();
		
		// Mention the log file, if any
		Path logFile = outputDir.toPath().resolve("coexpnetviz.log");
		if (Files.exists(logFile)) {
			Context.showTaskMessage(monitor, Level.INFO, "Backend log file: " + logFile);
		}
		
		return stdoutThread.getOutput();
	}
	
	/*
	 * Formulate json for a call to coexpnetviz-python
	 */
	private ObjectNode createJsonInput() {
		ObjectNode root = context.getJsonMapper().createObjectNode();
		
		if (baitsSource.getSelectedValue().equals("Inline")) {
			ArrayNode baitsArray = root.putArray("baits");
			for (String bait : cleanedBaits) {
				baitsArray.add(bait);
			}
		} else {
			root.put("baits", baitsFile.toString());
		}
		
		ArrayNode matrixArray = root.putArray("expression_matrices");
		for (File matrix : expressionMatrixFiles) {
			matrixArray.add(matrix.toString());
		}
		
		if (geneFamiliesFile != null) {
			root.put("gene_families", geneFamiliesFile.toString());
		}
		
		root.put("lower_percentile", lowerPercentile.getValue());
		root.put("upper_percentile", upperPercentile.getValue());
		root.put("output_dir", outputDir.toString());
		
		return root;
	}
	
	private void writeJsonInput(ObjectNode json, File jsonFile) throws UserException {
		try {
			FileWriter writer = new FileWriter(jsonFile);
			context.getJsonMapper().writeValue(writer, json);
			writer.close();
		} catch (IOException e) {
			String msg = String.format("Failed to write json input to %s: %s", jsonFile.toString(), e.toString());
			throw new UserException(msg, e);
		}
	}
	
	private void createNetwork() {
		network = context.getNetworkFactory().createNetwork();
		network.getRow(network).set(CyNetwork.NAME, networkName);
	}
	
	private Map<Long, CyNode> createNodes(JsonNode response) {
		// Define extra node columns. false means the user may delete the columns.
		CyTable nodeTable = network.getDefaultNodeTable();
		nodeTable.createColumn(Context.NAMESPACE, "type", String.class, false);
		nodeTable.createListColumn(Context.NAMESPACE, "genes", String.class, false);
		nodeTable.createColumn(Context.NAMESPACE, "family", String.class, false);
		nodeTable.createColumn(Context.NAMESPACE, "colour", String.class, false);
		nodeTable.createColumn(Context.NAMESPACE, "partition_id", Long.class, false);
		
		Map<Long, CyNode> nodes = new HashMap<>();
		for (JsonNode rawNode : response.get("nodes")) {
			CyNode node = network.addNode();
			CyRow nodeAttrs = network.getRow(node);
			JsonNode nodeAttr;
			
			nodeAttr = rawNode.get("id");
			assert nodeAttr.isIntegralNumber();
			nodes.put(nodeAttr.asLong(), node);
			
			nodeAttr = rawNode.get("label");
			assert nodeAttr.isTextual();
			nodeAttrs.set(CyNetwork.NAME, nodeAttr.textValue());
			
			nodeAttr = rawNode.get("type");
			assert nodeAttr.isTextual();
			nodeAttrs.set(Context.NAMESPACE, "type", nodeAttr.textValue());
			
			/* Apparently you could use mapper.convertValue to convert to List<String>
			 * but I couldn't get that to compile.
			 */
			nodeAttr = rawNode.get("genes");
			assert nodeAttr.isArray();
			List<String> genes = new ArrayList<String>();
			for (JsonNode gene : nodeAttr) {
				assert gene.isTextual();
				genes.add(gene.textValue());
			}
			nodeAttrs.set(Context.NAMESPACE, "genes", genes);
			
			nodeAttr = rawNode.get("family");
			assert nodeAttr.isTextual();
			nodeAttrs.set(Context.NAMESPACE, "family", nodeAttr.textValue());
			
			nodeAttr = rawNode.get("colour");
			assert nodeAttr.isTextual();
			nodeAttrs.set(Context.NAMESPACE, "colour", nodeAttr.textValue());
			
			nodeAttr = rawNode.get("partition_id");
			assert nodeAttr.isIntegralNumber();
			nodeAttrs.set(Context.NAMESPACE, "partition_id", nodeAttr.asLong());
		}
		
		return nodes;
	}

	private void createEdges(JsonNode response, Map<Long, CyNode> nodes) {
		CyTable edgeTable = network.getDefaultEdgeTable();
		edgeTable.createColumn(Context.NAMESPACE, "max_correlation", Double.class, false);

		for (JsonNode jsonEdge : response.get("homology_edges")) {
			CyNode node1 = getNode(nodes, jsonEdge, "bait_node1");
			CyNode node2 = getNode(nodes, jsonEdge, "bait_node2");
			boolean isDirected = false;
			CyEdge edge = network.addEdge(node1, node2, isDirected);
			CyRow edgeAttrs = network.getRow(edge);
			
			edgeAttrs.set("interaction", "hom");
		}
		
		for (JsonNode jsonEdge : response.get("cor_edges")) {
			CyNode baitNode = getNode(nodes, jsonEdge, "bait_node");
			CyNode node = getNode(nodes, jsonEdge, "node");
			boolean isDirected = false;
			CyEdge edge = network.addEdge(baitNode, node, isDirected);
			CyRow edgeAttrs = network.getRow(edge);
			
			edgeAttrs.set("interaction", "cor");
			
			JsonNode edgeAttr = jsonEdge.get("max_correlation");
			assert edgeAttr.isNumber();
			edgeAttrs.set(Context.NAMESPACE, "max_correlation", edgeAttr.asDouble());
		}
	}

	private CyNode getNode(Map<Long, CyNode> nodes, JsonNode json, String nodeIdAttr) {
		JsonNode jsonNodeId = json.get(nodeIdAttr);
		assert jsonNodeId.isIntegralNumber();
		long nodeId = jsonNodeId.asLong();
		return nodes.get(nodeId);
	}
	
	/*
	 * Create network view and style it
	 * 
	 * We do this after we are done with the CyNetwork, its nodes and edges. Otherwise we would
	 * have to call CyEventHelper to flushPayloadEvents() to the view.
	 */
	private CyNetworkView createNetworkView() {
		/* User can't view the network before it has a view, so create one and add it to the GUI.
		 * We add it to the GUI right away to prevent the user from clicking "Create view" while
		 * we're working on ours.
		 */
		CyNetworkView networkView = context.getNetworkViewFactory().createNetworkView(network);

		/* Style the view
		 * 
		 * If the style already exists, it might have been created by and older coexpnetviz version,
		 * so once a version has set a visual property, all future versions must keep setting it (e.g.
		 * to its default). Otherwise you may end up with an unintended mix of old and new properties.
		 * E.g. v1 sets x, v2 only sets y, you then end up with x=v1_value and y=v2_value instead of
		 * x=default. Or you might try using style.removeVisualMappingFunction, or style.getDefaultValue().
		 */
		VisualStyle style = getOrCreateStyle();
		VisualMappingFunctionFactory passthrough = context.getPassthroughMappingFactory();
		VisualMappingFunctionFactory discrete = context.getDiscreteMappingFactory();
		VisualMappingFunctionFactory continuous = context.getContinuousMappingFactory();
		
		style.addVisualMappingFunction(passthrough.createVisualMappingFunction(
			CyNetwork.NAME, String.class, BasicVisualLexicon.NODE_LABEL
		));
		
		style.addVisualMappingFunction(passthrough.createVisualMappingFunction(
			Context.NAMESPACE + "::colour", String.class, BasicVisualLexicon.NODE_FILL_COLOR
		));
		
		// Cor edge colour: red (-1) -- black (0) -- green (1)
		ContinuousMapping<Double, Paint> edgeColourMapping = (ContinuousMapping<Double, Paint>) continuous.createVisualMappingFunction(
			Context.NAMESPACE + "::max_correlation", Double.class, BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT
		);
		edgeColourMapping.addPoint(-1.0, new BoundaryRangeValues<>(Color.RED, Color.RED, Color.RED));
		edgeColourMapping.addPoint(0.0, new BoundaryRangeValues<>(Color.BLACK, Color.BLACK, Color.BLACK));
		edgeColourMapping.addPoint(1.0, new BoundaryRangeValues<>(Color.GREEN, Color.GREEN, Color.GREEN));
		style.addVisualMappingFunction(edgeColourMapping);
		
		// Hom edge: dotted line
		DiscreteMapping<String, LineType> lineTypeMapping = (DiscreteMapping<String, LineType>) discrete.createVisualMappingFunction(
				"interaction", String.class, BasicVisualLexicon.EDGE_LINE_TYPE
		);
		lineTypeMapping.putMapValue("hom", LineTypeVisualProperty.DOT);
		lineTypeMapping.putMapValue("cor", LineTypeVisualProperty.SOLID);
		style.addVisualMappingFunction(lineTypeMapping);
		
		style.apply(networkView);
		return networkView;
	}
	
	/* Get/create our stylesheet and ensure it's listed in the GUI (in case user made changes
	 * and wants to restore the original). If we simply create a new one each time, we end
	 * up spamming the style list with coexpnetviz_0, _1, ... Deleting the existing style
	 * would probably reset the style of previous networks, so we use the existing one if any.
	 */
	private VisualStyle getOrCreateStyle() {
		for (VisualStyle style : context.getVisualMappingManager().getAllVisualStyles()) {
			if (style.getTitle() == Context.APP_NAME) {
				return style;
			}
		}
		
		VisualStyle style = context.getVisualStyleFactory().createVisualStyle(Context.APP_NAME);
		context.getVisualMappingManager().addVisualStyle(style);
		return style;
	}

}
