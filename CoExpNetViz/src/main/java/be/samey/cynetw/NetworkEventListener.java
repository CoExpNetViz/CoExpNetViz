package be.samey.cynetw;

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
import be.samey.internal.CyAppManager;
import java.util.Set;
import org.cytoscape.model.CyNetwork;
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

    private final CyAppManager cyAppManager;

    public NetworkEventListener(CyAppManager cyAppManager) {
        this.cyAppManager = cyAppManager;
    }

    @Override
    public void handleEvent(NetworkAboutToBeDestroyedEvent natbde) {
        Set<CyNetwork> visibleCevNetworks = cyAppManager.getCyModel().getVisibleCevNetworks();
        visibleCevNetworks.remove(natbde.getNetwork());
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
