package be.samey.io;

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
