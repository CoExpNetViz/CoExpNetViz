package be.samey.io;

import be.samey.model.Model;
import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class CevVizmapReader {

    public static void readVIZ(Path path, CyNetwork network, Model model) {
        //get required services
        LoadVizmapFileTaskFactory loadVizmapFileTaskFactory = model.getServices().getLoadVizmapFileTaskFactory();
        VisualMappingManager visualMappingManager = model.getServices().getVisualMappingManager();

        //read the file
        File file = path.toFile();
        Set<VisualStyle> vsSet = loadVizmapFileTaskFactory.loadStyles(file);

        //add them to the available styles
        String networkName = network.getRow(network).get("name", String.class);
        int i = 0;
        for (VisualStyle vs : vsSet) {
            //chagne the name to somthing meaningful
            String styleName = String.format("%s_%d", networkName, i);
            vs.setTitle(styleName);
            //makes style available in styles tab of the main Cytoscape window
            visualMappingManager.addVisualStyle(vs);

            //for debugging
            System.out.println("added style: " + styleName);

            i++;
        }
        //remeber that these styles belongs to this network
        model.getCoreStatus().getSubNetworkStylesMap().put(network, vsSet);
    }

}
