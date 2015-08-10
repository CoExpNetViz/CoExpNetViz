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

import java.nio.file.Path;
import java.util.Set;
import org.cytoscape.view.vizmap.VisualStyle;

import be.ugent.psb.coexpnetviz.CENVApplication;

public class VizmapReader {

    private final CENVApplication application;

    public VizmapReader(CENVApplication cyAppManager) {
        this.application = cyAppManager;
    }

    public Set<VisualStyle> readVIZ(Path vizmapPath) {
        // Note: the factory has already called visualMappingManager.addVisualStyle() for us
        return application.getCytoscapeApplication().getLoadVizmapFileTaskFactory().loadStyles(vizmapPath.toFile());
    }

}
