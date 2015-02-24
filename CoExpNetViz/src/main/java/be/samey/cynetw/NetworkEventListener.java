package be.samey.cynetw;

import be.samey.model.Model;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;

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

    }

    @Override
    public void handleEvent(NetworkAddedEvent nae) {

    }

}
