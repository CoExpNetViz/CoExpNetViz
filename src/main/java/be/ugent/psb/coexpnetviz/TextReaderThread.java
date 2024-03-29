/*
 * #%L
 * CoExpNetViz
 * %%
 * Copyright (C) 2015 - 2021 PSB/UGent
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

package be.ugent.psb.coexpnetviz;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/*
 * Read an input stream into a String in a separate thread
 */
public class TextReaderThread extends AbstractReaderThread {
	
	private String output;
	
	public TextReaderThread(String name) {
		super(name);
	}
	
	public TextReaderThread(String name, InputStream inputStream) {
		super(name, inputStream);
	}

	@Override
	public void run() {
		try {
			output = IOUtils.toString(getInputStream());
		} catch (IOException e) {
			String msg = "Failed to read " + getName() + ": " + e.toString();
			setCaughtException(new UserException(msg, e));
		}
	}
	
	public String getOutput() throws UserException {
		throwIfCaughtException();
		return output;
	}
	
}
