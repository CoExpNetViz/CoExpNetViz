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
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import be.ugent.psb.coexpnetviz.CENVContext;
import be.ugent.psb.util.Strings;

/**
 * Context menus on CENV network/graph nodes
 */
public class NodeViewContextMenuFactory implements CyNodeViewContextMenuFactory, ActionListener {

    private String plazaMonocotKey = "Plaza Monocots";
    private String plazaDicotKey = "Plaza Dicots";
    private Pattern plazaMonocotPattern;
    private Pattern plazaDicotPattern;
    
    private CENVContext context;

    private CyNetworkView cnv;
    private View<CyNode> view;

    public NodeViewContextMenuFactory(CENVContext context) {
        this.context = context;
        plazaMonocotPattern = Pattern.compile("ORTHO\\d+M\\d+", Pattern.CASE_INSENSITIVE);
        plazaDicotPattern = Pattern.compile("ORTHO\\d+D\\d+", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public CyMenuItem createMenuItem(CyNetworkView cnv, View<CyNode> view) {
        this.cnv = cnv;
        this.view = view;
        JMenuItem menuItem = new JMenuItem("Node info (" + CENVContext.APP_NAME + ")");
        menuItem.addActionListener(this);
        CyMenuItem cyMenuItem = new CyMenuItem(menuItem, 0);
        return cyMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        CyNetwork network = cnv.getModel();
        CyNode node = view.getModel();
        CyRow row = network.getRow(node);

        String families;
        String title;
        if (row.get("type", String.class).equals("family node")) {
        	families = row.get("family", String.class);
        	title = "Family node info";
        }
        else {
        	families = row.get("families", String.class);
        	title = "Bait node info";
        }
        
        if (Strings.isNullOrEmpty(families))
        	families = "<None>";
        
        Frame parent = context.getCySwingApplication().getJFrame();;
        new NodeInfoDialog(families, title, parent);
    }

    private class NodeInfoDialog extends JDialog {

        public NodeInfoDialog(String families, String title, Frame parent) {
            super(parent, title);

            JPanel panel = new JPanel();
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            GridLayout layout = new GridLayout();
            layout.setColumns(1);
            layout.setVgap(5);
            panel.setLayout(layout);
            setContentPane(panel);

            // Link to family pages
            for (String family : families.split("[+]")) {
            	if (plazaMonocotPattern.matcher(family).matches()) {
            		panel.add(createLinkLabel(String.format("http://bioinformatics.psb.ugent.be/plaza/versions/plaza_v3_monocots/gene_families/view/%s", family), family));
            	}
            	else if (plazaDicotPattern.matcher(family).matches()) {
            		panel.add(createLinkLabel(String.format("http://bioinformatics.psb.ugent.be/plaza/versions/plaza_v3_dicots/gene_families/view/%s", family), family));
            	}
            	else {
            		panel.add(new JLabel(family));
            	}
            }
            layout.setRows(panel.getComponentCount());

            // When press escape, dispose window
            ActionListener escListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            };
            getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            // When close window, dispose it
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
            // Show window at mouse
            Point p = MouseInfo.getPointerInfo().getLocation();
            setLocation(p);
            pack();
            setVisible(true);
        }

        private JLabel createLinkLabel(String url, String text) {
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
                context.getOpenBrowser().openURL(url);
            }

        }

    }

}
