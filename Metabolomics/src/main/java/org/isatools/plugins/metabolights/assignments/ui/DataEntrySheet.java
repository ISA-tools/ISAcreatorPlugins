package org.isatools.plugins.metabolights.assignments.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/10/2011
 *         Time: 16:24
 */
public class DataEntrySheet extends JPanel {

    private Spreadsheet sheet;
    private EditorUI parentFrame;
    private TableReferenceObject tableReferenceObject;

    @InjectedResource
    private ImageIcon saveIcon, saveIconOver, loadIcon, loadIconOver;

    public DataEntrySheet(EditorUI parentFrame, TableReferenceObject tableReferenceObject) {
        ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);

        this.parentFrame = parentFrame;
        this.tableReferenceObject = tableReferenceObject;
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
    }

    public void createGUI() {
        sheet = new Spreadsheet(parentFrame, tableReferenceObject, "");

        createTopPanel();
        add(sheet, BorderLayout.CENTER);
    }

    public void createTopPanel() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(UIHelper.BG_COLOR);

        Box buttonContainer = Box.createHorizontalBox();
        buttonContainer.setBackground(UIHelper.BG_COLOR);

        final JLabel loadButton = new JLabel(loadIcon);
        loadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                loadButton.setIcon(loadIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                loadButton.setIcon(loadIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                loadButton.setIcon(loadIconOver);
            }
        });

        final JLabel saveButton = new JLabel(saveIcon);
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                saveButton.setIcon(saveIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                saveButton.setIcon(saveIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                saveButton.setIcon(saveIconOver);
            }
        });

        buttonContainer.add(saveButton);
        buttonContainer.add(Box.createHorizontalStrut(5));
        buttonContainer.add(loadButton);

        topContainer.add(buttonContainer, BorderLayout.EAST);

        add(topContainer, BorderLayout.NORTH);
    }
}
