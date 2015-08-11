package be.ugent.psb.coexpnetviz.io;

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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.work.TaskIterator;

import be.ugent.psb.coexpnetviz.CENVApplication;

class TableReader {

    private final CENVApplication application;

    public TableReader(CENVApplication cyAppManager) {
        this.application = cyAppManager;
    }

    public CyTable readAttributes(Path noaPath, Class<? extends CyIdentifiable> type, CyNetwork network) throws IOException {
    	// Synchronously load table
    	CyTableReader tableReader = application.getCyTableReaderManager().getReader(noaPath.toUri(), null);
    	application.getSynchronousTaskManager().execute(new TaskIterator(tableReader));
    	
    	// Import table into network
    	assert(tableReader.getTables().length == 1);
    	CyColumn joinColumn = network.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).getPrimaryKey();
    	CyTable table = tableReader.getTables()[0];
    	CyRootNetwork rootNetwork = application.getCyRootNetworkManager().getRootNetwork(network);
    	TaskIterator tasks = application.getImportDataTableTaskFactory().createTaskIterator(
    			table, false, false, Collections.singletonList(network), rootNetwork, joinColumn, CyNode.class
    	);
    	application.getSynchronousTaskManager().execute(tasks);
    	
    	return table;
    }

}
