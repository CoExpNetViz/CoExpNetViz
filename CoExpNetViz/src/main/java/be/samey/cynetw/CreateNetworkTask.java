/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.cynetw;

import be.samey.model.Model;
import java.nio.file.Path;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author sam
 */
public class CreateNetworkTask extends AbstractTask {

    private Model model;


    public CreateNetworkTask(Model model) {
        this.model = model;
    }

    @Override
    public void run(TaskMonitor tm) throws Exception {
        tm.setTitle("Reading network files");
        CevNetworkBuilder cnb = model.getCoreStatus().getCevNetworkBuilder();
        cnb.createNetworkView();

        model.getCoreStatus().notifyNetworkCreated();
    }

}
