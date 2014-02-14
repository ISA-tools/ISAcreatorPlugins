package org.isatools.plugins.metabolights.assignments;


import org.apache.log4j.Logger;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.plugins.AbstractPluginSpreadsheetWidget;
import org.isatools.isacreator.plugins.DefaultWindowListener;
import org.isatools.isacreator.plugins.registries.SpreadsheetPluginRegistry;
import org.isatools.plugins.metabolights.assignments.io.ConfigurationLoader;
import org.isatools.plugins.metabolights.assignments.ui.EditorUI;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

public class MetabolomicsResultEditor extends AbstractPluginSpreadsheetWidget {
	
	private static final long serialVersionUID = 1699770979608557533L;
	public static final String NMR = "NMR spectroscopy";
	public static final String MS = "mass spectrometry";
	
	private static Logger logger = Logger.getLogger(MetabolomicsResultEditor.class);
	
	public static int WIDTH = 700;
    public static int HEIGHT = 400;
    private IsaCreatorInfo isaCreatorInfo;

    private EditorUI editorUI;
    private ConfigurationLoader configurationLoader;
    
    @InjectedResource
    private ImageIcon logo;

    public ConfigurationLoader getConfigurationLoader() {
        if (configurationLoader == null)
            configurationLoader = new ConfigurationLoader();
        return configurationLoader;
    }

    public MetabolomicsResultEditor() {
        super();
        ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
        logger.info("MetabolomicsResultEditor constructor invoked");
    }

    @Override
    public void instantiateComponent() {
    	logger.info("instantiateComponent called.");
    	//instantiateComponent(MS, getOriginalValue());
        instantiateComponent(null, getOriginalValue());      //Should not have to send any technology parameters here
    }
    
    public void instantiateComponent(String technologyType, String fileName) {
    	logger.info("Instantiating the metabolomics plugin");
        editorUI = new EditorUI();
        editorUI.setAmIAlone(!isIsaCreatorLoaded());

        logger.info("metabolomics plugin alone?" + editorUI.getAmIAlone());

        if (isIsaCreatorLoaded() && technologyType != null && fileName != null)
            editorUI.createGUI(technologyType, fileName);

        editorUI.setLocationRelativeTo(null);
        editorUI.setAlwaysOnTop(true);

        editorUI    .addPropertyChangeListener("confirm",
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
       	
    	logger.info("Plugin: Checking which configuration file to load");

    	
    	// Check if the component can't be shown
    	if (!canComponentBeShown()){
            displayMessage("You must save your study once before your can assign metabolites");
    		return;
    	}
    	
        try {
            //Is this NMR or MS? Load the appropriate xml file (differs where some columns are hidden)
            if (getTechnology().equalsIgnoreCase(NMR)){
            	logger.info("Plugin: Loading the NMR configuration file");
            	instantiateComponent(NMR, getOriginalValue());
            } else {
            	logger.info("Plugin: Loading the MS configuration file");
            	instantiateComponent(MS, getOriginalValue());
            }

        } catch (Exception e) {
            e.printStackTrace();  
        } 
        
        logger.info("Original value of cell is " + getOriginalValue());
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
        SpreadsheetPluginRegistry.deregisterPlugin(this);
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

    private Assay getAssay(){

        Assay assay = getIsaCreatorInfo().getCurrentAssay();

        if (assay == null)
            return new Assay();

        return assay;
    }

    private String getTechnology(){            //TODO, always returns MS, but NMR on new assay
           //Get the current assay
        Assay assay = getAssay();
        logger.info("The current Assay is "+assay.getIdentifier());

        //Get the Technology type from the assay NMR or MS
        String technology = assay.getTechnologyType();
        logger.info("The current Assay Technology type is "+technology);

        if (technology.contains(":")) {//Ontology reference, like "OBI:NMR spectroscopy"

            String[] localTechnology = technology.split(":");
            technology = localTechnology[1];     //Discard the first entry

        }
        return technology;
    }

    private boolean isIsaCreatorLoaded(){
    	return (getIsaCreatorInfo().getIsacreator() != null);
    }

    private boolean canComponentBeShown(){
    	// If IsaCreator is not available...
    	if (!isIsaCreatorLoaded()){
    		return false;
    	// If the data has not been saved yet,...
    	} else if (getIsaCreatorInfo().getFileLocation() == null){
    		return false;
    	} else {
    		return true;
    	}
    }

    public void displayMessage(String message) {
        final ISAcreator currentInstance = getIsaCreatorInfo().getIsacreator();
            JOptionPane pane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
            pane.setIcon(logo);
            pane.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                        currentInstance.hideSheet();
                    }
                }
            });

            currentInstance.showJDialogAsSheet(pane.createDialog("MetaboLights Plugin"));
    }



}
