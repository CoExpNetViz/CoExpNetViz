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

    CyAppManager cam;
    SettingsIO sio;
    InpPnlModel ipm;
    SpeciesEntryModel sem1;
    SpeciesEntryModel sem2;
    SpeciesEntry se1;
    SpeciesEntry se2;

    String baits1 = "\rSolyc03g097500 \t  Solyc02g014730\n Solyc04g011600 AT5G41040\t\t AT5G23190    AT3G11430 ";
    String[] baitArr = new String[]{"Solyc03g097500", "Solyc02g014730", "Solyc04g011600", "AT5G41040", "AT5G23190", "AT3G11430"};
    String speciesName1 = "Species name 1";
    String speciesName2 = "Species name 2";
    Path path1 = Paths.get("Path/To/File");
    Path path2 = Paths.get("Path/To/Other/File");
    Path baitFilePath = Paths.get("Path/To/Biats");

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
        //get instances
        cam = new CyAppManager(new CyModel(), null);
        sio = new SettingsIO(cam);
        sem1 = new SpeciesEntryModel();
        sem2 = new SpeciesEntryModel();
        se1 = new SpeciesEntry();
        se2 = new SpeciesEntry();
        ipm = new InpPnlModel(sem1, se1);
        ipm.addSpecies(sem2, se2);

        //set some values
        ipm.setTitle("The title");
        ipm.setSaveResults(true);
        ipm.setSaveFilePath(Paths.get(""));
        ipm.setBaits(baits1);
        ipm.setBaitFilePath(baitFilePath);
        sem1.setSpeciesName(speciesName1);
        sem1.setSpeciesExprDataPath(path1);
        sem2.setSpeciesName(speciesName2);
        sem2.setSpeciesExprDataPath(path2);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of inpPnlProfile2Map method, of class SettingsIO.
     */
    @Test
    public void testInpPnlProfile2Map() {
        System.out.println("inpPnlProfile2Map");

        //test method
        Map map = sio.inpPnlProfile2Map(ipm);
        assertArrayEquals(baitArr, (String[]) map.get(SettingsIO.BAITS));
        assertArrayEquals(new String[]{speciesName1, speciesName2}, (String[]) map.get(SettingsIO.SPECIES_NAMES));
        assertEquals(baitFilePath.toString(), map.get(SettingsIO.BAIT_FILE_PATH));
    }

    /**
     * Test of readAllProfiles method, of class SettingsIO. This is for for
     * for convenience when testing new values in the inpPnlModel
     */
//    @Ignore
    @Test
    public void testWriteAllProfiles() throws IOException, URISyntaxException {
        System.out.println("writeAllProfiles");

        //set some values
        List<InpPnlModel> inpPnlModels = new ArrayList<InpPnlModel>();
        InpPnlModel ipmOne = ipm.copy();
        ipmOne.setTitle("title one");
        InpPnlModel ipmTwo = ipm.copy();
        ipmTwo.setTitle("title two");
        inpPnlModels.add(ipmOne);
        inpPnlModels.add(ipmTwo);

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings_out");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //test method
        sio.writeAllProfiles(inpPnlModels);
    }

    /**
     * Test of writeAllSpecies method, of class SettingsIO. for convenience when
     * testing new values in the inpPnlModel
     */
//    @Ignore
    @Test
    public void testWriteAllSpecies() throws IOException, URISyntaxException {

        //set some values
        List<SpeciesEntryModel> sems = new ArrayList<SpeciesEntryModel>();
        sems.add(sem1);
        sems.add(sem2);

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings_out");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //test method
        sio.writeAllSpecies(sems);
    }

    /**
     * Test of readAllProfiles method, of class SettingsIO.
     */
    @Test
    public void testReadAllProfiles() throws URISyntaxException, IOException {
        System.out.println("readAllProfiles");

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings_in");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //test method
        List<InpPnlModel> ipms = sio.readAllProfiles();
        assertEquals("positive cutoff", ((InpPnlModel) ipms.get(0)).getTitle());
        assertEquals(4, ((InpPnlModel) ipms.get(0)).getAllSpecies().size());
        assertEquals(1, ((InpPnlModel) ipms.get(2)).getAllSpecies().size());
    }

    /**
     * Test of readAllSpecies method, of class SettingsIO.
     */
    @Test
    public void testReadAllSpecies() throws URISyntaxException, IOException {
        System.out.println("readAllSpecies");

        //redirect settings folder for the test
        URL url = getClass().getClassLoader().getResource("testsettings_in");
        Path settingsPath = new File(url.toURI()).toPath();
        cam.getCyModel().setSettingsPath(settingsPath);

        //test method
        Map<String, String> speciesMap = sio.readAllSpecies();
        assertEquals("/datasets/Apple_dataset.txt", speciesMap.get("Apple_dataset"));
    }

}
