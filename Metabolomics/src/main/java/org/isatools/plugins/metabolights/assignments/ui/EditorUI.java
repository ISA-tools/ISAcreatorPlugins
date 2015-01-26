package org.isatools.plugins.metabolights.assignments.ui;


import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.plugins.metabolights.assignments.IsaCreatorInfo;
import org.isatools.plugins.metabolights.assignments.MetabolomicsResultEditor;
import org.isatools.plugins.metabolights.assignments.io.ConfigurationLoader;
import org.isatools.plugins.metabolights.assignments.model.RemoteInfo;
import org.isatools.plugins.metabolights.assignments.model.RemoteInfo.remoteProperties;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

@SuppressWarnings("restriction")
public class EditorUI extends AnimatableJFrame implements PropertyChangeListener {

	private static Logger logger = Logger.getLogger(EditorUI.class);

	private static final long serialVersionUID = -5036524042579480467L;
    public static final float DESIRED_OPACITY = .94f;
    public static final String PLUGIN_VERSION = "1.7";

    private String currentCellValue;
    private String newCellValue;

    // True when running alone without ISACreator
    private boolean amIAlone = true;

    private IsaCreatorInfo isaCreatorInfo;

    // Progress trigger
    private ProgressTrigger progressTrigger = new ProgressTrigger();
	private static InfiniteProgressPanel progressIndicator;

	public ProgressTrigger getProgressTrigger() {
		return progressTrigger;
	}
    private IsaCreatorInfo getIsaCreatorInfo() {
        if (isaCreatorInfo == null)
            isaCreatorInfo = new IsaCreatorInfo();
        return isaCreatorInfo;
    }

    private boolean version1File = false;

    public boolean isVersion1File() {
        return version1File;
    }

    private void setVersion1File(boolean version1File) {
        this.version1File = version1File;
    }

    /*
    A public method to return the version of this plugin, can be use by the ISAcreator to provide an "update plugin function"
     */
    public static String getPluginVersion() {
        return remoteProperties.VERSION.getDefaultValue();
    }

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

    public void createGUI(String technologyType, String fileName) {

        logger.info("Metabolomics plugin starting up");

        setTitle("Assign metabolites");
        setUndecorated(true);
        setPreferredSize(new Dimension(MetabolomicsResultEditor.WIDTH, MetabolomicsResultEditor.HEIGHT));

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        //AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);

        HUDTitleBar titlePanel = new HUDTitleBar(logo, logoInactive);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        createCentralPanel(technologyType, fileName);

        createSouthPanel();

        configureProgressTrigger();

        checkVersion();

        pack();
    }

    // Configures the progress triggered.
    private void configureProgressTrigger(){

    	progressTrigger.addPropertyChangeListener(this);
    }

    private void createCentralPanel(String technologyType, String fileName) {

        TableReferenceObject tableReferenceObject = loadConfiguration(technologyType, fileName);
        DataEntrySheet sheet = new DataEntrySheet(EditorUI.this,
                getIsaCreatorInfo().addTableRefSampleColumns(tableReferenceObject));  //Add sample columns to the table definition

        //Check if we have a filename first
        if ( fileName.length() >= 2 ){
            String path = getIsaCreatorInfo().getFileLocation();     //Where the ISA archive is stored

            //Do we need to add the path?
            if (fileName.contains(path))
                sheet.setFileName(fileName);
            else
                sheet.setFileName(path + "/" + fileName);

            if (fileName.contains(".csv")){
                setVersion1File(true);
                sheet.setVersion1File(isVersion1File());     //Can in the next version test for the string "_v2_maf.tsv" in the filename
            }

        }

        sheet.createGUI();

        add(sheet, BorderLayout.CENTER);

        // Check if he ISACreator is available
        if (!amIAlone) {

        	// If so, try to load the file (if exists)
        	sheet.loadFile();

        	// Fill sample data....
        	sheet.importSampleData();
        }

    }

    public void confirm(){
    	setVisible(false);
    	firePropertyChange("confirm", "1", "2");

    }

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

    public String getCurrentCellValue(){
    	return this.currentCellValue;
    }

    public String getNewCellValue() {
        return newCellValue;
    }

    public boolean getAmIAlone(){
    	return amIAlone;
    }

    public void setAmIAlone(boolean amIAlone){
    	this.amIAlone = amIAlone;
    }

    public static void main(String[] args) {
        EditorUI ui = new EditorUI();
        ui.createGUI(MetabolomicsResultEditor.MS, null);

        ui.setVisible(true);
    }

    private TableReferenceObject loadConfiguration(String techologyType, String fileName) {
        ConfigurationLoader loader = new ConfigurationLoader();

        try {

            if (fileName.contains(".csv") || isVersion1File()){           //TODO, should not have to test for the filename here, just the isVersionXXXFile methods
                return loader.loadGenericConfig(1,techologyType);
            } else {

                if (techologyType.equals(MetabolomicsResultEditor.MS))
                    return loader.loadConfigurationXML();
                else
                    return loader.loadNMRConfigurationXML();
            }


        } catch (XmlException e) {
            e.printStackTrace();
            logger.error(e.getMessage().toString());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage().toString());
            return null;
        }
    }

	public void propertyChange(PropertyChangeEvent arg0) {
		// For the progress bar
		if (arg0.getSource() instanceof ProgressTrigger){
			propertyChangeProgressTrigger(arg0, (ProgressTrigger) arg0.getSource());

		}

	}

	@SuppressWarnings("static-access")
	private void propertyChangeProgressTrigger(PropertyChangeEvent arg0, ProgressTrigger pt){

		// If the process is starting
		if (arg0.getPropertyName().equals(pt.PROGRESS_START)){

//			progressIndicator = new InfiniteProgressPanel(pt.getProcessDescription());
//
//
//			progressIndicator.setSize(new Dimension(
//											getWidth(),
//											getHeight()));
//			setGlassPane(progressIndicator);
//	        progressIndicator.start();
//	        validate();

			// At this point activate the wait cursor
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		} else if (arg0.getPropertyName().equals(pt.PROGRESS_END)){
//			progressIndicator.stop();
			// Deactivate the wait cursor
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		}
	}

	private void checkVersion(){
        String remoteVersion = RemoteInfo.getProperty(remoteProperties.VERSION);

        if (remoteVersion == null) return;

        if (!getPluginVersion().equals(remoteVersion)){
        	openUrl (RemoteInfo.getProperty(remoteProperties.DOWNLOADURL));
        }else{

        }
	}

	public static void openUrl(String url){
		if( !java.awt.Desktop.isDesktopSupported() ) {

            System.err.println( "Desktop is not supported (fatal)" );
            return;
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {

            System.err.println( "Desktop doesn't support the browse action (fatal)" );
            return;
        }

        try {

            java.net.URI uri = new java.net.URI( url);
            desktop.browse( uri );
        }
        catch ( Exception e ) {

            System.err.println( e.getMessage() );
        }

	}
}
