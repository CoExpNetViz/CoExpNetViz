package be.samey.cynetw;

import be.samey.io.ServerConn;
import be.samey.model.Model;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author sam
 */
public class RunAnalysisTask extends AbstractTask {

    private final Model model;

    public RunAnalysisTask(Model model){
        this.model = model;
    }
    @Override
    public void run(TaskMonitor tm) throws Exception {
        tm.setTitle("Running analysis");
        ServerConn sc = new ServerConn(model);
        sc.connect();
    }

}
