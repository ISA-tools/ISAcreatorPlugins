package org.isatools.plugins.metabolights.assignments;


import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.plugins.AbstractPluginSpreadsheetWidget;
import org.isatools.isacreator.plugins.DefaultWindowListener;
import org.isatools.isacreator.plugins.registries.SpreadsheetPluginRegistry;
import org.isatools.plugins.metabolights.assignments.io.ConfigurationLoader;
import org.isatools.plugins.metabolights.assignments.ui.DataEntrySheet;
import org.isatools.plugins.metabolights.assignments.ui.EditorUI;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MetabolomicsResultEditor extends AbstractPluginSpreadsheetWidget {
	
	private static final long serialVersionUID = 1699770979608557533L;
	
	private static Logger logger = Logger.getLogger(MetabolomicsResultEditor.class);
	
	public static int WIDTH = 700;
    public static int HEIGHT = 400;
    private IsaCreatorInfo isaCreatorInfo;

    private EditorUI editorUI;
    private DataEntrySheet dataEntrySheet;
    private ConfigurationLoader configurationLoader;

    public ConfigurationLoader getConfigurationLoader() {
        if (configurationLoader == null)
            configurationLoader = new ConfigurationLoader();
        return configurationLoader;
    }

    public DataEntrySheet getDataEntrySheet() {
        return dataEntrySheet;
    }

    public MetabolomicsResultEditor() {
        super();
    }

    @Override
    public void instantiateComponent() {
    	logger.info("Instantiating the metabolomics plugin");
        editorUI = new EditorUI();
        editorUI.createGUI();
        editorUI.setLocationRelativeTo(null);
        editorUI.setAlwaysOnTop(true);

        editorUI.addPropertyChangeListener("confirm",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        logger.info("Cell editing confirmed");
                        setCellValue(getCellValue());
                        stopCellEditing();
                    }
                });
        editorUI.addPropertyChangeListener("cancel",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        logger.info("Cell editing cancelled");
                        setCellValue(getOriginalValue());
                        cancelCellEditing();
                    }
                });

        editorUI.addWindowListener(new DefaultWindowListener() {

            public void windowDeactivated(WindowEvent event) {
                logger.info("Cell editing cancelled");
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
    	
        logger.info("Original value of cell is " + getOriginalValue());
        editorUI.setCurrentCellValue(getOriginalValue());
        editorUI.setVisible(true);
    	
    	logger.info("Plugin: Checking which configuration file to load");

        try {
            //Is this NMR or MS? Load the appropriate xml file (differs where some columns are hidden)
            if (getTechnology().equalsIgnoreCase("NMR")){
            	logger.info("Plugin: Loading the NMR configuration file");
                //getDataEntrySheet().loadFile(getConfigurationLoader().loadNMRConfigurationXML());
            } else {
            	logger.info("Plugin: Loading the MS configuration file");
                //getDataEntrySheet().loadFile(getConfigurationLoader().loadConfigurationXML());
            }

        } catch (Exception e) {
            e.printStackTrace();  
        } 
   
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
        //targetColumns.add("Sample Name");
        return targetColumns;
    }


    public IsaCreatorInfo getIsaCreatorInfo() {
        if (isaCreatorInfo == null)
            isaCreatorInfo = new IsaCreatorInfo();
        return isaCreatorInfo;
    }

    public void setIsaCreatorInfo(IsaCreatorInfo isaCreatorInfo) {
        this.isaCreatorInfo = isaCreatorInfo;
    }

    private Assay getAssay(){

        Assay assay = getIsaCreatorInfo().getCurrentAssay();

        if (assay == null)
            return new Assay();

        return assay;
    }

    private String getTechnology(){
           //Get the current assay
        Assay assay = getAssay();
        logger.info("The current Assay is "+assay.getIdentifier());

        //Get the Technology type from the assay NMR or MS
        String technology = assay.getTechnologyType();
        logger.info("The current Assay Technology type is "+technology);

        return technology;
    }

}
