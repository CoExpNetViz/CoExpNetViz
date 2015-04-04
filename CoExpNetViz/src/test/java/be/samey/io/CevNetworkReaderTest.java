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
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.NetworkTestSupport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sam
 */
public class CevNetworkReaderTest {

    public CevNetworkReaderTest() {
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

    /**
     * Test of readSIF method, of class CevNetworkReader.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadSIF() throws Exception {
        System.out.println("readSIF");

        //get test resources
        URL url = getClass().getClassLoader().getResource("testdata/network.sif");
        Path sifPath = new File(url.toURI()).toPath();

        //make instances
        NetworkTestSupport nts = new NetworkTestSupport();
        CyAppManager cam = new CyAppManager(null, null);
        CyNetwork cn = nts.getNetwork();

        //use method
        CevNetworkReader cnr = new CevNetworkReader(cam);
        cnr.readSIF(sifPath, cn);

        //check result
        assertEquals(2162, cn.getNodeCount());
        assertEquals(4334, cn.getEdgeCount());
    }

}
