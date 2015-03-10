package be.samey.io;

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
import java.io.InputStream;
import java.util.Set;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class CevVizmapReader {

    public static Set<VisualStyle> readVIZ(Model model) {
        ClassLoader classLoader = Model.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream("vizmap/CevStyle.xml");

        //get required services
        LoadVizmapFileTaskFactory loadVizmapFileTaskFactory = model.getServices().getLoadVizmapFileTaskFactory();

        //The factory method here already adds the visual styles to the
        // visualMappingManager. Don't add it again with
        // visualMappingManager.addVisualStyle() or you will get buggy behaviour
        Set<VisualStyle> vsSet = loadVizmapFileTaskFactory.loadStyles(is);

        return vsSet;
    }

}
