package be.ugent.psb.coexpnetviz.io;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

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

import be.ugent.psb.coexpnetviz.CENVApplication;

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

import be.ugent.psb.coexpnetviz.gui.model.JobInputModel;

/**
 * Read/write of CoExpNetViz settings
 */
public class SettingsIO {

    private final CENVApplication cyAppManager;

    // YAML attribute names
    private static final String TITLE = "title";
    private static final String USE_BAIT_FILE = "useBaitFile";
    private static final String BAITS = "baits";
    private static final String BAIT_FILE_PATH = "baitFilePath";
    private static final String SPECIES_NAMES = "speciesNames";
    private static final String NEGCUTOFF = "negCutoff";
    private static final String POSCUTOFF = "posCutoff";
    private static final String SAVE_RESULTS = "saveResults";
    private static final String SAVE_FILE_PATH = "saveFilePath";
    private static final String PROFILE_FILE_NAME = "profiles";
    private static final String SPECIES_FILE_NAME = "species";

    public SettingsIO(CENVApplication cyAppManager) {
        this.cyAppManager = cyAppManager;
    }

    public void writeAllProfiles(List<JobInputModel> allModels) throws IOException {
    	// TODO use typed read/write https://bitbucket.org/asomov/snakeyaml/wiki/Documentation#markdown-header-type-safe-collections
        Map<String, Map> allProfilesMap = new LinkedHashMap<String, Map>();
        for (JobInputModel ipm : allModels) {
            allProfilesMap.put(ipm.getTitle(), inpPnlProfile2Map(ipm));
        }

        Charset charset = Charset.forName("UTF-8");
        Path profilePath = cyAppManager.getCyModel().getSettingsPath().resolve(PROFILE_FILE_NAME);
        BufferedWriter bw = Files.newBufferedWriter(profilePath, charset, CREATE, TRUNCATE_EXISTING);
        Yaml yaml = new Yaml();
        yaml.dump(allProfilesMap, bw);
    }

    public Map inpPnlProfile2Map(JobInputModel inpPnlModel) {

        List<String> speciesNames = new ArrayList<String>();
        for (SpeciesEntryModel sem : inpPnlModel.getAllSpecies().keySet()) {
            speciesNames.add(sem.getSpeciesName());
        }

        Map<String, Object> profileMap = new LinkedHashMap<String, Object>();
        profileMap.put(USE_BAIT_FILE, inpPnlModel.isUseBaitsFile());
        profileMap.put(BAITS, inpPnlModel.getBaits());
        profileMap.put(BAIT_FILE_PATH, inpPnlModel.getBaitsFilePath().toString());
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

    public List<JobInputModel> readAllProfiles() throws IOException {

        Path profilesPath = cyAppManager.getCyModel().getSettingsPath().resolve(PROFILE_FILE_NAME);
        Map profilesMap = new LinkedHashMap();

        if (Files.isRegularFile(profilesPath) && Files.isReadable(profilesPath)) {
            Charset charset = Charset.forName("UTF-8");
            BufferedReader br = Files.newBufferedReader(profilesPath, charset);
            Yaml yaml = new Yaml();
            profilesMap = (Map) yaml.load(br);
        }

        Map<String, String> speciesMap = readAllSpecies();
        List<JobInputModel> profiles = new ArrayList<JobInputModel>();
        for (Object profileName : profilesMap.keySet()) {
            Map profileMap = (Map) profilesMap.get(profileName);

            //set fields/values etc
            JobInputModel ipm = new JobInputModel();
            ipm.setTitle((String) profileName);
            ipm.setUseBaitsFile((Boolean) profileMap.get(USE_BAIT_FILE));
            ipm.setBaits((String) profileMap.get(BAITS));
            ipm.setBaitsFilePath(Paths.get((String) profileMap.get(BAIT_FILE_PATH)));
            ipm.setNegCutoff((Double) profileMap.get(NEGCUTOFF));
            ipm.setPosCutoff((Double) profileMap.get(POSCUTOFF));
            ipm.setSaveResults((Boolean) profileMap.get(SAVE_RESULTS));
            ipm.setSaveFilePath(Paths.get((String) profileMap.get(SAVE_FILE_PATH)));

            //add species to model
            ArrayList<String> speciesForThisModel;
            speciesForThisModel = (ArrayList<String>) profileMap.get(SPECIES_NAMES);
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

}
