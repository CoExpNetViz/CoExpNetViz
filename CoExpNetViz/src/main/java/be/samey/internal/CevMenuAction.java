package be.samey.internal;

import be.samey.gui.RootTabbedPane;
import be.samey.model.Model;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class CevMenuAction extends AbstractCyAction {

    private Model model;

    public CevMenuAction(CyApplicationManager cyApplicationManager, final String menuTitle, Model model) {

        super(menuTitle, cyApplicationManager, null, null);
        setPreferredMenu("Apps");
        this.model = model;

    }

    //invoked on clicking the app in the menu
    @Override
    public void actionPerformed(ActionEvent e) {
        //the main window to interact with the app
        final JFrame rootPanelFrame = new JFrame("Co-expression Network Visualization Tool");
        //root panel for the main window, contains the tabs with options
        RootTabbedPane rootPanel = new RootTabbedPane(model);
        rootPanelFrame.setContentPane(rootPanel);
        //pack and show the window
        rootPanelFrame.pack();
        rootPanelFrame.setVisible(true);
    }
}
