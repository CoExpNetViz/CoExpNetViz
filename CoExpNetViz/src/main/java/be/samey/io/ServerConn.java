package be.samey.io;

import be.samey.model.Model;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import static java.nio.file.StandardCopyOption.*;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author sam
 */
public class ServerConn {

    private Model model;

    public ServerConn(Model model) {
        this.model = model;
    }

    public void connect(TaskMonitor tm) throws InterruptedException, IOException {

        /*----------------------------------------------------------------------
         1) create output directory
         */
        //if the user clicked the checkbox to save the output, then the archive
        //downloaded from the server is saved in the directory specified by the
        //user. If the user does not want to save the output, then the archive
        //is downloaded to a temp folder.
        Path outPath;
        if (model.getCoreStatus().getOutPath() == null) {
            outPath = Files.createTempDirectory("Cev_archive");
        } else {
            outPath = model.getCoreStatus().getOutPath();
        }
        Path archivePath = outPath.resolve("Cev.tgz");

        /*----------------------------------------------------------------------
         2) Upload user files and settings
         */
        tm.setStatusMessage("Uploading your data");
        tm.setProgress(0.1);
        String url = Model.URL;
        //TODO: get info from coreStatus
        // ...

        //TODO: build useragent
        // ...
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //TODO: upload files
        Thread.sleep(1000); //simulate runtime on server

        /*----------------------------------------------------------------------
         3) Download response to output directory
         */
        tm.setStatusMessage("Dowloading network files");
        tm.setProgress(0.3);
        //TODO: download archive to outPath
        //now replaced with local copy so I can test the archiver libraries
        Files.copy(Paths.get("/home/sam/Documents/uma1_s2-mp2-data/CexpNetViz_web-interface/out/network.tgz"), archivePath, REPLACE_EXISTING);

        //TODO: close useragent

        /*----------------------------------------------------------------------
         4) Unpack files to temp dir
         */
        //create a temp folder to store the network files
        Path unpackPath = Files.createTempDirectory("Cev_netw");
        //unpack the network files
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        File archive = archivePath.toFile();
        ArchiveStream stream = archiver.stream(archive);
        ArchiveEntry entry;

        Path sifPath = null;
        Path noaPath = null;
        Path edaPath = null;
        Path logPath = null;
        File netwFile;
        while ((entry = stream.getNextEntry()) != null) {
            netwFile = entry.extract(unpackPath.toFile());
            if (netwFile.toString().endsWith(".sif")) {
                sifPath = netwFile.toPath();
            }
            if (netwFile.toString().endsWith(".node.attr")) {
                noaPath = netwFile.toPath();
            }
            if (netwFile.toString().endsWith(".edge.attr")) {
                edaPath = netwFile.toPath();
            }
            if (netwFile.toString().endsWith("_log")) {
                logPath = netwFile.toPath();
            }
        }
        stream.close();

        /*----------------------------------------------------------------------
         5) update corestatus with network paths
         */
        //TODO: sanity checks
        model.getCoreStatus().setSifPath(sifPath);
        model.getCoreStatus().setNoaPath(noaPath);
        model.getCoreStatus().setEdaPath(edaPath);
        model.getCoreStatus().setLogPath(logPath);

    }
}
