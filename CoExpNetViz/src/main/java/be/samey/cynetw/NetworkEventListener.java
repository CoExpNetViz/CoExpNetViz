package be.samey.cynetw;

import be.samey.model.Model;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 *
 * @author sam
 */
public class NetworkEventListener implements NetworkAboutToBeDestroyedListener, NetworkAddedListener {

    private Model model;

    public NetworkEventListener(Model model) {
        this.model = model;
    }

    @Override
    public void handleEvent(NetworkAboutToBeDestroyedEvent natbde) {
        CyNetwork doomedNetwork = natbde.getNetwork();

        //if the network is destroyed, we dont want to keep the visual styles
        // for that network
        for (VisualStyle vs : model.getCoreStatus().getSubNetworkStylesMap().get(doomedNetwork)) {
            model.getServices().getVisualMappingManager().removeVisualStyle(vs);
        }

        //forget the destroyed network and its associated visual styles
        model.getCoreStatus().getSubNetworkStylesMap().remove(doomedNetwork);

    }

    @Override
    public void handleEvent(NetworkAddedEvent nae) {
//        CyNetwork network = nae.getNetwork();
//        network.getRow(network).set(CyNetwork.NAME, Model.APP_NAME + model.getCoreStatus().getNetworkCount());
//
//        //for debugging
//        System.out.println("name changed");
    }

}
