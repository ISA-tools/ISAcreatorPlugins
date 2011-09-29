package org.isatools.plugins.metabolights.assignments;


import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;
import org.isatools.isacreator.plugins.AbstractPluginSpreadsheetWidget;
import org.isatools.isacreator.plugins.registries.SpreadsheetPluginRegistry;
import org.isatools.plugins.metabolights.assignments.ui.EditorUI;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MetabolomicsResultEditor extends AbstractPluginSpreadsheetWidget {

    private static int WIDTH = 500;
    private static int HEIGHT = 400;

    EditorUI editorUI;

    public MetabolomicsResultEditor() {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EditorUI editorUI = new EditorUI(null);
                editorUI.createGUI();
                editorUI.setLocationRelativeTo(null);
                editorUI.setAlwaysOnTop(true);
                editorUI.setVisible(true);
            }
        });

    }

    @Override
    public void instantiateComponent() {
        editorUI = new EditorUI(null);
        editorUI.createGUI();
        editorUI.setLocationRelativeTo(null);
        editorUI.setAlwaysOnTop(true);
    }

    @Override
    public void hideComponent() {
        editorUI.setVisible(false);
    }

    @Override
    public void showComponent() {
        editorUI.setVisible(true);
    }

    @Override
    public String getCellValue() {
        return "Awesome";
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
        targetColumns.add("Sample Name");

        return targetColumns;
    }
}
