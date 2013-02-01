package org.isatools.plugins.metabolights.assignments.ui;

import org.apache.log4j.Logger;
import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.spreadsheet.*;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.plugins.metabolights.assignments.IsaCreatorInfo;
import org.isatools.plugins.metabolights.assignments.TableCellListener;
import org.isatools.plugins.metabolights.assignments.actions.AutoCompletionAction;
import org.isatools.plugins.metabolights.assignments.actions.CellToAutoComplete;
import org.isatools.plugins.metabolights.assignments.actions.CopyPasteAdaptor;
import org.isatools.plugins.metabolights.assignments.actions.SelectionRunner;
import org.isatools.plugins.metabolights.assignments.io.ConfigurationLoader;
import org.isatools.plugins.metabolights.assignments.io.FileLoader;
import org.isatools.plugins.metabolights.assignments.io.FileWriter;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/10/2011
 *         Time: 16:24
 */
public class DataEntrySheet extends JPanel {
	
	private static final long serialVersionUID = -7317091603657433515L;

	private static Logger logger = Logger.getLogger(DataEntrySheet.class);

    private Spreadsheet sheet;
    private EditorUI parentFrame;

    private TableReferenceObject tableReferenceObject;

    private ConfigurationLoader configurationLoader;

    private String fileName = null;
    private String fileNameNoPath = null;
    private JLabel info;
    
    private boolean autocomplete= true;
    private boolean forceAutoComplete=false;
    
    public static String TAXID = "taxid";
    public static String SPECIES = "species";
    public static String SPECIEFIELD = "Characteristics[organism]";
    private boolean forceSpecieImport = false;

    private boolean version1File = false;

    private IsaCreatorInfo isaCreatorInfo;


    public TableReferenceObject getTableReferenceObject() {
        // if (isVersion1File()) {   //Return old style filename
        //     String technologyType = getIsaCreatorInfo().getCurrentAssay().getTechnologyType();
        //     tableReferenceObject = getConfigurationLoader().loadGenericConfig(1,technologyType);
        // }
        return tableReferenceObject;
    }

    public void setTableReferenceObject(TableReferenceObject tableReferenceObject) {
        this.tableReferenceObject = tableReferenceObject;
    }

    private boolean isVersion1File() {
        return version1File;
    }

    public void setVersion1File(boolean version1File) {
        this.version1File = version1File;
    }
	
	public boolean isForceAutoComplete() {
		return forceAutoComplete;
	}

	public void setForceAutoComplete(boolean forceAutoComplete) {
		this.forceAutoComplete = forceAutoComplete;
	}

    private IsaCreatorInfo getIsaCreatorInfo() {
        if (isaCreatorInfo == null)
            isaCreatorInfo = new IsaCreatorInfo();
        return isaCreatorInfo;
    }

    private ConfigurationLoader getConfigurationLoader() {
        if (configurationLoader == null)
            configurationLoader = new ConfigurationLoader();
        return configurationLoader;
    }

    @InjectedResource
    private ImageIcon saveIcon, saveIconOver, loadIcon, loadIconOver, 
    					okIcon,	okIconOver, importSpecieIcon, importSpecieIconOver,
    					getIdIcon, getIdIconOver, selectedIcon, unSelectedIcon;

    public DataEntrySheet(EditorUI parentFrame, TableReferenceObject referenceObject) {
        ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
        this.parentFrame = parentFrame;
        setTableReferenceObject(referenceObject);
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
    }

