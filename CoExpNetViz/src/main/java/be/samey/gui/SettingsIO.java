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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import static java.nio.file.StandardOpenOption.*;

/**
 *
 * @author sam
 */
public class SettingsIO {

    private final CyAppManager cyAppManager;

    public static final String TITLE = "title";
    public static final String USE_BAIT_FILE = "useBaitFile";
    public static final String BAITS = "baits";
    public static final String BAIT_FILE_PATH = "baitFilePath";
    public static final String SPECIES_NAMES = "speciesNames";
    public static final String NEGCUTOFF = "negCutoff";
    public static final String POSCUTOFF = "posCutoff";
    public static final String SAVE_RESULTS = "saveResults";
    public static final String SAVE_FILE_PATH = "saveFilePath";

    public static final String PROFILE_FILE_NAME = "profiles";
    public static final String SPECIES_FILE_NAME = "species";

    public SettingsIO(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
    }

    public void writeAllProfiles(List<InpPnlModel> allModels) throws IOException {
        Map<String, Map> allProfilesMap = new LinkedHashMap<String, Map>();
        for (InpPnlModel ipm : allModels) {
            allProfilesMap.put(ipm.getTitle(), inpPnlProfile2Map(ipm));
        }

        Charset charset = Charset.forName("UTF-8");
        Path profilePath = cyAppManager.getCyModel().getSettingsPath().resolve(PROFILE_FILE_NAME);
        BufferedWriter bw = Files.newBufferedWriter(profilePath, charset, CREATE, TRUNCATE_EXISTING);
        Yaml yaml = new Yaml();
        yaml.dump(allProfilesMap, bw);
    }

    public Map inpPnlProfile2Map(InpPnlModel inpPnlModel) {

        List<String> speciesNames = new ArrayList<String>();
        for (SpeciesEntryModel sem : inpPnlModel.getAllSpecies().keySet()) {
            speciesNames.add(sem.getSpeciesName());
        }

        Map<String, Object> profileMap = new LinkedHashMap<String, Object>();
        profileMap.put(USE_BAIT_FILE, inpPnlModel.isUseBaitFile());
        profileMap.put(BAITS, inpPnlModel.getBaits().trim().split("\\s+"));
        profileMap.put(BAIT_FILE_PATH, inpPnlModel.getBaitFilePath().toString());
        profileMap.put(SPECIES_NAMES, speciesNames.toArray(new String[speciesNames.size()]));
        profileMap.put(NEGCUTOFF, inpPnlModel.getNegCutoff());
        profileMap.put(POSCUTOFF, inpPnlModel.getPosCutoff());
        profileMap.put(SAVE_RESULTS, inpPnlModel.isSaveResults());
        profileMap.put(SAVE_FILE_PATH, inpPnlModel.getSaveFilePath().toString());

        return profileMap;
    }

    public void writeAllSpecies(List<SpeciesEntryModel> sems) throws IOException {
        Map<String, String> speciesModelMap = new LinkedHashMap<String, String>();
        for (SpeciesEntryModel sem : sems) {
            speciesModelMap.put(sem.getSpeciesName(), sem.getSpeciesFilePath().toString());
        }

        Charset charset = Charset.forName("UTF-8");
        BufferedWriter bw = Files.newBufferedWriter(cyAppManager.getCyModel().getSettingsPath().resolve(SPECIES_FILE_NAME), charset, CREATE, TRUNCATE_EXISTING);
        Yaml yaml = new Yaml();
        yaml.dump(speciesModelMap, bw);
    }

    public List<InpPnlModel> readAllProfiles() throws IOException {

        Path profilesPath = cyAppManager.getCyModel().getSettingsPath().resolve(PROFILE_FILE_NAME);
        Map profilesMap = new LinkedHashMap();

        if (Files.isRegularFile(profilesPath) && Files.isReadable(profilesPath)) {
            Charset charset = Charset.forName("UTF-8");
            BufferedReader br = Files.newBufferedReader(profilesPath, charset);
            Yaml yaml = new Yaml();
            profilesMap = (Map) yaml.load(br);
        }

        Map<String, String> speciesMap = readAllSpecies();
        List<InpPnlModel> profiles = new ArrayList<InpPnlModel>();
        for (Object profileName : profilesMap.keySet()) {
            Map profileMap = (Map) profilesMap.get(profileName);

            //set fields/values etc
            InpPnlModel ipm = new InpPnlModel();
            ipm.setTitle(profileName.toString());
            ipm.setUseBaitFile((Boolean) profileMap.get(USE_BAIT_FILE));
            ipm.setBaits(combine((ArrayList) profileMap.get(BAITS), " "));
            ipm.setBaitFilePath(Paths.get(profileMap.get(BAIT_FILE_PATH).toString()));
            ipm.setNegCutoff((Double) profileMap.get(NEGCUTOFF));
            ipm.setPosCutoff((Double) profileMap.get(POSCUTOFF));
            ipm.setSaveResults((Boolean) profileMap.get(SAVE_RESULTS));
            ipm.setSaveFilePath(Paths.get(profileMap.get(SAVE_FILE_PATH).toString()));

            //add species to model
            ArrayList<String> speciesForThisModel;
            speciesForThisModel = (ArrayList) profileMap.get(SPECIES_NAMES);
            for (String speciesName : speciesForThisModel) {
                if (speciesMap.containsKey(speciesName)) {
                    SpeciesEntryModel sem = new SpeciesEntryModel();
                    sem.setSpeciesName(speciesName);
                    sem.setSpeciesExprDataPath(Paths.get(speciesMap.get(speciesName)));
                    ipm.addSpecies(sem, null);
                }
            }

            //add to profiles
            profiles.add(ipm);
        }
        return profiles;
    }

    public Map<String, String> readAllSpecies() throws IOException {

        Path speciesPath = cyAppManager.getCyModel().getSettingsPath().resolve(SPECIES_FILE_NAME);
        Map<String, String> speciesMap = new LinkedHashMap<String, String>();

        if (Files.isRegularFile(speciesPath) && Files.isReadable(speciesPath)) {
            Charset charset = Charset.forName("UTF-8");
            BufferedReader br = Files.newBufferedReader(speciesPath, charset);
            Yaml yaml = new Yaml();
            speciesMap = (Map) yaml.load(br);
        }

        return speciesMap;

    }

    String combine(ArrayList<String> s, String glue) {
        int k = s.size();
        if (k == 0) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        out.append(s.get(0));
        for (int i = 1; i < k; ++i) {
            out.append(glue).append(s.get(i));
        }
        return out.toString();
    }
}
