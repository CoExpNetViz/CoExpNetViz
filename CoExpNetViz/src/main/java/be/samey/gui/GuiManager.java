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
import be.samey.gui.controller.ResetGuiController;
import be.samey.gui.model.SpeciesEntryModel;
import be.samey.gui.model.InpPnlModel;
import be.samey.gui.controller.SaveFileChbController;
import be.samey.gui.controller.SpeciesAddBtnController;
import be.samey.gui.controller.SpeciesFileBtnController;
import be.samey.gui.controller.BaitFileTfController;
import be.samey.gui.controller.RunAnalysisController;
import be.samey.gui.controller.BaitFileBtnController;
import be.samey.gui.controller.SaveFileTfController;
import be.samey.gui.controller.BaitFileOrInpController;
import be.samey.gui.controller.BaitInpInfoBtnController;
import be.samey.gui.controller.BaitInpTaController;
import be.samey.gui.controller.CutoffController;
import be.samey.gui.controller.OrthDelController;
import be.samey.gui.controller.OrthEntryAddBtnController;
import be.samey.gui.controller.OrthFileBtnController;
import be.samey.gui.controller.OrthFileTfController;
import be.samey.gui.controller.OrthNameTfController;
import be.samey.gui.controller.ProfDelBtnController;
import be.samey.gui.controller.SaveFileBtnController;
import be.samey.gui.controller.SpeciesDelController;
import be.samey.gui.controller.SpeciesFileTfController;
import be.samey.gui.controller.SpeciesNameTfController;
import be.samey.gui.controller.TitleTfController;
import be.samey.gui.controller.ProfLoadBtnController;
import be.samey.gui.controller.ProfSaveBtnController;
import be.samey.gui.model.OrthEntryModel;
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author sam
 */
public class GuiManager {

    private final CyAppManager cyAppManager;
    private final CyModel cyModel;

    private final InpPnl inpPnl;
    private final JFrame rootFrame;

    private Path lastUsedDirPath;

    private InpPnlModel activeModel;

    private SettingsIO sio;
    private List<InpPnlModel> allModels;

    public GuiManager(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();

        lastUsedDirPath = Paths.get(System.getProperty("user.home"));

        //load settings
        sio = new SettingsIO(cyAppManager);
        allModels = new ArrayList<InpPnlModel>();
        try {
            allModels = sio.readAllProfiles();
        } catch (IOException ex) {
            //TODO: warn user
        }

        //make GUI
        inpPnl = initGui();

        //put the GUI in a new Frame
        rootFrame = new JFrame(GuiConstants.ROOTFRAME_TITLE);
        rootFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        rootFrame.setContentPane(inpPnl);

    }

    private InpPnl initGui() {
        InpPnl tempInpPnl = new InpPnl();

        //attach controllers
        //buttons
        tempInpPnl.profLoadBtn.addActionListener(new ProfLoadBtnController(cyAppManager));
        tempInpPnl.profSaveBtn.addActionListener(new ProfSaveBtnController(cyAppManager));
        tempInpPnl.profDelBtn.addActionListener(new ProfDelBtnController(cyAppManager));
        tempInpPnl.resetBtn.addActionListener(new ResetGuiController(cyAppManager));
        //title
        tempInpPnl.titleTf.addFocusListener(new TitleTfController(cyAppManager));
        //baits
        tempInpPnl.baitInpRb.addActionListener(new BaitFileOrInpController(cyAppManager));
        tempInpPnl.baitFileRb.addActionListener(new BaitFileOrInpController(cyAppManager));
        tempInpPnl.baitInpInfoBtn.addActionListener(new BaitInpInfoBtnController(cyAppManager));
        tempInpPnl.baitInpTa.addFocusListener(new BaitInpTaController(cyAppManager));
        tempInpPnl.baitFileTf.addFocusListener(new BaitFileTfController(cyAppManager));
        tempInpPnl.baitFileBtn.addActionListener(new BaitFileBtnController(cyAppManager));
        //species
        tempInpPnl.addSpeciesBtn.addActionListener(new SpeciesAddBtnController(cyAppManager));
        //cutoffs
        tempInpPnl.nCutoffSp.addChangeListener(new CutoffController(cyAppManager));
        tempInpPnl.pCutoffSp.addChangeListener(new CutoffController(cyAppManager));
        //save
        tempInpPnl.saveFileChb.addActionListener(new SaveFileChbController(cyAppManager));
        tempInpPnl.saveFileTf.addFocusListener(new SaveFileTfController(cyAppManager));
        tempInpPnl.saveFileBtn.addActionListener(new SaveFileBtnController(cyAppManager));
        //add orth groups file
        tempInpPnl.orthAddBtn.addActionListener(new OrthEntryAddBtnController(cyAppManager));
        //go
        tempInpPnl.goBtn.addActionListener(new RunAnalysisController(cyAppManager));

        //make the model for the GUI
        activeModel = makeDefaultModel();
        activeModel.addObserver(tempInpPnl);

        return tempInpPnl;

    }

