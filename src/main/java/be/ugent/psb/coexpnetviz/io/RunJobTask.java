package be.ugent.psb.coexpnetviz.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;

import be.ugent.psb.coexpnetviz.CENVContext;
import be.ugent.psb.coexpnetviz.InputError;
import be.ugent.psb.util.Strings;

/**
 * Communication with CoExpNetViz server that handles jobs (in reality, this may be an intermediate)
 */
public class RunJobTask implements Task, TunableValidator {

	private CENVContext context;
	private JobServer jobServer;
	private JobDescription jobDescription;
	
	/* Tunables are only picked up when public field/property in a public class.
	 * Tunable supports File, but not Path, so we use File here. A File with
	 * input=True will browse to a file for reading, else for writing (e.g.
	 * the latter will ask to confirm to overwrite a file); the former still
	 * lets you open files which do not exist though. Tunable(tooltip) only shows
	 * up in the GUI, Tunable(longDescription) only shows up on the CLI.
	 * Tunable(exampleStringValue) and Tunable(required) do not seem to do anything
	 * in cytoscape 3.8.2.
	 */
	
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
	
	private List<File> expressionMatrixFiles;
	
	public RunJobTask(CENVContext context) {
		super();
		this.context = context;
		baitGroupSource.setSelectedValue("File");
	}
	
	@Override
	public ValidationState getValidationState(Appendable msg) {
		try {
			if (baitGroupSource.getSelectedValue() == "Inline") {
				baitGroupText = cleanRequiredString("Bait names", baitGroupText);
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
	
	private void cleanExpressionMatrices() throws InputError {
		String[] paths = expressionMatrices.split("[,;]");
		expressionMatrixFiles = new ArrayList<File>();
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
	
	private String cleanRequiredString(String name, String value) throws InputError {
		if (Strings.isNullOrBlank(value)) {
			throw new InputError(name + " is required.");
		}
		return value.trim();
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
