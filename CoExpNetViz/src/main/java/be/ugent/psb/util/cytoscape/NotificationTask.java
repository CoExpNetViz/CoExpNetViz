package be.ugent.psb.util.cytoscape;

import java.util.Observable;
import java.util.Observer;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

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



/**
 * Task that simply notifies an observer when run.
 * 
 * Useful to notify when all previous tasks have completed. 
 * You may be tempted to use a TaskObserver instead, but that 
 * only gets notified when ObservableTasks finish, not when regular Tasks finish.
 */
public class NotificationTask extends AbstractTask {

	private Observer observer;
	
	public NotificationTask(Observer observer) {
		super();
		this.observer = observer;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		if (!cancelled)
			observer.update(null, this);
	}
	
}
