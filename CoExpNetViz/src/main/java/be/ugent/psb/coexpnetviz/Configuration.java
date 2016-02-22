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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import be.ugent.psb.coexpnetviz.gui.jobinput.JobInputPreset;

/**
 * CoExpNetViz Configuration
 */
@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Configuration {

	@XmlElementWrapper
	@XmlElement(name="preset")
	List<JobInputPreset> presets;
	
	String lastUsedPreset;
	
	public Configuration() {
		presets = new ArrayList<>();
	}
	
	public List<JobInputPreset> getPresets() {
		return presets;
	}
	
	public JobInputPreset getLastUsedPreset() {
		for (JobInputPreset preset : presets) {
			if (preset.getName().equals(lastUsedPreset)) {
				return preset;
			}
		}
		return null;
	}
	
	public String getLastUsedPresetName() {
		return lastUsedPreset;
	}

	public void setLastUsedPresetName(String lastUsedPreset) {
		if (lastUsedPreset != null) {
			JobInputPreset preset = new JobInputPreset();
			preset.setName(lastUsedPreset);
			assert presets.contains(preset);
		}
		this.lastUsedPreset = lastUsedPreset;
	}

}
