package org.isatools.plugins.metabolights.assignments.ui;


import com.sun.awt.AWTUtilities;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.plugins.metabolights.assignments.MetabolomicsResultEditor;
import org.isatools.plugins.metabolights.assignments.io.ConfigurationLoader;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class EditorUI extends AnimatableJFrame {

    public static final float DESIRED_OPACITY = .94f;

    private String currentCellValue;
    private String newCellValue;

//    private JPanel swappableContainer;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("metabolights-fileeditor-package.style").load(
                EditorUI.class.getResource("/dependency-injections/metabolights-fileeditor-package.properties"));

        ResourceInjector.get("filechooser-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/filechooser-package.properties"));
    }

    @InjectedResource
    private Image logo, logoInactive;

    public EditorUI() {
        ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
    }

    public void createGUI() {

        setTitle("Assign metabolites");
        setUndecorated(true);
        setPreferredSize(new Dimension(MetabolomicsResultEditor.WIDTH, MetabolomicsResultEditor.HEIGHT));

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);

        HUDTitleBar titlePanel = new HUDTitleBar(logo, logoInactive);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        DataEntrySheet sheet = new DataEntrySheet(EditorUI.this, loadConfiguration());
        sheet.createGUI();

        add(sheet, BorderLayout.CENTER);

        createSouthPanel();

        pack();
    }

//    private void instantiateOptionPanel() {
//        OptionPane optionPane = new OptionPane();
//        optionPane.createGUI();
//        optionPane.addPropertyChangeListener("createNew", new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
//                DataEntrySheet sheet = new DataEntrySheet(EditorUI.this);
//                sheet.createGUI();
//                swapContainers(sheet);
//            }
//        });
//
//        swappableContainer.add(optionPane);
//    }

    /**
     * Contains the footer panel to allow resizing of the window and a button to save the changes.
     */
    private void createSouthPanel() {
        FooterPanel footer = new FooterPanel(this);
        add(footer, BorderLayout.SOUTH);
    }

    public void setCurrentCellValue(String currentCellValue) {
        this.currentCellValue = currentCellValue;
        this.newCellValue = currentCellValue;
    }

    public String getNewCellValue() {
        return newCellValue;
    }

    public static void main(String[] args) {
        EditorUI ui = new EditorUI();
        ui.createGUI();

        ui.setVisible(true);
    }

    private TableReferenceObject loadConfiguration() {
        ConfigurationLoader loader = new ConfigurationLoader();

        try {
            return loader.loadConfigurationXML();
        } catch (XmlException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    private void swapContainers(Container newContainer) {
//        if (newContainer != null) {
//            swappableContainer.removeAll();
//            swappableContainer.add(newContainer);
//            swappableContainer.repaint();
//            swappableContainer.validate();
//        }
//    }
}
