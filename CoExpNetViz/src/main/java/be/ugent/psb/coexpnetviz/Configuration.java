package be.ugent.psb.coexpnetviz;

import java.nio.file.Path;
import java.nio.file.Paths;

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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.ugent.psb.coexpnetviz.gui.jobinput.JobInputPreset;
import be.ugent.psb.util.jaxb.PathAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * CoExpNetViz Configuration
 */
@XmlRootElement
@XmlAccessorType (XmlAccessType.NONE)
public class Configuration {

	@XmlElementWrapper
	@XmlElement(name="preset")
	List<JobInputPreset> presets;
	
	@XmlElement
	String lastUsedPreset;
	
	ObjectProperty<Path> lastBrowsedPath;
	
	public Configuration() {
		presets = new ArrayList<>();
		lastBrowsedPath = new SimpleObjectProperty<>(Paths.get(System.getProperty("user.home")));
		assert lastBrowsedPath.get() != null;
	}
	
	public List<JobInputPreset> getPresets() {
		return presets;
	}
	
	public void setPresets(List<JobInputPreset> presets) {
		this.presets = presets;
	}
	
	public JobInputPreset getLastUsedPreset() {
		for (JobInputPreset preset : presets) {
			if (preset.getName().equals(lastUsedPreset)) {
				return preset;
			}
		}
		return null;
	}

	public void setLastUsedPreset(JobInputPreset preset) {
		if (preset == null) {
			// unset
			lastUsedPreset = null;
		}
		else {
			// set
			assert presets.contains(preset);
			lastUsedPreset = preset.getName();
		}
	}

	@XmlJavaTypeAdapter(PathAdapter.class)
	@XmlElement
	public Path getLastBrowsedPath() {
		return lastBrowsedPath.get();
	}

	public void setLastBrowsedPath(Path lastBrowsedPath) {
		assert lastBrowsedPath != null;
		this.lastBrowsedPath.set(lastBrowsedPath);
	}
	
	public ObjectProperty<Path> lastBrowsedPathProperty() {
		return lastBrowsedPath;
	}

}
