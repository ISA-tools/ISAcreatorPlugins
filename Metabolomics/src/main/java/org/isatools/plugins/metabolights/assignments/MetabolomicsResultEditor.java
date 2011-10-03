package org.isatools.plugins.metabolights.assignments;


import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;
import org.isatools.isacreator.plugins.AbstractPluginSpreadsheetWidget;
import org.isatools.isacreator.plugins.DefaultWindowListener;
import org.isatools.isacreator.plugins.registries.SpreadsheetPluginRegistry;
import org.isatools.plugins.metabolights.assignments.ui.EditorUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

public class MetabolomicsResultEditor extends AbstractPluginSpreadsheetWidget {

    public static int WIDTH = 700;
    public static int HEIGHT = 400;

    EditorUI editorUI;

    public MetabolomicsResultEditor() {
        super();
    }

    @Override
    public void instantiateComponent() {
        editorUI = new EditorUI();
        editorUI.createGUI();
        editorUI.setLocationRelativeTo(null);
        editorUI.setAlwaysOnTop(true);

        editorUI.addPropertyChangeListener("confirm",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        System.out.println("Cell editing confirmed");
                        setCellValue(getCellValue());
                        stopCellEditing();
                    }
                });
        editorUI.addPropertyChangeListener("cancel",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        System.out.println("Cell editing cancelled");
                        setCellValue(getOriginalValue());
                        cancelCellEditing();
                    }
                });

        editorUI.addWindowListener(new DefaultWindowListener() {

            public void windowDeactivated(WindowEvent event) {
                System.out.println("Cell editing cancelled");
                setCellValue(editorUI.getNewCellValue());
                cancelCellEditing();
            }
        });
    }

    @Override
    public void hideComponent() {
        editorUI.setVisible(false);
    }

    @Override
    public void showComponent() {
        System.out.println("Original value of cell is " + getOriginalValue());
        editorUI.setCurrentCellValue(getOriginalValue());
        editorUI.setVisible(true);
    }

    @Override
    public String getCellValue() {
        return editorUI.getNewCellValue();
    }

    @Override
    public void setOnScreenLocation(Point point) {
        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        if ((point.x + MetabolomicsResultEditor.WIDTH) > desktopBounds.width) {
            int difference = (point.x + MetabolomicsResultEditor.WIDTH) -
                    desktopBounds.width;
            point.x = point.x - difference;
        }

        if ((point.y + MetabolomicsResultEditor.HEIGHT) > desktopBounds.height) {
            int difference = (point.y + MetabolomicsResultEditor.HEIGHT) -
                    desktopBounds.height;
            point.y = point.y - difference;
        }


        editorUI.setLocation(point);
    }

    public void registerCellEditor() {
        SpreadsheetPluginRegistry.registerPlugin(this);
    }

    public void deregisterCellEditor() {
        SpreadsheetPluginRegistry.registerPlugin(this);
    }

    public Set<String> targetColumns() {
        Set<String> targetColumns = new HashSet<String>();
        targetColumns.add("Metabolite Assignment File");
        return targetColumns;
    }


}
