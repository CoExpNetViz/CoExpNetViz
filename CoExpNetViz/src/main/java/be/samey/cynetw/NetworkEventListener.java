package be.samey.cynetw;

import be.samey.model.Model;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.vizmap.events.VisualStyleSetEvent;
import org.cytoscape.view.vizmap.events.VisualStyleSetListener;

/**
 * Not used at the moment, but can come in handy later (and for testing things)
 * 
 * @author sam
 */
public class NetworkEventListener implements
    NetworkAboutToBeDestroyedListener,
    NetworkAddedListener,
    NetworkViewAddedListener,
    VisualStyleSetListener {

    private final Model model;

    public NetworkEventListener(Model model) {
        this.model = model;
    }

    @Override
    public void handleEvent(NetworkAboutToBeDestroyedEvent natbde) {

    }

    @Override
    public void handleEvent(NetworkAddedEvent nae) {

    }

    @Override
    public void handleEvent(NetworkViewAddedEvent nvae) {

    }

    @Override
    public void handleEvent(VisualStyleSetEvent vsse) {

    }

}
