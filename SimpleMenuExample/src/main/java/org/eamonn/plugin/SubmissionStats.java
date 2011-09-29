package org.eamonn.plugin;

import org.apache.log4j.Logger;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.plugins.host.service.PluginMenu;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class SubmissionStats implements PluginMenu {

    private Logger log = Logger.getLogger(SubmissionStats.class.getName());

    private JMenuItem menuItem;

    public SubmissionStats() {
        menuItem = new JMenuItem(new AbstractAction("Simple Plugin") {

            public void actionPerformed(ActionEvent e) {
                ISAcreator isacreator = ApplicationManager.getCurrentApplicationInstance();
                
                JOptionPane.showMessageDialog(null, "Hello, this is a simple plugin!");
            }
        });
    }



    public JMenu removeMenu(JMenu menu) {
        menu.remove(menuItem);
        return menu;
    }

    public JMenu addMenu(JMenu menu) {
        menu.add(menuItem);
        return menu;
    }
}
