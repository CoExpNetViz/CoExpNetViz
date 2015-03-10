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
