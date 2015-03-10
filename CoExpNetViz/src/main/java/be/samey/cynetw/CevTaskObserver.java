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

    }

    @Override
    public void allFinished(FinishStatus finishStatus) {

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
