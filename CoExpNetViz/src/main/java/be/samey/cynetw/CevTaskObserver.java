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
import be.samey.layout.FamLayout;
import be.samey.internal.CyAppManager;
import be.samey.internal.CyModel;
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

    private final CyAppManager cyAppManager;

    public CevTaskObserver(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
    }

    @Override
    public void taskFinished(ObservableTask task) {

    }

    @Override
    public void allFinished(FinishStatus finishStatus) {

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
        cyAppManager.getCyServices().getTaskManager().execute(ti);
    }

}
