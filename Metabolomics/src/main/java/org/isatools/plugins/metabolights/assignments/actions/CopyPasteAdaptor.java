package org.isatools.plugins.metabolights.assignments.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.isatools.isacreator.spreadsheet.Spreadsheet;

public class CopyPasteAdaptor implements ActionListener {

    private Spreadsheet spreadsheet;

    public CopyPasteAdaptor(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;

        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,
                ActionEvent.CTRL_MASK, false);

        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V,
                ActionEvent.CTRL_MASK, false);
        // Identifying the Paste KeyStroke user can modify this
        //to copy on some other Key combination.
        spreadsheet.getTable().registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
        spreadsheet.getTable().registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().compareTo("Copy") == 0) {
            spreadsheet.getSpreadsheetFunctions().copy();
        }

        if (actionEvent.getActionCommand().compareTo("Paste") == 0) {

            int startRow = (spreadsheet.getTable().getSelectedRows())[0];
            int startCol = (spreadsheet.getTable().getSelectedColumns())[0];

            spreadsheet.getSpreadsheetFunctions().paste(startRow, startCol, true);
        }
    }
}