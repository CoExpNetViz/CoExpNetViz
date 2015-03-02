package be.samey.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author sam
 */
public class SpeciesEntry extends JPanel {

    JLabel speciesLbl;

    public SpeciesEntry() {
        constructGui();
    }

    private void constructGui() {
        this.speciesLbl = new JLabel("Species:");

        //make layout
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 0, 0, 0);
        c.weightx = 0.5;
        add(speciesLbl, c);
    }

}
