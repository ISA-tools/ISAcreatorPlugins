package org.isatools.plugins.validatorconverter;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.plugins.host.service.PluginMenu;
import org.isatools.plugins.validatorconverter.ui.OperatingMode;
import org.isatools.plugins.validatorconverter.ui.ValidateUI;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import java.awt.event.ActionEvent;
import javax.swing.*;

public class Validator implements PluginMenu {

    private JMenuItem menuItem;

    @InjectedResource
    private ImageIcon icon;

    public Validator() {
        ResourceInjector.get("validator-package.style").inject(this);

        menuItem = new JMenuItem(new AbstractAction("Validate ISAtab") {

            public void actionPerformed(ActionEvent e) {
                ISAcreator isacreator = ApplicationManager.getCurrentApplicationInstance();
                ValidateUI validateUI = new ValidateUI(isacreator, OperatingMode.VALIDATE);
                validateUI.createGUI();
                validateUI.setLocationRelativeTo(isacreator);
                validateUI.setAlwaysOnTop(true);
                validateUI.setVisible(true);
                validateUI.validateISAtab();
            }
        });

        menuItem.setIcon(icon);
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
