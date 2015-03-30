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
import be.samey.gui.model.InpProfileModel;
import be.samey.gui.model.InpPnlModel;
import be.samey.gui.model.GuiModel;
import be.samey.gui.controller.TfController;
import be.samey.gui.controller.FileTfController;
import be.samey.gui.controller.CutoffController;
import be.samey.gui.controller.SaveResultsController;
import be.samey.gui.controller.UseBaitFileController;
import be.samey.gui.controller.AddSpeciesController;
import be.samey.gui.controller.BrowseController;
import be.samey.gui.controller.RunAnalysisController;
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;

/**
 *
 * @author sam
 */
public class GuiManager {

    private final CyAppManager cyAppManager;
    private final CyModel cyModel;

    private InpPnl inpPnl;
    private JFrame rootFrame;
    private Path lastUsedDirPath;
    private Path settingsPath;

    //baits
    UseBaitFileController useBaitFileController;
    TfController baitInpTac;
    BrowseController baitFileBc;
    FileTfController baitFileTfc;
    //species
    AddSpeciesController addSpeciesController;
    //cutoffs
    CutoffController nCutoffController;
    CutoffController pCutoffController;
    SaveResultsController saveResultsController;
    //save
    FileTfController saveFileTfc;
    BrowseController saveFileBc;
    //controllers that acces non-GUI classes
    RunAnalysisController runAnalysisController;
    ResetGuiController resetGuiController;

    public GuiManager(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();
    }

