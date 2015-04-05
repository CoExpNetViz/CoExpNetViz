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
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
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

/**
 *
 * @author sam
 */
public class ServerConn {

    private final CyAppManager cyAppManager;
    private final CyModel cyModel;

    private HttpPost httppost;

    public ServerConn(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();
    }

    public void stop() throws Exception {
        httppost.abort();
        throw new Exception("The http request was aborted");
    }

    public void connect() throws InterruptedException, IOException {

        /*----------------------------------------------------------------------
         1) create output directory
         */
        //if the user clicked the checkbox to save the output, then the archive
        //downloaded from the server is saved in the directory specified by the
        //user. If the user does not want to save the output, then the archive
        //is downloaded to a temp folder.
        Path outPath;
        if (cyModel.getSaveFilePath() == null) {
            outPath = Files.createTempDirectory("Cev_archive");
        } else {
            outPath = cyModel.getSaveFilePath();
        }

        String fileExtension = ".tgz";
        String archiveName = cyModel.getTitle();

        Path archivePath = outPath.resolve(archiveName + fileExtension);
        //prevent overwriting the same file if the user forgot to change the
        //title
        if (Files.exists(archivePath)) {
            archivePath = outPath.resolve(archiveName + "_" + CyAppManager.getTimeStamp() + fileExtension);
        }

        /*----------------------------------------------------------------------
         2) Upload user files and settings, download response
         */
        //make multipart entity with user data and settings
        HttpEntity postEntity = makeEntity(cyModel.getBaits(),
            cyModel.getSpeciesNames(),
            cyModel.getSpeciesPaths(),
            cyModel.getPCutoff(),
            cyModel.getNCutoff());

        //run the app on the server
        executeAppOnSever(CyModel.URL, postEntity, archivePath);

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
        cyModel.setSifPath(sifPath);
        cyModel.setNoaPath(noaPath);
        cyModel.setEdaPath(edaPath);
        cyModel.setLogPath(logPath);

    }

    private HttpEntity makeEntity(String baits, String[] names, Path[] filepaths,
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
        for (int i = 0; i < CyModel.MAX_SPECIES_COUNT; i++) {
            if (i < names.length && i < filepaths.length) {
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

    private void executeAppOnSever(String url, HttpEntity entity, Path archivePath) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        httppost = new HttpPost(url);
        httppost.setEntity(entity);

        CloseableHttpResponse response = null;
        HttpEntity resEntity = null;
        try {
            response = httpclient.execute(httppost);
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
        } finally {
            out.close();
        }
    }
}
