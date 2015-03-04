package be.samey.internal;

import be.samey.gui.RootPanel;
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
    JFrame rootPanelFrame;

    public CevMenuAction(CyApplicationManager cyApplicationManager, final String menuTitle, Model model) {

        super(menuTitle, cyApplicationManager, null, null);
        setPreferredMenu("Apps");
        this.model = model;
    }

    //invoked on clicking the app in the menu
    @Override
    public void actionPerformed(ActionEvent e) {
        //only make a new window is the app is launched for the first time
        if (model.getGuiStatus().getRootPanelFrame() == null) {
            //create a new window
            rootPanelFrame = new JFrame("Co-expression Network Visualization Tool");
            rootPanelFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            //root panel for the main window, contains the tabs with options
            RootPanel rootPanel = new RootPanel(model);
            rootPanelFrame.setContentPane(rootPanel);
            model.getGuiStatus().setRootPanelFrame(rootPanelFrame);
        } else {
            rootPanelFrame = model.getGuiStatus().getRootPanelFrame();
        }
        //pack and show the window
        rootPanelFrame.pack();
        rootPanelFrame.setVisible(true);
    }
}