    public void initGui() {
        //make GUI
        inpPnl = new InpPnl();

        //make controllers
        //baits
        useBaitFileController = new UseBaitFileController();
        baitInpTac = new TfController() {
            @Override
            public void updateModel(GuiModel guiModel, String text) {
                ((InpPnlModel) guiModel).setBaits(text);
            }
        };
        baitFileTfc = new FileTfController() {
            @Override
            public void updateModel(GuiModel guiModel, Path path) {
                ((InpPnlModel) guiModel).setBaitFilePath(path);
            }
        };
        baitFileBc = new BrowseController(inpPnl, "Choose file with bait genes", BrowseController.FILE) {
            @Override
            public void updateModel(GuiModel guiModel, Path path) {
                ((InpPnlModel) guiModel).setBaitFilePath(path);
            }
        };
        //species
        addSpeciesController = new AddSpeciesController();
        //cutoffs
        nCutoffController = new CutoffController() {
            @Override
            public void updateModel(InpPnlModel inpPnlModel, double cutOff) {
                inpPnlModel.setNegCutoff(cutOff);
            }
        };
        pCutoffController = new CutoffController() {
            @Override
            public void updateModel(InpPnlModel inpPnlModel, double cutOff) {
                inpPnlModel.setPosCutoff(cutOff);
            }
        };
        //save
        saveFileTfc = new FileTfController() {
            @Override
            public void updateModel(GuiModel guiModel, Path path) {
                ((InpPnlModel) guiModel).setSaveFilePath(path);
            }
        };
        saveFileBc = new BrowseController(inpPnl, "Choose output directory", BrowseController.DIRECTORY) {
            @Override
            public void updateModel(GuiModel guiModel, Path path) {
                ((InpPnlModel) guiModel).setSaveFilePath(path);
            }
        };
        saveResultsController = new SaveResultsController();
        //controllers that acces non-GUI classes
        runAnalysisController = new RunAnalysisController(cyAppManager);
        resetGuiController = new ResetGuiController(cyAppManager);

        //attach controllers to GUI
        //baits
        inpPnl.inpBaitRb.addActionListener(useBaitFileController);
        inpPnl.useBaitFileRb.addActionListener(useBaitFileController);
        inpPnl.inpBaitTa.addFocusListener(baitInpTac);
        inpPnl.baitFileTf.addFocusListener(baitFileTfc);
        inpPnl.baitFileBtn.addActionListener(baitFileBc);
        //species
        inpPnl.addSpeciesBtn.addActionListener(addSpeciesController);
        //cutoffs
        inpPnl.pCutoffSp.addChangeListener(pCutoffController);
        inpPnl.nCutoffSp.addChangeListener(nCutoffController);
        //save
        inpPnl.saveFileChb.addActionListener(saveResultsController);
        inpPnl.saveFileBtn.addActionListener(saveFileBc);
        inpPnl.saveFileTf.addFocusListener(saveFileTfc);
        //controllers that acces non-GUI classes
        inpPnl.goBtn.addActionListener(runAnalysisController);
        inpPnl.resetBtn.addActionListener(resetGuiController);

        //make profile and attach it to controllers and GUI
        InpProfileModel ipm = makeDefaultProfile();
        
        //for debugging
        testWithDefaults(ipm);

        applyProfile(ipm);

        cyModel.addObserver(inpPnl);

        //put the GUI in a new Frame
        rootFrame = new JFrame(GuiConstants.ROOTFRAME_TITLE);
        rootFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        rootFrame.setContentPane(inpPnl);

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

    public Path getSettingsPath() {
        return settingsPath;
    }

    public void setSettingsPath(Path settingsPath) {
        this.settingsPath = settingsPath;
    }

    public void showRootFrame() {
        rootFrame.pack();
        rootFrame.setVisible(true);
    }

    public InpProfileModel makeDefaultProfile() {
        //TODO get last used dir and profiles from file
        lastUsedDirPath = Paths.get("user.home");

        //make Gui models
        InpPnlModel inpPnlModel = new InpPnlModel();
        inpPnlModel.setDefaultDirPath(lastUsedDirPath);
        SpeciesEntryModel speciesEntryModel = new SpeciesEntryModel();
        speciesEntryModel.setDefaultDirPath(lastUsedDirPath);
        InpProfileModel inpProfileModel = new InpProfileModel();
        inpProfileModel.setInpPnlModel(inpPnlModel);
        inpProfileModel.addSpeciesEntryModel(speciesEntryModel);

        return inpProfileModel;

    }

    //TODO
    void readProfiles() {

    }

    public void applyProfile(InpProfileModel inpProfileModel) {

        InpPnlModel inpPnlModel = inpProfileModel.getInpPnlModel();

        //Tell GUI to listen to this model from now on
        inpPnl.setInpProfile(inpProfileModel);

        //Tell action listeners to update this model from now on
        //baits
        useBaitFileController.setInpPnlModel(inpPnlModel);
        baitInpTac.setGuiModel(inpPnlModel);
        baitFileTfc.setGuiModel(inpPnlModel);
        baitFileBc.setGuiModel(inpPnlModel);
        //species
        addSpeciesController.setInpProfileModel(inpProfileModel);
        //cutoffs
        nCutoffController.setGuiModel(inpPnlModel);
        pCutoffController.setGuiModel(inpPnlModel);
        //save
        saveResultsController.setInpPnlModel(inpPnlModel);
        saveFileBc.setGuiModel(inpPnlModel);
        saveFileTfc.setGuiModel(inpPnlModel);
        //controllers that acces non-GUI classes
        runAnalysisController.setInpProfileModel(inpProfileModel);

        inpProfileModel.triggerUpdate();
    }

    //for debugging, adds some default input for quick testing
    private void testWithDefaults(InpProfileModel ipm) {
        InpPnlModel inpPnlModel = ipm.getInpPnlModel();
        inpPnlModel.setUseBaitFile(true);
        inpPnlModel.setBaitFilePath(Paths.get("/home/sam/favs/uma1_s2-mp2-web-datasets/baits.txt"));
        ipm.getSpeciesEntryModel(0).setSpeciesName("Tomato");
        ipm.getSpeciesEntryModel(0).setSpeciesExprDataPath(Paths.get("/home/sam/favs/uma1_s2-mp2-web-datasets/Tomato_dataset.txt"));
        ipm.addSpeciesEntryModel(new SpeciesEntryModel());
        ipm.getSpeciesEntryModel(1).setSpeciesName("Apple");
        ipm.getSpeciesEntryModel(1).setSpeciesExprDataPath(Paths.get("/home/sam/favs/uma1_s2-mp2-web-datasets/Apple_dataset.txt"));
        ipm.addSpeciesEntryModel(new SpeciesEntryModel());
        ipm.getSpeciesEntryModel(2).setSpeciesName("Arabidopsis");
        ipm.getSpeciesEntryModel(2).setSpeciesExprDataPath(Paths.get("/home/sam/favs/uma1_s2-mp2-web-datasets/Arabidopsis_dataset.txt"));
        ipm.addSpeciesEntryModel(new SpeciesEntryModel());
        ipm.getSpeciesEntryModel(3).setSpeciesName("Patato");
        ipm.getSpeciesEntryModel(3).setSpeciesExprDataPath(Paths.get("/home/sam/favs/uma1_s2-mp2-web-datasets/Potato_dataset.txt"));
    }
}

//        guiStatus.getSpeciesList().get(0).speciesPathTf.setText();
//        guiStatus.addSpecies(new SpeciesEntry(model));
//        guiStatus.getSpeciesList().get(1).speciesTf.setText("Apple");
//        guiStatus.getSpeciesList().get(1).speciesPathTf.setText("/home/sam/favs/uma1_s2-mp2-web-datasets/Apple_dataset.txt");
//        guiStatus.addSpecies(new SpeciesEntry(model));
//        guiStatus.getSpeciesList().get(2).speciesTf.setText("Arabidopsis");
//        guiStatus.getSpeciesList().get(2).speciesPathTf.setText("/home/sam/favs/uma1_s2-mp2-web-datasets/Arabidopsis_dataset.txt");
//        guiStatus.addSpecies(new SpeciesEntry(model));
//        guiStatus.getSpeciesList().get(3).speciesTf.setText("Patato");
//        guiStatus.getSpeciesList().get(3).speciesPathTf.setText("/home/sam/favs/uma1_s2-mp2-web-datasets/Potato_dataset.txt");
//    }