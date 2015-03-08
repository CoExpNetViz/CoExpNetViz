package be.samey.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 *
 * @author sam
 */
public class BrowseAl implements ActionListener {

    public static final int DIRECTORY = JFileChooser.DIRECTORIES_ONLY;
    public static final int FILE = JFileChooser.FILES_ONLY;
    
    private final JComponent parent;
    private final JTextField target;
    private final String windowTitle;
    private final int mode;

    public BrowseAl(JComponent parent, JTextField target, String windowTitle, int mode) {
        this.parent = parent;
        this.target = target;
        this.windowTitle = windowTitle;
        this.mode = mode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Execute when button is pressed
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setFileSelectionMode(mode);
        chooser.setDialogTitle(windowTitle);
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            target.setText(chooser.getSelectedFile().toString());
        }
    }
}
