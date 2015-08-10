package be.ugent.psb.coexpnetviz.gui;

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

import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import be.ugent.psb.coexpnetviz.CENVApplication;

/**
 *
 * @author sam
 */
public class CENVNodeViewContextMenuFactory implements CyNodeViewContextMenuFactory, ActionListener {

    private String plazaMonocotKey = "Plaza Monocots";
    private String plazaMonocotRegex = "ORTHO\\d+M\\d+";

    private String plazaDicotKey = "Plaza Dicots";
    private String plazaDicotRegex = "ORTHO\\d+D\\d+";

    public CENVNodeViewContextMenuFactory(CENVApplication cyAppManager) {
        this.cyAppManager = cyAppManager;
    }

    private CENVApplication cyAppManager;

    private CyNetworkView cnv;
    private View<CyNode> view;

    @Override
    public CyMenuItem createMenuItem(CyNetworkView cnv, View<CyNode> view) {
        this.cnv = cnv;
        this.view = view;
        JMenuItem menuItem = new JMenuItem(CENVApplication.APP_NAME);
        menuItem.addActionListener(this);
        CyMenuItem cyMenuItem = new CyMenuItem(menuItem, 0);
        return cyMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        CyNetworkTableManager cyNetworkTableManager = cyAppManager.getCytoscapeApplication().getCyNetworkTableManager();

        CyNetwork cn = cnv.getModel();
        CyNode node = view.getModel();
        CyTable cevNodeTable = cyNetworkTableManager.getTable(cn, CyNode.class, CyNetwork.LOCAL_ATTRS);
        ArrayList<CyColumn> columnList = (ArrayList) cevNodeTable.getColumns();
        String famColumnName = columnList.get(4 + CENVModel.FAMILIES_COLUMN).getName();
        String geneColumnName = columnList.get(4 + CENVModel.GENES_COLUMN).getName();
        CyRow row = cn.getRow(node);

        String families = row.get(famColumnName, String.class);
//        String genes = row.get(geneColumnName, String.class);

        Frame parent = CENVApplication.getCytoscapeRootFrame();

        NodeJDialog dialog = new NodeJDialog(parent, "Node info", parseFamilies(families));
    }

    public Map<String, List<String>> parseFamilies(String families) { // not sure if supposed to be public
        HashMap<String, List<String>> famsMap = new LinkedHashMap<String, List<String>>();

        List<String> monocotFams = parseFamilyType(families, plazaMonocotRegex);
        if (!monocotFams.isEmpty()) {
            famsMap.put(plazaMonocotKey, monocotFams);
        }

        List<String> dicotFams = parseFamilyType(families, plazaDicotRegex);
        if (!dicotFams.isEmpty()) {
            famsMap.put(plazaDicotKey, dicotFams);
        }

        return famsMap;
    }

    private List<String> parseFamilyType(String families, String regex) {
        Pattern dicotp = Pattern.compile(regex);
        Matcher dicotm = dicotp.matcher(families);

        List<String> fams = new ArrayList<String>();
        while (dicotm.find()) {
            String match = dicotm.group();
            fams.add(match);
        }

        return fams;
    }

    private class NodeJDialog extends JDialog {

        public NodeJDialog(Frame parent, String title, Map<String, List<String>> famsMap) {
            super(parent, title);
            // set the position of the window
            Point p = MouseInfo.getPointerInfo().getLocation();
            setLocation(p);
//            Point p = parent.getLocationOnScreen();
//            setLocation(p.x + 400, p.y + 200);

            JPanel famPane = new JPanel();
            famPane.setLayout(new BoxLayout(famPane, BoxLayout.PAGE_AXIS));
            setContentPane(famPane);

            // show source of gene family
            for (String key : famsMap.keySet()) {
                famPane.add(new JLabel(key));

                // show gene family linkouts
                for (String genfam : famsMap.get(key)) {
                    String url = null;
                    if (key.equals(plazaMonocotKey)) {
                        url = String.format("http://bioinformatics.psb.ugent.be/plaza/versions/plaza_v3_monocots/gene_families/view/%s", genfam);
                    }
                    if (key.equals(plazaDicotKey)) {
                        url = String.format("http://bioinformatics.psb.ugent.be/plaza/versions/plaza_v3_dicots/gene_families/view/%s", genfam);
                    }
                    famPane.add(makeLinkoutLbl(genfam, url));
                }
            }

            //close window on ESC keypress
            KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
            InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(stroke, "ESCAPE");
            rootPane.getActionMap().put("ESCAPE", new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    setVisible(false);
                    dispose();
                }

            });

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setVisible(true);
        }

        private JLabel makeLinkoutLbl(String text, String url) {
            JLabel hrefLabel;
            if (url != null) {
                hrefLabel = new JLabel(String.format("<html><a href=%s>%s</a></html>", text, text));
                hrefLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                hrefLabel.addMouseListener(new LinkMouseListener(url));
            } else {
                hrefLabel = new JLabel(text);
            }
            return hrefLabel;
        }

        private class LinkMouseListener extends MouseAdapter {

            private String url;

            public LinkMouseListener(String url) {
                this.url = url;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cyAppManager.getCytoscapeApplication().getOpenBrowser().openURL(url);
            }

        }

    }

}
