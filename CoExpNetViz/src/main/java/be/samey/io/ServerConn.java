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
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.commons.io.IOUtils;
import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.yaml.snakeyaml.Yaml;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author sam
 */
public class ServerConn {

    private final CyAppManager cyAppManager;
    private final CyModel cyModel;

    private HttpPost httpPost;

    public ServerConn(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();
    }

    public void stop() throws Exception {
        httpPost.abort();
        throw new Exception("The http request was aborted");
    }

    /**
     * Submit run and store the result in the local file system
     * 
     * @throws InterruptedException
     * @throws IOException
     * @throws ServerException 
     */
    public void connect() throws InterruptedException, IOException, ServerException {

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
            cyModel.getNCutoff(),
            cyModel.getOrthGroupNames(),
            cyModel.getOrthGroupPaths());

        //run the app on the server
        executeAppOnSever(CyModel.URL, postEntity, archivePath);

        /*----------------------------------------------------------------------
         4) Handle response: Unpack files to temp dir; or if return is error response, interpret the error.
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
        double poscutoff, double negcutoff, String[] orthNames, Path[] orthPaths)
        throws UnsupportedEncodingException {

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

        //make hidden form fields, to the server knows to use the api
        entityBuilder.addTextBody("__controller", "api");
        entityBuilder.addTextBody("__action", "execute_job");

        //make the bait part
        entityBuilder.addTextBody("baits", baits);

        //make the species file upload parts
        for (int i = 0; i < CyModel.MAX_SPECIES_COUNT; i++) {
            if (i < names.length && i < filepaths.length) {
                entityBuilder.addBinaryBody("matrix[]", filepaths[i].toFile(), ContentType.TEXT_PLAIN, names[i]);
            }
        }

        //make the cutoff parts
        entityBuilder.addTextBody("positive_correlation", Double.toString(poscutoff));
        entityBuilder.addTextBody("negative_correlation", Double.toString(negcutoff));

        //make the orthgroup file upload parts
        entityBuilder.addTextBody("orthologs_source", "plaza"); // 'custom' otherwise
        for (int i = 0; i < CyModel.MAX_ORTHGROUP_COUNT; i++) {
            if (cyModel.getOrthGroupPaths() != null && i < orthNames.length && i < orthPaths.length) {
                entityBuilder.addBinaryBody("orthologs[]", orthPaths[i].toFile(), ContentType.TEXT_PLAIN, orthNames[i]);
            }
        }

        return entityBuilder.build();
    }

    private void executeAppOnSever(String url, HttpEntity entity, Path archivePath) throws IOException, ServerException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        httpPost = new HttpPost(url);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            
            // Check for http errors
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
            	throw new ServerException("Server returned http response code: " + statusCode);
            }
            
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
            	throw new ServerException("Server http response has no body");
            }
            
            // Save response if tgz, show error otherwise 
            String contentType = responseEntity.getContentType().getValue();
            if ("application/x-gtar".equals(contentType)) {
            	// Archive with the actual result
            	saveResponse(responseEntity.getContent(), archivePath);
            }
            else if ("application/x-yaml".equals(contentType)) {
            	// Error response
            	Yaml yaml = new Yaml();
            	Map<?,?> root = (Map<?,?>) yaml.load(responseEntity.getContent());
            	Map<?,?> errorNode = (Map<?,?>)root.get("error");
            	throw new ServerException((String)errorNode.get("message")); // By default just show the message generated by the server
            }
            else {
            	throw new ServerException("Server http response's content type unknown: " + contentType);
            }
            EntityUtils.consume(responseEntity);
        } finally {
            if (response != null) {
                response.close();
            }
            
            httpClient.close();
        }
    }

    /**
     * Write stream to file
     * @param input
     * @param destination
     * @throws IOException
     */
    private void saveResponse(final InputStream in, Path destination) throws IOException {
    	OutputStream out = null;
        try {
        	out = Files.newOutputStream(destination);
            IOUtils.copy(in, out);
        } finally {
            if (out != null) {
            	out.close();
            }
        }
    }
}