    public Spreadsheet getSheet(){
    	return sheet;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName(){
        // if we do not have the property already set or of the file is just the base directory
        if (this.fileName == null || this.fileName.endsWith("/")){    //TODO, use isDirectory()
            calculateFileName();     //Use the new style tsv file
        }

        return fileName;
    }



	public void createGUI() {

        sheet = new Spreadsheet(parentFrame, getIsaCreatorInfo().addTableRefSampleColumns(getTableReferenceObject()), "");  // Add the sample columns to the definition
        createTopPanel();
        add(getIsaCreatorInfo().addSpreadsheetSampleColumns(sheet), BorderLayout.CENTER);  // Add the sample columns to the spreadsheet
        createBottomPanel();
        
        // Add a listener to the changes of the table
        addChangesListener();
        
        // Add custom cell editors only if the latest fileformat is active
        if (!isVersion1File())
            addCustomCellEditors();
    }
	
	private void addCustomCellEditors(){

		// Add a metabolite cell editor to Description column
        addMetaboliteCellEditorToColumn(AutoCompletionAction.DESCRIPTION_COL_NAME);
		
		// Add a metabolite cell editor to Formula column
		addMetaboliteCellEditorToColumn(AutoCompletionAction.FORMULA_COL_NAME);

        int colindex = 0;

        // Add Link style cell to Identifiers
        colindex  = sheet.getTable().getColumnModel().getColumnIndex(AutoCompletionAction.IDENTIFIER_COL_NAME);


        TableColumn col = sheet.getTable().getColumnModel().getColumn(colindex);

		// non-editing state
		MetaboliteLinkCellRenderer mlcr = new MetaboliteLinkCellRenderer(); 
		col.setCellRenderer(mlcr); 
		sheet.getTable().addMouseListener(mlcr);

		
	}

	private void addMetaboliteCellEditorToColumn(String columnName){
		
		int colindex  = sheet.getTable().getColumnModel().getColumnIndex(columnName);
		TableColumn col = sheet.getTable().getColumnModel().getColumn(colindex);
		col.setCellEditor(new MetaboliteCellEditor(this));

		// non-editing state
		col.setCellRenderer(new MetaboliteCellRenderer()); 
		
	}

    public void addChangesListener(){
    	Action action = new AbstractAction()
    	{
    		public void actionPerformed(ActionEvent e)
    	    {

                if ( isVersion1File() ) return;  //TODO, determine if this should be supported.  Quick fix, only new versions of the id file can use the NCBI PubChem lookup

                if ( !autocomplete ) return;  //Have the user turned off the autocomplete
    			
    	        TableCellListener tcl = (TableCellListener)e.getSource();

    	        // Create an auto-completion action and invoke the actionperformed method
                AutoCompletionAction aca = new AutoCompletionAction();
                
                // Add a progress trigger to the action
                aca.setProgressTrigger(parentFrame.getProgressTrigger());

                aca.actionPerformed(new ActionEvent(new CellToAutoComplete(tcl.getTable(), tcl.getRow(), tcl.getColumn(), forceAutoComplete),1,"CELL_CHANGED"));
                
                // Force will be true after selecting a metabolite from the list of the MetaboliteCellEditor,
                // In this case we only want to force it once
                forceAutoComplete = false;
                
                // Repaint the table
                sheet.getTable().repaint();
                
    	    }
    	};

    	TableCellListener tcl = new TableCellListener(sheet.getTable(), action);
    	//sheet.getTable().setBackground(Color.RED);
    	
    	// Add copypaste actionListener
    	new CopyPasteAdaptor(sheet);
    	
    	// Listen to Paste events...
    	sheet.registerCopyPasteObserver(new CopyPasteObserver() {
            public void notifyOfEvent(SpreadsheetEvent event) {
				
            	//                if(event == SpreadsheetEvent.COPY) {
				//                    System.out.println("Copy event recorded");
				//                    
            	//                }
                
            	// If event is paste
                if(event == SpreadsheetEvent.PASTE) {
                	// If autocomplete deactivated...exit
                	if (!autocomplete) return;
                	
                	//Auto-complete
                    Action getIds = new SelectionRunner(sheet.getTable(), new AutoCompletionAction(),parentFrame.getProgressTrigger());
                    
                    getIds.actionPerformed(null);
                    
                    sheet.getTable().repaint();
                    
                }
            }
        });
    	
    }
    
    public void createBottomPanel(){
    	JPanel bottomPannel = new JPanel(new BorderLayout());
    	bottomPannel.setBackground(UIHelper.BG_COLOR);
    	
    	Box buttonContainer = Box.createHorizontalBox();
    	buttonContainer.setBackground(UIHelper.BG_COLOR);
    	
    	JLabel file = new JLabel();
    	file.setBackground(UIHelper.BG_COLOR);
    	
    	// Not available in the first load (without ISACreator) 
//    	if (!parentFrame.getAmIAlone()){
//    		file.setText(getFileName());
//    	}
    	
    	bottomPannel.add(file);
    	
    	final JLabel okButton = new JLabel(okIcon);
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                
            	// Try to force to save the current cell if it is in "edition mode".
            	SpreadsheetUtils.stopCellEditingInTable(sheet.getTable());
            	
            	saveFile();
                if (parentFrame.getCurrentCellValue().isEmpty())
            	    parentFrame.setCurrentCellValue(fileNameNoPath);         //fileName

            	parentFrame.confirm();
            }
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                okButton.setIcon(okIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                okButton.setIcon(okIconOver);
            }
        });
        
        buttonContainer.add(okButton);

        bottomPannel.add(buttonContainer, BorderLayout.EAST);

        add(bottomPannel, BorderLayout.SOUTH);

    	
    }

    public void createTopPanel() {
        
    	// Create the top container
    	JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(UIHelper.BG_COLOR);
        // Create a button container to add buttons, etc.
        Box buttonContainer = Box.createHorizontalBox();
        buttonContainer.setBackground(UIHelper.BG_COLOR);

        // Add an info label
        info = new JLabel();
        buttonContainer.add(info);

        // Add a checkbox for activate/de-activate auto-completion.
        final JCheckBox autocompleteCheck = new JCheckBox();
        autocompleteCheck.setText("Automatic metabolite search:");
        autocompleteCheck.setToolTipText("Activate automatic metabolite search if you want to have related cells updated after a cell is edited");
        autocompleteCheck.setIcon(unSelectedIcon);
        autocompleteCheck.setSelectedIcon(selectedIcon);
        autocompleteCheck.setSelected(autocomplete);
        autocompleteCheck.setHorizontalTextPosition(SwingConstants.LEFT);
              
        // Add a listener to the state changed
        autocompleteCheck.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent arg0) {
				// Change the autocomplete variable.
        		autocomplete = autocompleteCheck.isSelected();
        		//autocompleteCheck.setText(autocomplete?"Deactivate Metabolite search:":"Activate Metabolite search:");
			}
         });

        if (isVersion1File()){     //True if version 1, false if any new version
            autocompleteCheck.setToolTipText(" NB! ONLY works with newer versions of the metabolite identification file. Please contact metabolights-help@ebi.ac.uk if you want an updated version for your study");
            autocompleteCheck.setEnabled(false);
        }



        // Add the check box to the container
        buttonContainer.add(Box.createHorizontalStrut(5));
    	buttonContainer.add (autocompleteCheck);

        
        // Add a button to resolve IDs based on descriptions.
        final JLabel getIdButton = new JLabel(getIdIcon);
        getIdButton.setToolTipText("Autocomplete other columns based on the current column looking up in PubChem.");
        getIdButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                getIdButton.setIcon(getIdIcon);
                
                Action getIds = new SelectionRunner(sheet.getTable(), new AutoCompletionAction(),parentFrame.getProgressTrigger());
               
                getIds.actionPerformed(null);
                
                sheet.getTable().repaint();

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                getIdButton.setIcon(getIdIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                getIdButton.setIcon(getIdIconOver);
            }
        });
                        
        // Add the button to the container...
        buttonContainer.add(Box.createHorizontalStrut(5));
    	buttonContainer.add (getIdButton);

        
        final JLabel importSpecieButton = new JLabel(importSpecieIcon);
        importSpecieButton.setToolTipText("Fill the columns [taxid] and [species] based on the data entered in the study sample.");
        importSpecieButton.addMouseListener(new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent mouseEvent) {
              importSpecieButton.setIcon(importSpecieIcon);
              
              //addChangesListener3();
              forceSpecieImport = true;
              importSampleData();
              forceSpecieImport = false;
          }

          @Override
          public void mouseExited(MouseEvent mouseEvent) {
              importSpecieButton.setIcon(importSpecieIcon);
          }

          @Override
          public void mouseEntered(MouseEvent mouseEvent) {
              importSpecieButton.setIcon(importSpecieIconOver);
          }
      });
      
      	buttonContainer.add(Box.createHorizontalStrut(5));
      	buttonContainer.add (importSpecieButton);
        
      	
      	topContainer.add(buttonContainer, BorderLayout.EAST);
        add(topContainer, BorderLayout.NORTH);
    }

    @SuppressWarnings("static-access")
	private void calculateFileName(){
    	
    	// Check if the current cell has any value
    	if (parentFrame.getCurrentCellValue() == null){
    		
    		String path = getIsaCreatorInfo().getFileLocation();
    		
    		// Get the assay name
    		String assayName = getIsaCreatorInfo().getCurrentAssay().getIdentifier();
    		
    		// Remove the extension
    		assayName = assayName.substring(0, assayName.length()-4);

            //Make sure the filename starts with m_ not a_ (a_ = Assay, s_ = Study, i_ Investigation, so we adopt m_ = metabolite)
            assayName = assayName.replaceFirst("a_","m_") + "_v2_maf.tsv";

    		// Compose the final file name
			fileName = path + (new File(".")).separator + assayName;   //Problem with file separators and the File class, we could use "/"
            fileNameNoPath = assayName;                             //Do we need the path?
    		
    	} else {

    		fileName = parentFrame.getCurrentCellValue();
            fileNameNoPath = fileName;
    	}

    }

    private void saveFile(){
        logger.info("Saving the file");
        
        FileWriter fw = new FileWriter();
        
        try {
            System.out.println("file separator check before writing file: " +System.getProperty("file.separator"));
            fw.writeFile(getFileName(), sheet);

		} catch (FileNotFoundException e) {
            logger.error(e.getMessage().toString());
			e.printStackTrace();
		}
    }

    public void loadFile(){
    	logger.info("Loading file");

        String fn = getFileName();
        File file = new File(fn);
        
        // If the file exists...
        if (file.exists()){
            logger.info("Trying to load the metabolite assignment file: " + fn);

            FileLoader fl = new FileLoader();

            try {
                setTableReferenceObject(fl.loadFile(fn, getTableReferenceObject()));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            Spreadsheet loadedSheet = new Spreadsheet(parentFrame, getIsaCreatorInfo().addTableRefSampleColumns(getTableReferenceObject()),"");   //To map the columns that we load from the file
        	updateSpreadsheet(getIsaCreatorInfo().addSpreadsheetSampleColumns(loadedSheet));  // Load the existing spreadsheet and add any new sample columns
        }

    }

    private void updateSpreadsheet(Spreadsheet newSpreadsheet){

        logger.info("Removing existing spreadsheet");
        
        // If we have a previous sheet
        if (sheet != null){
        	remove(sheet);
        }
        
        logger.info("Adding the new sheet");
        sheet = newSpreadsheet;
        addChangesListener();

        if (!isVersion1File())  //Only add if new TSV fileformat is present
            addCustomCellEditors();

        add(getIsaCreatorInfo().addSpreadsheetSampleColumns(sheet),BorderLayout.CENTER);  //Add all missing sample columns to the spreadsheet
        validate();
        
        // To test
        //info.setText("The sample file identifier is: " + getIsaCreatorInfo().getCurrentStudy().getStudySampleFileIdentifier());
        //info.setText("The sample file identifier is: " + getIsaCreatorInfo().getCurrentStudySample().getIdentifier());
    }

   	public boolean isColumnEmpty(String columnName){
   		int column = sheet.getSpreadsheetFunctions().getModelIndexForColumn(columnName);
   		
   		return isColumnEmpty(column);
   	}

    private boolean isColumnEmpty(int column){
    	
    	SpreadsheetCell value = (SpreadsheetCell) sheet.getTable().getValueAt(0, column);
    	return (value.isEmpty());
    }

    /**
     * Fill sample columns of our configuration (taxid & species) based on Study Sample data
     * taxid should be a taxon identifier based on an ontology
     * species, the human readable equivalent. 
     * IN the Study sample we have: "Characteristics[organism]"	"Term Source REF"	"Term Accession Number"
     * So:
     *  taxid --> "Term Source REF" + "Term Accession Number"
     *  species --> "Characteristics[organism]"	
     */
    public void importSampleData(){

		try{
			
			System.out.println("Importing sample data");
			logger.info("Importing sample data");
			// Get the study sample data
			Assay studySample = isaCreatorInfo.getCurrentStudySample();
	    	
	    	// Check if we have to populate the sampledata
	    	if (haveToFillSampleData(studySample)){

	    		String termSourceREF="", termAccessionNumber="", organism = "", taxid="";
	    		
	    		int column = studySample.getSpreadsheetUI().getSpreadsheet().getSpreadsheetFunctions().getModelIndexForColumn(SPECIEFIELD);
	    	   	   		
	    		SpreadsheetCell cell = (SpreadsheetCell) studySample.getSpreadsheetUI().getSpreadsheet().getTable().getValueAt(0, column);
	    		
	    		String value = cell.toString();

	    		logger.info("Importing sample data to metabolights plugin: " + value);
	    		
	            OntologyTerm ontologyTerm = isaCreatorInfo.getOntologyTerm(value);

	            if (ontologyTerm != null){
	            	
	            	termSourceREF = ontologyTerm.getOntologySourceInformation().getSourceName();
	                termAccessionNumber = ontologyTerm.getOntologySourceAccession();
	                organism = ontologyTerm.getOntologyTermName();
	                taxid=termSourceREF + ":" + termAccessionNumber;
	  
	        		// Write sample data
	        	  	// Get the current assay
	            	Assay assay = isaCreatorInfo.getCurrentAssay();
	    			int taxidCol = getSheet().getSpreadsheetFunctions().getModelIndexForColumn("taxid");
	    			int speciesCol = getSheet().getSpreadsheetFunctions().getModelIndexForColumn("species");

	    			int rows = getSheet().getTable().getRowCount();
	    			
	    			// Fill the whole columns....(TODO: why columnumber-2?).
	    			if (!taxid.equals("")) getSheet().getSpreadsheetFunctions().fill(new SpreadsheetCellRange(new int[]{0,rows}, new int[]{taxidCol}), taxid);
	    			if (!organism.equals("")) getSheet().getSpreadsheetFunctions().fill(new SpreadsheetCellRange(new int[]{0,rows}, new int[]{speciesCol}), organism);
	            }
	    	}
		
		}catch (Exception e){
			logger.error("Theres been an error while importing sample information into the maf file!!!.");
			logger.error(e);
		}
    	
    }

    public boolean haveToFillSampleData(Assay studySample){
    	
    	// Check if there is already sample data in the spreadsheet (target)
    	boolean dataInTarget = !isColumnEmpty(TAXID);
    	
    	// If import is forced, let change dataInTarget to false
    	if (forceSpecieImport) dataInTarget = false;
    	
    	// Check if there is data in the study sample assay (source)
    	boolean dataInSource = isThereSampleData(studySample);
    	
    	
    	return (dataInSource && !dataInTarget);
    }

    private boolean isThereSampleData(Assay studySample){

        String value = null;
    	
		int column = studySample.getSpreadsheetUI().getSpreadsheet().getSpreadsheetFunctions().getModelIndexForColumn(SPECIEFIELD);

		SpreadsheetCell cell = (SpreadsheetCell)studySample.getSpreadsheetUI().getSpreadsheet().getTable().getValueAt(0, column);

        if (cell != null)
		    value = cell.toString();
    	
    	return !(value == null || value.equals(""));
    }


}
