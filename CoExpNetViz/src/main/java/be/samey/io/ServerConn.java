package be.samey.io;

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

import be.samey.model.Model;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
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
         2) Upload user files and settings, download response
         */
        tm.setStatusMessage("Running analysis on server");
        tm.setProgress(0.1);

        //make multipart entity with user data and settings
        HttpEntity postEntity = makeEntity(model.getCoreStatus().getBaits(),
            model.getCoreStatus().getNames(),
            model.getCoreStatus().getFilePaths(),
            model.getCoreStatus().getPCutoff(),
            model.getCoreStatus().getNCutoff());

        //run the app on the server
        tm.setProgress(-1.0);
        try {
            executeAppOnSever(Model.URL, postEntity, archivePath);
        } catch (Exception ex) {
            //TODO: warn user somehow
            ex.printStackTrace();
        }
        tm.setProgress(6.0);

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
    
    public HttpEntity makeEntity(String baits, String[] names, Path[] filepaths,
        double poscutoff, double negcutoff)
        throws UnsupportedEncodingException {

        //Tim has numbered his form entity names in this order, this could change
        // change in the future
        int[] formNames = new int[]{1, 2, 3, 4, 0};
        
        MultipartEntityBuilder mpeb = MultipartEntityBuilder.create();

        //make the bait part
        StringBody baitspart = new StringBody(baits, ContentType.TEXT_PLAIN);
        mpeb.addPart("baits", baitspart);

        //make the file upload parts
        for (int i = 0; i < 5; i++) {
            if (names[i] != null && filepaths[i] != null) {
                mpeb.addBinaryBody("matrix" + formNames[i], filepaths[i].toFile(), ContentType.TEXT_PLAIN, names[i]);
            } else {
                mpeb.addBinaryBody("matrix" + formNames[i], new byte[0], ContentType.APPLICATION_OCTET_STREAM, "");
            }
        }

        //make the cutoff parts
        StringBody poscpart = new StringBody(Double.toString(poscutoff));
        mpeb.addPart("positive_correlation", poscpart);
        StringBody negcpart = new StringBody(Double.toString(negcutoff));
        mpeb.addPart("negative_correlation", negcpart);
        
        return mpeb.build();
    }
    
    public void executeAppOnSever(String url, HttpEntity entity, Path archivePath) throws Exception {
        
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
        
        System.out.println("executing request " + httppost.getRequestLine());
        CloseableHttpResponse response = null;
        HttpEntity resEntity = null;
        try {
            response = httpclient.execute(httppost);
            System.out.println(response.getStatusLine());
            resEntity = response.getEntity();
            saveResponse(resEntity.getContent(), archivePath);
            EntityUtils.consume(resEntity);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        httpclient.close();
    }
    
    private void saveResponse(final InputStream input, Path outp) throws IOException {
        OutputStream out = Files.newOutputStream(outp);
        try {
            final byte[] buffer = new byte[1024];
            while (true) {
                final int len = input.read(buffer);
                if (len < 0) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        } catch (IOException ex) {
            //TODO: warn user somehow
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }
}
