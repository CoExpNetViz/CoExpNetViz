package be.ugent.psb.coexpnetviz.io.test;

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

import be.ugent.psb.coexpnetviz.Context;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.NetworkTestSupport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class NetworkReaderTest {

	private CyNetwork cn;

    public NetworkReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public void readSIF() throws Exception {
    	assert false;
    	/*URL sifURL = getClass().getClassLoader().getResource("testdata/network.sif");
        Path sifPath = new File(sifURL.toURI()).toPath();
        
        NetworkTestSupport nts = new NetworkTestSupport();
        CyNetworkTableManager cntm = nts.getNetworkTableManager();
        CENVApplication cam = new CENVApplication();
        cam.setCyNetworkTableManager(cntm);
        cam.setCyNetworkFactory(nts.getNetworkFactory());
        cnr = new NetworkReader(cam);
        cn = cnr.getNetwork();
        cnr.readSIF(sifPath);
        
        assertEquals(1440, cn.getNodeCount());
        assertEquals(2814, cn.getEdgeCount());*/
    }
    
    /**
     * Test of readNOA method, of class CevTableReader.
     * @throws java.lang.Exception
     */
    @Ignore
    @Test
    public void testReadNOA() throws Exception {
    	assert false;
    	/*
        System.out.println("readNOA");

        URL noaURL = getClass().getClassLoader().getResource("testdata/network.node.attr");
        Path noaPath = new File(noaURL.toURI()).toPath();

        readSIF();        
        CyTable ct = cnr.readNodeAttributes(noaPath);

        assertEquals(1440, ct.getRowCount());
        assertEquals(String.class, ct.getColumn("species").getType());*/
    }

    /**
     * Test of readEDA method, of class CevTableReader.
     * @throws java.lang.Exception
     */
    @Ignore
    @Test
    public void testReadEDA() throws Exception {
    	assert false;
        /*System.out.println("readEDA");

        URL noaURL = getClass().getClassLoader().getResource("testdata/network.edge.attr");
        Path edaPath = new File(noaURL.toURI()).toPath();

        readSIF();
        CyTable ct = cnr.readEdgeAttributes(edaPath);

        assertEquals(2814, ct.getRowCount());
        assertEquals(Double.class, ct.getColumn("r_value").getType());*/
    }

}
