package be.samey.cynetw;

import be.samey.model.CoreStatus;
import be.samey.model.Model;
import java.util.ArrayList;
import org.cytoscape.model.CyColumn;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;

/**
 *
 * @author sam
 */
public class CevTaskObserver implements TaskObserver {

    private final Model model;

    public CevTaskObserver(Model model) {
        this.model = model;
    }

    @Override
    public void taskFinished(ObservableTask task) {
        System.out.println("task finished");
    }

    @Override
    public void allFinished(FinishStatus finishStatus) {
        System.out.println("all finished");
        CevGroupAttributesLayout layout = (CevGroupAttributesLayout) model.getServices().
            getCyLayoutAlgorithmManager().getLayout(CoreStatus.COMP_LAYOUT_NAME);
        ArrayList<CyColumn> columnList = (ArrayList) model.getCoreStatus().getLastNoaTable().getColumns();
        String groupColumnName = columnList.get(4 + CoreStatus.GROUP_COLUMN).getName();
        String speciesColumnName = columnList.get(4 + CoreStatus.SPECIES_COLUMN).getName();
        TaskIterator ti = layout.createTaskIterator(model.getCoreStatus().getLastCnv(),
            layout.createLayoutContext(),
            CyLayoutAlgorithm.ALL_NODE_VIEWS,
            groupColumnName,
            speciesColumnName);
        model.getServices().getTaskManager().execute(ti);
    }

}
