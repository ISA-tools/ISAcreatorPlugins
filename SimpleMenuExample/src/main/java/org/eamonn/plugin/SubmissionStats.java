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

/**
 * @author Peter Karich, peat_hal 'at' users 'dot' sourceforge 'dot' net
 */
public class SubmissionStats implements PluginMenu {

    private Logger log = Logger.getLogger(SubmissionStats.class.getName());

    private JMenuItem menuItem;

    public SubmissionStats() {
        menuItem = new JMenuItem(new AbstractAction("Eamonn's Plugin") {

            public void actionPerformed(ActionEvent e) {
                ISAcreator isacreator = ApplicationManager.getCurrentApplicationInstance();
                log.info("ISAcreator environment is null: " + (isacreator == null));
                JOptionPane.showMessageDialog(null, "Hello OSGI, here are the statistics: " +
                        (isacreator == null
                                ? "we have no environment!" :
                                isacreator.getDataEntryEnvironment().getInvestigation() + " studies loaded!"));
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
