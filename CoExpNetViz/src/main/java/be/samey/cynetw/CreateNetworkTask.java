package be.samey.cynetw;

import be.samey.model.Model;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author sam
 */
public class CreateNetworkTask extends AbstractTask {

    private final Model model;

    public CreateNetworkTask(Model model) {
        this.model = model;
    }

    @Override
    public void run(TaskMonitor tm) throws Exception {
        CevNetworkBuilder cnb = model.getCoreStatus().getCevNetworkBuilder();
        cnb.createNetworkView(tm);

    }

}