    public void showRootFrame() {
        rootFrame.pack();
        rootFrame.setVisible(true);
        activeModel.triggerUpdate();
    }

    /**
     * Create speciesEntry with controllers that updates sem
     *
     * @param sem
     * @return
     */
    public SpeciesEntry initSpeciesEntry(SpeciesEntryModel sem) {
        SpeciesEntry se = new SpeciesEntry();

        se.speciesNameTf.addFocusListener(new SpeciesNameTfController(cyAppManager, sem));
        se.speciesFileTf.addFocusListener(new SpeciesFileTfController(cyAppManager, sem));
        se.removeBtn.addActionListener(new SpeciesDelController(cyAppManager, sem));
        se.browseBtn.addActionListener(new SpeciesFileBtnController(cyAppManager, sem));

        sem.addObserver(se);

        return se;
    }

    public OrthEntry initOrthEntry(OrthEntryModel oem){
        OrthEntry oe = new OrthEntry();

        oem.addObserver(oe);

        //add controllers for textfield, browse and remove
        oe.orthNameTf.addFocusListener(new OrthNameTfController(cyAppManager, oem));
        oe.orthRemoveBtn.addActionListener(new OrthDelController(oem, cyAppManager));
        oe.orthPathTf.addFocusListener(new OrthFileTfController(oem, cyAppManager));
        oe.orthBrowseBtn.addActionListener(new OrthFileBtnController(oem, cyAppManager));

        return oe;
    }

    public InpPnlModel makeDefaultModel() {
        SpeciesEntryModel sem = new SpeciesEntryModel();
        SpeciesEntry se = initSpeciesEntry(sem);
        return new InpPnlModel(sem, se);
    }

    public void addCurrentModel() {
        InpPnlModel ipmToRemove = null;
        for (InpPnlModel ipm : allModels) {
            if (ipm.getTitle().equals(activeModel.getTitle())) {
                ipmToRemove = ipm;
            }
        }
        if (ipmToRemove != null) {
            allModels.remove(ipmToRemove);
        }
        allModels.add(activeModel.copy());
    }

    public void saveProfiles() throws IOException {
        sio.writeAllProfiles(allModels);
        sio.writeAllSpecies(getAllSpeciesEntryModels());
    }

    public List<SpeciesEntryModel> getAllSpeciesEntryModels() {
        List<SpeciesEntryModel> sems = new ArrayList<SpeciesEntryModel>();
        for (InpPnlModel ipm : allModels) {
            for (SpeciesEntryModel sem : ipm.getAllSpecies().keySet()) {
                if (!sems.contains(sem)) {
                    sems.add(sem);
                }
            }
        }
        return sems;
    }

    public void loadProfile(String profileName) {
        InpPnlModel ipmToLoad = null;
        for (InpPnlModel ipm : allModels) {
            if (ipm.getTitle().equals(profileName)) {
                ipmToLoad = ipm.copy();
            }
        }
        if (ipmToLoad == null) {
            throw new IllegalArgumentException("Could not load profile" + profileName);
        }
        for (SpeciesEntryModel sem : ipmToLoad.getAllSpecies().keySet()) {
            SpeciesEntry se = initSpeciesEntry(sem);
            ipmToLoad.setSpeciesEntry(sem, se);
        }
        setActiveModel(ipmToLoad);
        ipmToLoad.triggerUpdate();
    }

    public void delCurrentProfile() {
        InpPnlModel ipmToRemove = null;
        for (InpPnlModel ipm : allModels) {
            if (ipm.getTitle().equals(activeModel.getTitle())) {
                ipmToRemove = ipm;
            }
        }
        if (ipmToRemove != null) {
            allModels.remove(ipmToRemove);
        }
    }

    public String[] getProfileTitles() {
        List<String> titles = new ArrayList<String>();
        for (InpPnlModel ipm : allModels) {
            titles.add(ipm.getTitle());
        }
        return titles.toArray(new String[titles.size()]);
    }

    public List<InpPnlModel> getAllModels() {
        return allModels;
    }

    public InpPnl getInpPnl() {
        return inpPnl;
    }

    public JFrame getRootFrame() {
        return rootFrame;
    }

    public Path getLastUsedDirPath() {
        return lastUsedDirPath;
    }

    public void setLastUsedDirPath(Path lastUsedDirPath) {
        this.lastUsedDirPath = lastUsedDirPath;
    }

    public void setActiveModel(InpPnlModel inpPnlModel) {
        if (inpPnlModel != this.activeModel) {
            this.activeModel.deleteObserver(inpPnl);
            this.activeModel = inpPnlModel;
            activeModel.addObserver(inpPnl);
        }
    }

    public InpPnlModel getActiveModel() {
        return activeModel;
    }

}
