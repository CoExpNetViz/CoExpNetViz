package be.samey.model;

import be.samey.cynetw.CevNetworkCreator;
import java.nio.file.Path;
import org.cytoscape.model.subnetwork.CyRootNetwork;

/**
 *
 * @author sam
 */
public class CoreStatus {

    private Model model;

    private CevNetworkCreator cnc;

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

    /**
     * Starts running the analysis
     */
    public void runAnalysis() {
        if (cnc == null) {
            cnc = new CevNetworkCreator(model);
        }
        //TODO: change this method so that it connects to the server
        cnc.runAnalysis();
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
}
