package be.samey.io;

import be.samey.model.Model;
import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class CevVizmapReader {

    public static Set<VisualStyle> readVIZ(Path path, Model model) {
        File file = path.toFile();

        //get required services
        LoadVizmapFileTaskFactory loadVizmapFileTaskFactory = model.getServices().getLoadVizmapFileTaskFactory();

        //The factory method here already adds the visual styles to the
        // visualMappingManager. Don't add it again with
        // visualMappingManager.addVisualStyle() or you will get buggy behaviour
        Set<VisualStyle> vsSet = loadVizmapFileTaskFactory.loadStyles(file);

        return vsSet;
    }

}
