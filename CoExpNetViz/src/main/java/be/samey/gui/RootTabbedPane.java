/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.samey.gui;

import be.samey.model.Model;
import java.awt.Dimension;
import javax.swing.JTabbedPane;

/**
 *
 * @author sam
 */
public class RootTabbedPane extends JTabbedPane {

        private Model model;

        TabOneSpecies tabOne;
        TabTwoSpecies tabTwo;
        TabMltSpecies tabMlt;

        public RootTabbedPane(Model model) {
                this.model = model;

                tabOne = new TabOneSpecies(model);
                tabTwo = new TabTwoSpecies(model);
                tabMlt = new TabMltSpecies(model);

                addTab("One Species", tabOne);
                addTab("Two Species", tabTwo);
                addTab("Multiple Species", tabMlt);

                setPreferredSize(new Dimension(500, 700));
        }

}
