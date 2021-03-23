package be.ugent.psb.coexpnetviz;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2016 PSB/UGent
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

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import be.ugent.psb.coexpnetviz.io.RunJobTask;

/**
 * Runs coexpnetviz-python and creates a network from the result
 */
public class CENVTaskFactory extends AbstractTaskFactory {

	private final CENVContext context;

	public CENVTaskFactory(CENVContext context) {
		super();
		this.context = context;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new RunJobTask(context));
	}

}