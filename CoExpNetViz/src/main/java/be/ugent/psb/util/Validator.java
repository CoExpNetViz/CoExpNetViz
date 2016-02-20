package be.ugent.psb.util;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

public class Validator {
	
	String name;
	
	public Validator() {
	}
	
	public Validator(String name) {
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	private void throwTantrum(String reason) throws ValidationException {
		throw new ValidationException(name + ": " + reason);
	}
	
	public Path ensurePath(String value) throws ValidationException {
		try {
        	Path path = Paths.get(value);
        	return path;
    	}
    	catch (InvalidPathException ex) {
    		throwTantrum("given path has syntax error(s)");
    		return null; // this never happens, but compiler did not understand otherwise
    	}
	}
	
	public void ensureReadable(Path path) throws ValidationException {
		if (!Files.isReadable(path)) {
			throwTantrum("given file is not readable or does not exist");
		}
	}
	
	public void ensureIsRegularFile(Path path) throws ValidationException {
		if (!Files.isRegularFile(path)) {
			throwTantrum("given file is not a regular file");
		}
	}
	
	public void ensureIsDirectory(Path path) throws ValidationException {
		if (!Files.isDirectory(path)) {
			throwTantrum("given file is not a directory");
		}
	}
	
	public String ensureNotEmpty(String value) throws ValidationException {
		if (value == null || value.trim().isEmpty()) {
			throwTantrum("may not be empty");
		}
		return value.trim();
	}
	
	public void ensureInRange(double value, double min, double max) throws ValidationException {
		if (value >= min && value <= max) {
			throwTantrum("must lie between " + min + " and " + max + " (inclusive)");
		}
	}

}
