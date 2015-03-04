package be.samey.model;

import java.nio.file.Path;
import org.cytoscape.model.subnetwork.CyRootNetwork;

/**
 *
 * @author sam
 */
public class CoreStatus {

    //some general information that is useful for many parts of the app
    //all subnetworks can be retrieved with rootnetwork.getSubNetworkList()
    private CyRootNetwork cyRootNetwork;

    //keeps track of how many networks have been created since the last root
    // network was created
    private int networkCount = 0;

    //which baits to send to the server
    private String baits;

    //which files to use to upload to the server
    private Path[] filespaths = new Path[Model.MAX_SPECIES_COUNT];

    //name for matrices to use to upload to the server
    private String[] names = new String[Model.MAX_SPECIES_COUNT];

    //output directory
    private Path outPath;

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
        return filespaths;
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
}
