package be.samey.cynetw;

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
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
import be.samey.io.ServerConn;
import be.samey.layout.FamLayout;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.cytoscape.model.CyColumn;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author sam
 */
public class RunAnalysisTask extends AbstractTask {

    private final CyAppManager cyAppManager;
    private ServerConn sc;

    private double progr = 0;

    public RunAnalysisTask(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
    }

    /**
     * Does all work and shows the resulting network to the user in the
     * appropriate layout. (Exceptions thrown during execution are automatically
     * handled by cytoscape and shown in the gui)
     *
     * @param tm
     * @throws Exception
     */
    @Override
    public void run(TaskMonitor tm) throws Exception {
        tm.setTitle(CyModel.PROG_TITLE);

        try {
            runOnServer(tm);
            createNetwork(tm);
            applyLayout(tm);
        } catch (Exception e) {
            //TODO: if any exception occured, roll back whatever has changed
            throw e; //doing this shows the exceptoin to the user in the gui
        }
    }

    void runOnServer(TaskMonitor tm) throws Exception {
        tm.setStatusMessage(CyModel.PROG_TITLE);

        sc = cyAppManager.getServerConn();

        //a threadexecuter is used because it can throw Exceptions from the child thread
        ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

        Future<Void> futureResult = threadExecutor.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                sc.connect();
                return null;
            }
        });

        int i = 0;
        int t = 60;

        while (!futureResult.isDone()) {
            Thread.sleep(1000);
            i++;
            progr = i * 1.0 / (t * CyModel.PROG_CONN_COMPLETE);
            progr = progr < CyModel.PROG_CONN_COMPLETE ? progr : -1.0;
            //TODO: replace fake progress with real progress
            tm.setProgress(progr);
            if (cancelled) {
                sc.stop();
                return;
            }
        }

        //this will throw the child thread's exceptions if there were any
        futureResult.get();
    }

    void createNetwork(TaskMonitor tm) throws Exception {
        tm.setProgress(CyModel.PROG_CONN_COMPLETE);
        tm.setStatusMessage("Creating network");
        cyAppManager.getCevNetworkBuilder().createNetworkView();
    }

    void applyLayout(TaskMonitor tm) {
        tm.setProgress(CyModel.PROG_NETW_COMPLETE);
        tm.setStatusMessage("Applying layout");
        FamLayout layout = (FamLayout) cyAppManager.getCyServices().
            getCyLayoutAlgorithmManager().getLayout(CyModel.COMP_LAYOUT_NAME);
        ArrayList<CyColumn> columnList = (ArrayList) cyAppManager.getCyModel().getLastNoaTable().getColumns();
        String groupColumnName = columnList.get(4 + CyModel.GROUP_COLUMN).getName();
        String speciesColumnName = columnList.get(4 + CyModel.SPECIES_COLUMN).getName();
        TaskIterator ti = layout.createTaskIterator(cyAppManager.getCyModel().getLastCnv(),
            layout.createLayoutContext(),
            CyLayoutAlgorithm.ALL_NODE_VIEWS,
            groupColumnName,
            speciesColumnName);
        insertTasksAfterCurrentTask(ti);
    }

}
