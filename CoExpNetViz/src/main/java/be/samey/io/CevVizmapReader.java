package be.samey.io;

import be.samey.model.Model;
import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class CevVizmapReader {

    public static void readVIZ(Path path, Model model) {
        //get required services
        LoadVizmapFileTaskFactory loadVizmapFileTaskFactory = model.getServices().getLoadVizmapFileTaskFactory();
        VisualMappingManager visualMappingManager = model.getServices().getVisualMappingManager();

        //read the file
        File file = path.toFile();
        Set<VisualStyle> vsSet = loadVizmapFileTaskFactory.loadStyles(file);

        //add them to the available styles
        for (VisualStyle vs : vsSet) {
            //makes style available in styles tab of the main Cytoscape window
            visualMappingManager.addVisualStyle(vs);

        }
    }

}
