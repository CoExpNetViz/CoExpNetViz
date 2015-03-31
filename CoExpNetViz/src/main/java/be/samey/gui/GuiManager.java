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
import be.samey.gui.controller.BaitInpTaController;
import be.samey.gui.controller.CutoffController;
import be.samey.gui.controller.SaveFileBtnController;
import be.samey.gui.controller.SpeciesDelController;
import be.samey.gui.controller.SpeciesFileTfController;
import be.samey.gui.controller.SpeciesNameTfController;
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    private InpPnlModel activeModel;

    private List<InpPnlModel> allModels;

    public GuiManager(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
        this.cyModel = cyAppManager.getCyModel();

        lastUsedDirPath = Paths.get(System.getProperty("user.home"));
        settingsPath = initSettingsPath();
        System.out.println(settingsPath);
    }

    public void initGui() {
        //make GUI
        inpPnl = new InpPnl();

        //attach controllers
        //baits
        inpPnl.baitInpRb.addActionListener(new BaitFileOrInpController(cyAppManager));
        inpPnl.baitFileRb.addActionListener(new BaitFileOrInpController(cyAppManager));
        inpPnl.baitInpTa.addFocusListener(new BaitInpTaController(cyAppManager));
        inpPnl.baitFileTf.addFocusListener(new BaitFileTfController(cyAppManager));
        inpPnl.baitFileBtn.addActionListener(new BaitFileBtnController(cyAppManager));
        //species
        inpPnl.addSpeciesBtn.addActionListener(new SpeciesAddBtnController(cyAppManager));
        //cutoffs
        inpPnl.nCutoffSp.addChangeListener(new CutoffController(cyAppManager));
        inpPnl.pCutoffSp.addChangeListener(new CutoffController(cyAppManager));
        //save
        inpPnl.saveFileChb.addActionListener(new SaveFileChbController(cyAppManager));
        inpPnl.saveFileTf.addFocusListener(new SaveFileTfController(cyAppManager));
        inpPnl.saveFileBtn.addActionListener(new SaveFileBtnController(cyAppManager));
        //buttons
        inpPnl.goBtn.addActionListener(new RunAnalysisController(cyAppManager));
        inpPnl.resetBtn.addActionListener(new ResetGuiController(cyAppManager));

        activeModel = makeDefaultModel();

        activeModel.addObserver(inpPnl);

        //put the GUI in a new Frame
        rootFrame = new JFrame(GuiConstants.ROOTFRAME_TITLE);
        rootFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        rootFrame.setContentPane(inpPnl);

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

    public InpPnlModel makeDefaultModel() {
        SpeciesEntryModel sem = new SpeciesEntryModel();
        SpeciesEntry se = initSpeciesEntry(sem);
        return new InpPnlModel(sem, se);
    }

    private Path initSettingsPath() {

        Path cyHomePath = Paths.get(System.getProperty("user.dir"));
        Path cyConfPath = cyHomePath.resolve("CytoscapeConfiguration");

        Path localSettingsPath;

        //try to get a settings directory in the cytoscape config folder
        if (Files.isDirectory(cyConfPath) && Files.isWritable(cyConfPath)) {
            localSettingsPath = cyConfPath.resolve(CyModel.APP_NAME + "_settings");
            //settins folder doesn't exists, so try to make it
            if (!Files.exists(localSettingsPath)) {
                try {
                    Files.createDirectory(localSettingsPath);
                    return localSettingsPath;
                } catch (IOException ex) {
                    System.out.println(ex);
                    //TODO:warn user somehow
                }
            } else if (Files.isDirectory(localSettingsPath) && Files.isWritable(localSettingsPath)) {
                //settings folder exists, check if I can write there
                return localSettingsPath;
            }
        }

        //if the above attempt failed, then try to get a settings a folder in
        // the user home directory
        localSettingsPath = Paths.get(System.getProperty("user.home"));
        localSettingsPath = localSettingsPath.resolve(CyModel.APP_NAME + "_settings");
        if (!Files.exists(localSettingsPath)) {
            //settins folder doesn't exists, so try to make it
            try {
                Files.createDirectory(localSettingsPath);
                return localSettingsPath;
            } catch (IOException ex) {
                System.out.println(ex);
                //TODO:warn user somehow
            }
        } else if (Files.isDirectory(localSettingsPath) && Files.isWritable(localSettingsPath)) {
            //settings folder exists, check if I can write there
            return localSettingsPath;
        }

        //TODO: handle this better?
        return null;
    }

    private void readProfiles() {
        //TODO
    }

    public void showRootFrame() {
        rootFrame.pack();
        rootFrame.setVisible(true);
        activeModel.triggerUpdate();
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

    public void setActiveModel(InpPnlModel inpPnlModel) {
        if (inpPnlModel != this.activeModel) {
            this.activeModel = inpPnlModel;
            activeModel.addObserver(inpPnl);
        }
    }

    public InpPnlModel getActiveModel() {
        return activeModel;
    }

}
