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
import be.samey.internal.CyServices;
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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sam
 */
public class CevTableReaderTest {

    public CevTableReaderTest() {
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
     * Test of readNOA method, of class CevTableReader.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadNOA() throws Exception {
        System.out.println("readNOA");

        //get test resources
        URL noaURL = getClass().getClassLoader().getResource("testdata/network.node.attr");
        Path noaPath = new File(noaURL.toURI()).toPath();
        URL sifURL = getClass().getClassLoader().getResource("testdata/network.sif");
        Path sifPath = new File(sifURL.toURI()).toPath();

        //make instances
        NetworkTestSupport nts = new NetworkTestSupport();
        CyNetworkTableManager cntm = nts.getNetworkTableManager();
        CyServices cs = new CyServices();
        cs.setCyNetworkTableManager(cntm);
        CyAppManager cam = new CyAppManager(new CyModel(), cs);
        CyNetwork cn = nts.getNetwork();
        CevNetworkReader cnr = new CevNetworkReader(cam);
        cnr.readSIF(sifPath, cn);

        //use method
        CevTableReader ctr = new CevTableReader(cam);
        CyTable ct = ctr.readNOA(noaPath, cn);

        //check result
        assertEquals(2162, ct.getRowCount());
        assertEquals(String.class, ct.getColumn("Species").getType());
    }

    /**
     * Test of readEDA method, of class CevTableReader.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadEDA() throws Exception {
        System.out.println("readEDA");

        //get test resources
        URL noaURL = getClass().getClassLoader().getResource("testdata/network.edge.attr");
        Path edaPath = new File(noaURL.toURI()).toPath();
        URL sifURL = getClass().getClassLoader().getResource("testdata/network.sif");
        Path sifPath = new File(sifURL.toURI()).toPath();

        //make instances
        NetworkTestSupport nts = new NetworkTestSupport();
        CyNetworkTableManager cntm = nts.getNetworkTableManager();
        CyServices cs = new CyServices();
        cs.setCyNetworkTableManager(cntm);
        CyAppManager cam = new CyAppManager(new CyModel(), cs);
        CyNetwork cn = nts.getNetwork();
        CevNetworkReader cnr = new CevNetworkReader(cam);
        cnr.readSIF(sifPath, cn);

        //use method
        CevTableReader ctr = new CevTableReader(cam);
        CyTable ct = ctr.readEDA(edaPath, cn);

        //check result
        assertEquals(4334, ct.getRowCount());
        assertEquals(Double.class, ct.getColumn("r_value").getType());
    }

}
