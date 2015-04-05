package be.samey.gui;

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
import be.samey.gui.model.InpPnlModel;
import be.samey.gui.model.SpeciesEntryModel;
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author sam
 */
public class SettingsIOTest {
    
    public SettingsIOTest() {
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
     * Test of readAllProfiles method, of class SettingsIO.
     */
    @Test
    public void testWriteAllProfiles() throws IOException, URISyntaxException {
        System.out.println("writeAllProfiles");

        //get instances
        CyAppManager cam = new CyAppManager(new CyModel(), null);
        SpeciesEntryModel sem1 = new SpeciesEntryModel();
        SpeciesEntry se1 = new SpeciesEntry();
        InpPnlModel ipm1 = new InpPnlModel(sem1, se1);
        SpeciesEntryModel sem2 = new SpeciesEntryModel();
        SpeciesEntry se2 = new SpeciesEntry();
        InpPnlModel ipm2 = new InpPnlModel(sem2, se2);
        List<InpPnlModel> inpPnlModels = new ArrayList<InpPnlModel>();

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //set some values
        String testtitle = "testtitle";
        String testbaits = "\rSolyc03g097500 \t  Solyc02g014730\n Solyc04g011600 AT5G41040\t\t AT5G23190    AT3G11430 ";
        String testspecies = "speciesName1";
        String baitfilepath = "foo";
        ipm1.setTitle(testtitle);
        ipm1.setBaits(testbaits);
        ipm1.setBaitFilePath(Paths.get(baitfilepath));
        sem1.setSpeciesName(testspecies);
        String testtitle2 = "speciesName2";
        ipm2.setTitle(testtitle2);
        inpPnlModels.add(ipm1);
        inpPnlModels.add(ipm2);

        //thest method
        SettingsIO sio = new SettingsIO(cam);
        sio.writeAllProfiles(inpPnlModels);
    }

    /**
     * Test of inpPnlProfile2Map method, of class SettingsIO.
     */
    @Test
    public void testInpPnlProfile2Map() {
        System.out.println("inpPnlProfile2Map");

        //get instances
        CyAppManager cam = new CyAppManager(new CyModel(), null);
        SpeciesEntryModel sem = new SpeciesEntryModel();
        SpeciesEntry se = new SpeciesEntry();
        InpPnlModel ipm = new InpPnlModel(sem, se);

        //set some values
        String testbaits = "\rSolyc03g097500 \t  Solyc02g014730\n Solyc04g011600 AT5G41040\t\t AT5G23190    AT3G11430 ";
        String testspecies = "speciesName1";
        String baitfilepath = "foo";
        ipm.setBaits(testbaits);
        ipm.setBaitFilePath(Paths.get(baitfilepath));
        sem.setSpeciesName(testspecies);

        //thest method
        SettingsIO sio = new SettingsIO(cam);
        Map map = sio.inpPnlProfile2Map(ipm);
        assertArrayEquals(new String[]{"Solyc03g097500", "Solyc02g014730", "Solyc04g011600", "AT5G41040", "AT5G23190", "AT3G11430"}, (String[]) map.get(SettingsIO.BAITS));
        assertArrayEquals(new String[]{testspecies}, (String[]) map.get(SettingsIO.SPECIES_NAMES));
        assertEquals(baitfilepath, map.get(SettingsIO.BAIT_FILE_PATH));
    }

    /**
     * Test of writeAllSpecies method, of class SettingsIO.
     */
    @Test
    public void testWriteAllSpecies() throws IOException, URISyntaxException {
        //get instances
        CyAppManager cam = new CyAppManager(new CyModel(), null);
        SpeciesEntryModel sem1 = new SpeciesEntryModel();
        SpeciesEntryModel sem2 = new SpeciesEntryModel();
        List<SpeciesEntryModel> sems = new ArrayList<SpeciesEntryModel>();

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //set some values
        sem1.setSpeciesName("speciesName1");
        sem1.setSpeciesExprDataPath(Paths.get("species/Path/1"));
        sem2.setSpeciesName("speciesName2");
        sem2.setSpeciesExprDataPath(Paths.get("species/Path/2"));
        sems.add(sem1);
        sems.add(sem2);

        //thest method
        SettingsIO sio = new SettingsIO(cam);
        sio.writeAllSpecies(sems);
    }

    /**
     * Test of readAllProfiles method, of class SettingsIO.
     */
    @Test
    public void testReadAllProfiles() throws URISyntaxException, IOException {
        System.out.println("readAllProfiles");

        //get instances
        CyAppManager cam = new CyAppManager(new CyModel(), null);

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //test method
        SettingsIO sio = new SettingsIO(cam);
        List<InpPnlModel> ipms = sio.readAllProfiles();
    }
    
    /**
     * Test of readAllSpecies method, of class SettingsIO.
     */
    @Test
    public void testReadAllSpecies() throws URISyntaxException, IOException {
        System.out.println("readAllSpecies");

        //get instances
        CyAppManager cam = new CyAppManager(new CyModel(), null);

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //thest method
        SettingsIO sio = new SettingsIO(cam);
        Map<String, String> speciesMap = sio.readAllSpecies();
    }
    
}
