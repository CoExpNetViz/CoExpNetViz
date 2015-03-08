package be.samey.internal;

import be.samey.cynetw.CevGroupAttributesLayout;
import be.samey.cynetw.NetworkEventListener;
import be.samey.model.Model;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.events.VisualStyleSetListener;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import static org.cytoscape.work.ServiceProperties.*;

/**
 * This is the entry point for the CoExpNetViz plugin for Cytoscape 3. Here the
 * necessary services are called and bundled into the {@link Model} to be used
 * throughout the app. The services this app offers are created here and
 * registered within the {@link BundleContext}.
 *
 * @author Sam De Meyer
 */
public class CyActivator extends AbstractCyActivator {

    @Override
    public void start(BundleContext context) throws Exception {

        //get services
        CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
        CyNetworkFactory cyNetworkFactory = getService(context, CyNetworkFactory.class);
        CyNetworkManager cyNetworkManager = getService(context, CyNetworkManager.class);
        CyRootNetworkManager cyRootNetworkManager = getService(context, CyRootNetworkManager.class);
        CyTableFactory cyTableFactory = getService(context, CyTableFactory.class);
        CyNetworkTableManager CyNetworkTableManager = getService(context, CyNetworkTableManager.class);
        LoadVizmapFileTaskFactory loadVizmapFileTaskFactory = getService(context, LoadVizmapFileTaskFactory.class);
        VisualMappingManager visualMappingManager = getService(context, VisualMappingManager.class);
        CyNetworkViewFactory cyNetworkViewFactory = getService(context, CyNetworkViewFactory.class);
        CyNetworkViewManager cyNetworkViewManager = getService(context, CyNetworkViewManager.class);
        CyLayoutAlgorithmManager cyLayoutAlgorithmManager = getService(context, CyLayoutAlgorithmManager.class);
        TaskManager taskManager = getService(context, TaskManager.class);
        UndoSupport undoSupport = getService(context, UndoSupport.class);

        //add them to the model's services
        Model model = new Model();
        model.getServices().setCyApplicationManager(cyApplicationManager);
        model.getServices().setCyNetworkFactory(cyNetworkFactory);
        model.getServices().setCyNetworkManager(cyNetworkManager);
        model.getServices().setCyRootNetworkManager(cyRootNetworkManager);
        model.getServices().setCyTableFactory(cyTableFactory);
        model.getServices().setCyNetworkTableManager(CyNetworkTableManager);
        model.getServices().setLoadVizmapFileTaskFactory(loadVizmapFileTaskFactory);
        model.getServices().setVisualMappingManager(visualMappingManager);
        model.getServices().setCyNetworkViewFactory(cyNetworkViewFactory);
        model.getServices().setCyNetworkViewManager(cyNetworkViewManager);
        model.getServices().setCyLayoutAlgorithmManager(cyLayoutAlgorithmManager);
        model.getServices().setTaskManager(taskManager);
        model.getServices().setUndoSupport(undoSupport);

        //create the menu action (menu entry for the app)
        CevMenuAction action = new CevMenuAction(cyApplicationManager, Model.APP_NAME, model);

        //register OSGi services
        registerAllServices(context, action, new Properties());

        //network event service
        NetworkEventListener networkEventListener = new NetworkEventListener(model);
        registerService(context, networkEventListener, NetworkAboutToBeDestroyedListener.class, new Properties());
        registerService(context, networkEventListener, NetworkAddedListener.class, new Properties());
        registerService(context, networkEventListener, NetworkViewAddedListener.class, new Properties());
        registerService(context, networkEventListener, VisualStyleSetListener.class, new Properties());

        //cev layout service
        CevGroupAttributesLayout cgal = new CevGroupAttributesLayout(undoSupport);
        Properties cgalProperties = new Properties();
        cgalProperties.setProperty("preferredTaskManager", "menu");
        cgalProperties.setProperty(TITLE, cgal.toString());
        cgalProperties.setProperty(MENU_GRAVITY, "10.65");
        registerService(context, cgal, CyLayoutAlgorithm.class, cgalProperties);

        //for debugging: print message if the app started succesfully
        System.out.println(Model.APP_NAME + " started succesfully");
    }
}
