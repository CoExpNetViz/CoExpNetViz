package be.ugent.psb.coexpnetviz.io;

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
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.yaml.snakeyaml.Yaml;

import be.ugent.psb.coexpnetviz.CENVApplication;
import be.ugent.psb.coexpnetviz.gui.CENVModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Manages a single request to the CoExpNetViz server
 */
public class JobServer {

	private static final String URL = "http://bioinformatics.psb.ugent.be/webtools/coexpr/";
	
    private final CENVModel cyModel; // TODO get rid of this dependency

    private HttpPost httpPost;

    public JobServer(CENVApplication cyAppManager) {
        this.cyModel = cyAppManager.getCyModel();
    }

    public void abort() {
        httpPost.abort();
    }

    /**
     * Submit run and store the unpacked result in the local file system, synchronously
     * 
     * @throws InterruptedException
     * @throws IOException
     * @throws JobServerException
     * @return Path to unpacked result  
     */
    public Path runJob(JobDescription job) throws InterruptedException, IOException, JobServerException {
        // Post request and retrieve result
        Path downloadDirectory = Files.createTempDirectory("cenv_archive");
        String archiveName = cyModel.getTitle();
        Path archivePath = downloadDirectory.resolve(archiveName + "_" + CENVApplication.getTimeStamp() + ".tgz");
        
        HttpEntity postEntity = makeEntity(job);
        executeAppOnSever(postEntity, archivePath);

        // Decide where to unpack
        Path unpackPath;
        if (cyModel.getSaveFilePath() == null) {
        	unpackPath = Files.createTempDirectory("cenv_network");
        }
        else {
        	unpackPath = cyModel.getSaveFilePath();
        }

        // Unpack the archive
        Archiver archiver = ArchiverFactory.createArchiver(archivePath.toFile());
        archiver.extract(archivePath.toFile(), unpackPath.toFile());

        return unpackPath;
    }

    private HttpEntity makeEntity(JobDescription job)
        throws UnsupportedEncodingException {

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

        // Action to request of server
        entityBuilder.addTextBody("__controller", "api");
        entityBuilder.addTextBody("__action", "execute_job");

        // Baits
        if (job.isSendBaitsAsFile()) {
        	entityBuilder.addBinaryBody("baits_file", job.getBaitsFilePath().toFile(), 
        			ContentType.TEXT_PLAIN, job.getBaitsFilePath().getFileName().toString());
        }
        else {
        	entityBuilder.addTextBody("baits", job.getBaits());
        }
        
        // Cutoffs
        entityBuilder.addTextBody("positive_correlation", Double.toString(job.getPositiveCutoff()));
        entityBuilder.addTextBody("negative_correlation", Double.toString(job.getNegativeCutoff()));

        // Expression matrices
        for (Map.Entry<String, Path> entry : job.getExpressionMatrices().entrySet()) {
            entityBuilder.addBinaryBody("matrix[]", entry.getValue().toFile(), ContentType.TEXT_PLAIN, entry.getKey());
        }

        // Ortholog families
        String orthologsSource;
        if (job.getGeneFamilies().isEmpty()) {
        	orthologsSource = "plaza";
        }
        else {
        	orthologsSource = "custom";
        	for (Map.Entry<String, Path> entry : job.getGeneFamilies().entrySet()) {
                entityBuilder.addBinaryBody("orthologs[]", entry.getValue().toFile(), ContentType.TEXT_PLAIN, entry.getKey());
            }
        }
        entityBuilder.addTextBody("orthologs_source", orthologsSource);
        

        return entityBuilder.build();
    }

    private void executeAppOnSever(HttpEntity entity, Path archivePath) throws IOException, JobServerException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        httpPost = new HttpPost(URL);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            
            // Check for http errors
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
            	throw new JobServerException("Server returned http response code: " + statusCode);
            }
            
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
            	throw new JobServerException("Server http response has no body");
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
            	throw new JobServerException((String)errorNode.get("message")); // By default just show the message generated by the server
            }
            else {
            	throw new JobServerException("Server http response's content type unknown: " + contentType);
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
