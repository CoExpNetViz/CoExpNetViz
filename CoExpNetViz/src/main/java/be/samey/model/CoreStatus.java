package be.samey.model;

import be.samey.cynetw.CevNetworkBuilder;
import be.samey.cynetw.CreateNetworkTask;
import be.samey.cynetw.RunAnalysisTask;
import be.samey.io.ServerConn;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Observable;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

/**
 *
 * @author sam
 */
public class CoreStatus extends Observable {

    private Model model;

    private CevNetworkBuilder cnb;

    //Which column to use to group nodes
    public static final int GROUP_COLUMN = 0;

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

    public void notifyNetworkCreated() {
        setChanged();
        notifyObservers();
    }

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
        model.getServices().getTaskManager().execute(ti);

        /*
         * After completion of the two tasks above, the layout can be applied.
         * Creating the layout task can only happen after completion of the two
         * above tasks. This is because the view of the network is needed to
         * create the layout task. Therefore the layout task can not be added to
         * the above taskiterator. It is necessary that the layout task is
         * created after completion of all tasks in the above taskiterator. To
         * solve this, CreateNetworkTask triggers a notification of the
         * coreStatus. This will trigger an update in the CevNetworkBuilder,
         * this update will perform the layout task. This strange routing is a
         * workaround to prevent triggering a task from withing another task.
         * I should try to find a better solution for this problem.
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
