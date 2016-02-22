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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.yaml.snakeyaml.Yaml;

import be.ugent.psb.coexpnetviz.gui.jobinput.BaitGroupSource;
import be.ugent.psb.coexpnetviz.gui.jobinput.CorrelationMethod;
import be.ugent.psb.coexpnetviz.gui.jobinput.GeneFamiliesSource;

/**
 * Allows posting jobs to the CoExpNetViz server sequentially
 */
public class JobServer {

	private static final String URL = "http://bioinformatics.psb.ugent.be/webtools/coexpr/";

    private HttpPost httpPost;

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
    public void runJob(JobDescription job) throws InterruptedException, IOException, JobServerException {
        // Post request and retrieve result
    	Path tempDirectory = null;
    	try {
	    	// Run job on server, which returns a tgz archive
    		tempDirectory =  Files.createTempDirectory("coexpnetviz_result");
    		Path packedResultPath = tempDirectory.resolve("network.tgz");
	        HttpEntity postEntity = makeRequestEntity(job);
	        executeJobOnServer(postEntity, packedResultPath);
	
	        // Unpack the tgz result
	        Archiver archiver = ArchiverFactory.createArchiver(packedResultPath.toFile());
	        archiver.extract(packedResultPath.toFile(), tempDirectory.toFile());
	        
	        // Move to the right spot
	        Files.move(tempDirectory.resolve("network"), job.getResultPath());
    	}
    	finally {
    		if (tempDirectory != null)
    			FileUtils.deleteDirectory(tempDirectory.toFile());
    	}
    }

    private HttpEntity makeRequestEntity(JobDescription job)
        throws UnsupportedEncodingException {

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

        // Action to request of server
        entityBuilder.addTextBody("__controller", "api");
        entityBuilder.addTextBody("__action", "execute_job");
        
        // Baits 
        if (job.getBaitGroupSource() == BaitGroupSource.FILE) {
        	entityBuilder.addBinaryBody("baits_file", job.getBaitGroupPath().toFile(), 
        			ContentType.TEXT_PLAIN, job.getBaitGroupPath().getFileName().toString());
        }
        else if (job.getBaitGroupSource() == BaitGroupSource.TEXT) {
        	entityBuilder.addTextBody("baits", job.getBaitGroupText());
        }
        else {
        	assert false;
        }
        
        // Expression matrices
        for (Path path : job.getExpressionMatrixPaths()) {
            entityBuilder.addBinaryBody("matrix[]", path.toFile(), ContentType.TEXT_PLAIN, path.toString());
        }
        
        // Correlation method
        String correlationMethod = null;
        if (job.getCorrelationMethod() == CorrelationMethod.MUTUAL_INFORMATION) {
        	correlationMethod = "mutual_information";
        }
        else if (job.getCorrelationMethod() == CorrelationMethod.PEARSON) {
        	correlationMethod = "pearson_r";
        }
        else {
        	assert false;
        }
        entityBuilder.addTextBody("correlation_method", correlationMethod);
        
        // Cutoffs
        entityBuilder.addTextBody("lower_percentile_rank", Double.toString(job.getLowerPercentile()));
        entityBuilder.addTextBody("upper_percentile_rank", Double.toString(job.getUpperPercentile()));

        // Gene families source
        String orthologsSource = null;
        if (job.getGeneFamiliesSource() == GeneFamiliesSource.PLAZA) {
        	orthologsSource = "plaza";
        }
        else if (job.getGeneFamiliesSource() == GeneFamiliesSource.CUSTOM) {
        	orthologsSource = "custom";
            entityBuilder.addBinaryBody("gene_families", job.getGeneFamiliesPath().toFile(), ContentType.TEXT_PLAIN, job.getGeneFamiliesPath().getFileName().toString());
        }
        else if (job.getGeneFamiliesSource() == GeneFamiliesSource.NONE) {
        	orthologsSource = "none";
        }
        else {
        	assert false;
        }
        entityBuilder.addTextBody("gene_families_source", orthologsSource);
        
        return entityBuilder.build();
    }

    private void executeJobOnServer(HttpEntity entity, Path resultPath) throws IOException, JobServerException {
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
            	saveResponse(responseEntity.getContent(), resultPath);
            }
            else if ("application/x-yaml".equals(contentType)) {
            	// Error response
            	Yaml yaml = new Yaml();
            	Map<?,?> root = (Map<?,?>) yaml.load(responseEntity.getContent());
            	Map<?,?> errorNode = (Map<?,?>)root.get("error");
            	throw new JobServerException((String)errorNode.get("message")); // Just show the message generated by the server
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
     * 
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
