package be.ugent.psb.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import be.ugent.psb.coexpnetviz.gui.model.JobInputModel;

/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2016 PSB/UGent
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
 * Runnable that runs with the current ClassLoader (getClass().getClassLoader()) as the Thread context ClassLoader 
 */
public abstract class TCCLRunnable implements Runnable {
	
	@Override
	public final void run() {
		final Thread thread = Thread.currentThread();
		final ClassLoader pastLoader = thread.getContextClassLoader();
		final ClassLoader thisLoader = getClass().getClassLoader();
		try { 
			thread.setContextClassLoader(thisLoader);
			runInner(); 
		}
		finally {
			thread.setContextClassLoader(pastLoader);
		}
	}
	
	/**
	 * Code that runs with a different context ClassLoader
	 */
	protected abstract void runInner();
}
