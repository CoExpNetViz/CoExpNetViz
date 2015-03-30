package be.samey.model;

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

import be.samey.cynetw.CevNetworkBuilder;
import be.samey.cynetw.CevTaskObserver;
import be.samey.cynetw.CreateNetworkTask;
import be.samey.cynetw.RunAnalysisTask;
import java.nio.file.Path;
import java.util.Arrays;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

/**
 *
 * @author sam
 */
public class CoreStatus {

    private final Model model;

    private CevNetworkBuilder cnb;

    //Which column to use to group nodes
    public static final int GROUP_COLUMN = 0;
    //which column contains the species of the baits
    public static final int SPECIES_COLUMN = 4;
    //what value indicates a node is a bait in the SPECIES_COLUMN
    public static final String BAIT_GROUP = "";
    //the name to use for the new layout created by this app
    public static final String COMP_LAYOUT_NAME = "cev-layout";
    public static final String HUMAN_LAYOUT_NAME = "Cev target-bait Layout";

    public CoreStatus(Model model) {
        this.model = model;
    }

    //some general information that is useful for many parts of the app
    //all subnetworks can be retrieved with rootnetwork.getSubNetworkList()
    private CyRootNetwork cyRootNetwork;

    //keeps track of how many networks have been created since the last root
    // network was created
    private int networkCount = 0;

    //which baits to send to the server
    private String baits;

    //which files to use to upload to the server
    private Path[] filePaths = new Path[Model.MAX_SPECIES_COUNT];

    //name for matrices to use to upload to the server
    private String[] names = new String[Model.MAX_SPECIES_COUNT];

    //cutoffs
    private double pCutoff;
    private double nCutoff;

    //output directory
    private Path outPath;

    //path to log file
    private Path logPath;

    //paths to network files for last created network
    //these are set by runAnalysistask and then used by CreateNetworkTask
    private Path sifPath;
    private Path noaPath;
    private Path edaPath;
    private Path vizPath;

    //Node table for last created network
    //this is set by CreateNetworkTask and used by ShowNetworkTask
    private CyTable lastNoaTable;

    //Network view for last created network
    //this is set by CreateNetworkTask and used by ShowNetworkTask
    private CyNetworkView lastCnv;

    /**
     * Starts running the analysis, the model and coreStatus should remain
     * unaltered during execution of this method, if not concurrency problems
     * might arise.
     */
    public void runAnalysis() {
        //for debugging, prints the baits, files, and cutoffs specified by the user
        //in the finished app this info is sent to the server
        System.out.println("---Debug output---");
        System.out.println("baits: " + getBaits());
        System.out.println("names: " + Arrays.toString(getNames()));
        System.out.println("files: " + Arrays.toString(getFilePaths()));
        System.out.println("neg c: " + getNCutoff());
        System.out.println("pos c: " + getPCutoff());
        System.out.println("out d: " + getOutPath());

        if (cnb == null) {
            cnb = new CevNetworkBuilder(model);
        }
        TaskIterator ti = new TaskIterator();
        ti.append(new RunAnalysisTask(model));
        ti.append(new CreateNetworkTask(model));

        //now execute them, execute() forks a new thread and returns immediatly
        model.getServices().getTaskManager().execute(ti, new CevTaskObserver(model));

        /*
         * Three tasks have to be executed in order to create the final network
         * with the correct Style and Layout. 1) Running the analysis, 2)
         * creating the network view and applying a Style. 3) Apply the layout.
         * The first two are executed here. The third task inherits from
         * AbstractLayoutTask, an abstract class defined in the cytoscape layout
         * implementation. The constructor of the task requires a CyNetworkView.
         * For this reason, the third task can only be initialized after
         * completion of the second task. This makes it impossible to append the
         * third task to the same taskiterator as the first two tasks. To work
         * around this problem, the execution of the third task is done by the
         * CevTaskObserver, this observer will start the third task after
         * completion of the first two tasks in the above taskIterator.
         */
    }

    public CyRootNetwork getCyRootNetwork() {
        return cyRootNetwork;
    }

    public void setCyRootNetwork(CyRootNetwork cyRootNetwork) {
        this.cyRootNetwork = cyRootNetwork;
    }

    public int getNetworkCount() {
        return networkCount;
    }

    public void resetNetworkCount() {
        networkCount = 0;
    }

    public void incrementNetworkCount() {
        networkCount++;
    }

    public String getBaits() {
        return baits;
    }

    public void setBaits(String baits) {
        this.baits = baits;
    }

    public Path[] getFilePaths() {
        return filePaths;
    }

    public String[] getNames() {
        return names;
    }

    public Path getOutPath() {
        return outPath;
    }

    public void setOutPath(Path outPath) {
        this.outPath = outPath;
    }

    public double getPCutoff() {
        return pCutoff;
    }

    public void setPCutoff(double pCutoff) {
        this.pCutoff = pCutoff;
    }

    public double getNCutoff() {
        return nCutoff;
    }

    public void setNCutoff(double nCutoff) {
        this.nCutoff = nCutoff;
    }

    public void setLogPath(Path logPath) {
        this.logPath = logPath;
    }

    public Path getLogPath() {
        return logPath;
    }

    public void setSifPath(Path sifPath) {
        this.sifPath = sifPath;
    }

    public Path getSifPath() {
        return sifPath;
    }

    public void setNoaPath(Path noaPath) {
        this.noaPath = noaPath;
    }

    public Path getNoaPath() {
        return noaPath;
    }

    public void setEdaPath(Path edaPath) {
        this.edaPath = edaPath;
    }

    public Path getEdaPath() {
        return edaPath;
    }

    public void setVizPath(Path vizPath) {
        this.vizPath = vizPath;
    }

    public Path getVizPath() {
        return vizPath;
    }

    public void setLastNoaTable(CyTable lastNoaTable) {
        this.lastNoaTable = lastNoaTable;
    }

    public CyTable getLastNoaTable() {
        return lastNoaTable;
    }

    public void setLastCnv(CyNetworkView lastCnv) {
        this.lastCnv = lastCnv;
    }

    public CyNetworkView getLastCnv() {
        return lastCnv;
    }

    public CevNetworkBuilder getCevNetworkBuilder() {
        return cnb;
    }
}
