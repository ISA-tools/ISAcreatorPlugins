package org.isatools.plugins.metabolights.assignments.ui;


import com.sun.awt.AWTUtilities;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gui.ISAcreator;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class EditorUI extends JFrame {

    static {

        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("metabolights-fileeditor-package.style").load(
                EditorUI.class.getResource("/dependency-injections/metabolights-fileeditor-package.properties"));
    }


    @InjectedResource
    private Image convertIcon, convertIconInactive;

    private ISAcreator isacreatorEnvironment;

    public static final float DESIRED_OPACITY = .93f;

    private JPanel swappableContainer;

    public EditorUI(ISAcreator isacreatorEnvironment) {

        ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);

        this.isacreatorEnvironment = isacreatorEnvironment;
    }

    public void createGUI() {

        setTitle("Assign metabolites");
        setUndecorated(true);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);

        HUDTitleBar titlePanel = new HUDTitleBar(convertIcon, convertIconInactive);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        Container loadingInfo = UIHelper.padComponentVerticalBox(100, new JLabel("Awesome"));

        swappableContainer = new JPanel();
        swappableContainer.add(loadingInfo);
        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        swappableContainer.setPreferredSize(new Dimension(750, 450));

        add(swappableContainer, BorderLayout.CENTER);

        FooterPanel footer = new FooterPanel(this);
        add(footer, BorderLayout.SOUTH);

        pack();
    }


    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }

}
