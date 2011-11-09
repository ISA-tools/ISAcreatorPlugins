package org.isatools.plugins.metabolights.assignments.ui;

import org.apache.log4j.Logger;
import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.SpreadsheetCell;
import org.isatools.isacreator.spreadsheet.SpreadsheetCellRange;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.plugins.metabolights.assignments.IsaCreatorInfo;
import org.isatools.plugins.metabolights.assignments.TableCellListener;
import org.isatools.plugins.metabolights.assignments.actions.AutoCompletionAction;
import org.isatools.plugins.metabolights.assignments.actions.SelectionRunner;
import org.isatools.plugins.metabolights.assignments.io.FileLoader;
import org.isatools.plugins.metabolights.assignments.io.FileWriter;
import org.isatools.plugins.metabolights.ols.OntologyLookup;
import org.isatools.plugins.metabolights.ols.TermTypes;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    private String fileName;
    private JLabel info;
    private boolean autocomplete= true;
    
    public static String TAXID = "taxid";
    public static String SPECIES = "species";
    public static String SPECIEFIELD = "Characteristics[organism]";
    private boolean forceSpecieImport = false;
    

	private IsaCreatorInfo isaCreatorInfo;

    private IsaCreatorInfo getIsaCreatorInfo() {
        if (isaCreatorInfo == null)
            isaCreatorInfo = new IsaCreatorInfo();
        return isaCreatorInfo;
    }

    @InjectedResource
    private ImageIcon saveIcon, saveIconOver, loadIcon, loadIconOver, 
    					okIcon,	okIconOver, importSpecieIcon, importSpecieIconOver,
    					getIdIcon, getIdIconOver, selectedIcon, unSelectedIcon;

    public DataEntrySheet(EditorUI parentFrame, TableReferenceObject tableReferenceObject) {
        ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
        this.parentFrame = parentFrame;
        this.tableReferenceObject = tableReferenceObject;
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
    }

    public Spreadsheet getSheet(){
    	return sheet;
    }
    
    public TableReferenceObject getTableReferenceObject() {
		return tableReferenceObject;
	}

	public void createGUI() {
        sheet = new Spreadsheet(parentFrame, getIsaCreatorInfo().addTableRefSampleColumns(tableReferenceObject), "");  // Add the sample columns to the definition
        createTopPanel();
        add(getIsaCreatorInfo().addSpreadsheetSampleColumns(sheet), BorderLayout.CENTER);  // Add the sample columns to the spreadsheet
        createBottomPanel();
        
        // Add a listener to the changes of the table
        addChangesListener();
    }

    public void addChangesListener(){
    	Action action = new AbstractAction()
    	{
    		public void actionPerformed(ActionEvent e)
    	    {
    			
    			if (!autocomplete) return;
    			
    	        TableCellListener tcl = (TableCellListener)e.getSource();

                validateColumns(tcl);
    	    }
    	};

    	TableCellListener tcl = new TableCellListener(sheet.getTable(), action);
    	//sheet.getTable().setBackground(Color.RED);
    }

    private void validateColumns(TableCellListener tcl) {

        Integer column = tcl.getColumn();
        //Integer sheetColumn = sheet.getSpreadsheetFunctions().getModelIndexForColumn(TermTypes.IDENTIFIER);

        if (column == sheet.getSpreadsheetFunctions().getModelIndexForColumn(TermTypes.IDENTIFIER)) { // Is this the identifier column?        //Column 1
            //Add the name/description from the identifier
            getNamebyId(tcl.getNewValue().toString(), tcl.getRow(), TermTypes.DESCRIPTION);
        } else if (column == sheet.getSpreadsheetFunctions().getModelIndexForColumn(TermTypes.DESCRIPTION)) { // Is this the name column?        //Column 5
            //Add the name/description from the identifier
            populateIdFromName(tcl.getNewValue().toString(), tcl.getRow(), TermTypes.CHEBI, TermTypes.IDENTIFIER);
        }
    }

    private void getNamebyId(String identifiers, int row, String columnName){
        String ontology = null;

        if (!identifiers.contains(","))
            identifiers = identifiers + ","; //Make this one entry into a comma separated list

        //Which ontology should we use?
        if (identifiers.toUpperCase().contains(TermTypes.CHEBI)){
            ontology = TermTypes.CHEBI;
        }

        if (identifiers.toUpperCase().contains(TermTypes.PUBMED)){
            ontology = TermTypes.PUBMED;
        }

        if (identifiers.toUpperCase().contains(TermTypes.KEGG)){
            ontology = TermTypes.KEGG;
        }

        //TODO, for all entries, loop until a name has been found
        List<String> identifierList = Arrays.asList(identifiers.split(","));    //Get all the individual identifiers
        for(String identifier: identifierList){
            populateNameFromId(identifier, row, ontology, columnName);
        }

    }

    /*
    This method sets the column name based on the ontology name, in the correct row/column
     */
    private boolean populateNameFromId(String identifier, int row, String ontology, String columnName){

        Integer columnNumber = null;
        Boolean termFound = false;

        if (identifier != null && identifier.length() > 0) {

            String ontologyTermName = getOntologyLookup().getNameByIdAndOntology(identifier, ontology);

            if (ontologyTermName != null && ontologyTermName.length() > 0){  //Do we have a name?
                columnNumber = sheet.getSpreadsheetFunctions().getModelIndexForColumn(columnName); //Get the column number for the column name passed in

                if (columnNumber != null && columnNumber > 0){  //Do we have the column in the model
                    SpreadsheetCell spreadsheetCell = (SpreadsheetCell) sheet.getTable().getValueAt(row, columnNumber);

                    if (spreadsheetCell == null || spreadsheetCell.isEmpty())  //Does the cell already have some values?
                        sheet.getTable().setValueAt(ontologyTermName, row, columnNumber);

                    termFound = true;
                }
            }

        }

        return termFound;
    }

    private void populateIdFromName(String identifier,  int row, String ontology, String columnName){
        //Find if from the name
        Map<OntologySourceRefObject, List<OntologyTerm>> results = getOntologyLookup().getIdByName(identifier, ontology);
        Integer columnNumber = null;

    	if (results.size()!=0){
    		OntologyTerm ot = results.values().iterator().next().get(0);

            columnNumber = sheet.getSpreadsheetFunctions().getModelIndexForColumn(columnName); //Get the column number for the column name passed in
            SpreadsheetCell spreadsheetCell = (SpreadsheetCell) sheet.getTable().getValueAt(row, columnNumber);

            if (spreadsheetCell == null || spreadsheetCell.isEmpty())  //Does the cell already have some values?
    		    sheet.getTable().setValueAt(ot.getOntologySource()+":"+ ot.getOntologySourceAccession(), row, columnNumber);
    	}
    }
    
    public void createBottomPanel(){
    	JPanel bottomPannel = new JPanel(new BorderLayout());
    	bottomPannel.setBackground(UIHelper.BG_COLOR);
    	
    	Box buttonContainer = Box.createHorizontalBox();
    	buttonContainer.setBackground(UIHelper.BG_COLOR);
    	
    	JLabel file = new JLabel();
    	file.setBackground(UIHelper.BG_COLOR);
    	
    	// Not available in the first load (without ISACreator) 
    	if (!parentFrame.getAmIAlone()){
    		file.setText(getFileName());
    	}
    	
    	bottomPannel.add(file);
    	
    	final JLabel okButton = new JLabel(okIcon);
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                
            	// Try to force to save the current cell if it is in "edition mode".
            	SpreadsheetUtils.stopCellEditingInTable(sheet.getTable());
            	
            	saveFile();
            	parentFrame.setCurrentCellValue(fileName);
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
        autocompleteCheck.setText("Autocomplete:");
        autocompleteCheck.setToolTipText("Activate autocomplete if you want to have related cells autocompleted after a cell id edited.");
        autocompleteCheck.setIcon(unSelectedIcon);
        autocompleteCheck.setSelectedIcon(selectedIcon);
        autocompleteCheck.setSelected(autocomplete);
        autocompleteCheck.setHorizontalTextPosition(SwingConstants.LEFT);

        // Add a listener to the state changed
        autocompleteCheck.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent arg0) {
				// Change the autocomplete variable.
        		autocomplete = autocompleteCheck.isSelected();
			}
        });
        
        // Add the check box to the container
        buttonContainer.add(Box.createHorizontalStrut(5));
    	buttonContainer.add (autocompleteCheck);

        
        // Add a button to resolve IDs based on descriptions.
        final JLabel getIdButton = new JLabel(getIdIcon);
        getIdButton.setToolTipText("Fills the [identifier] based on the [description] column for the selected cells.");
        getIdButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                getIdButton.setIcon(getIdIcon);
                
                Action getIds = new SelectionRunner(sheet.getTable(), new AutoCompletionAction());
                
                getIds.actionPerformed(null);
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
   	
     private String getFileName(){

    	// if we do not have the property already set
    	if (fileName == null){
    		calculateFileName();
    	}
    	return fileName;
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
    		
    		// Add a asigmentfile sufix
    		assayName = assayName + "_maf.csv";
    		
    		// Compose the final file name
			fileName = path + (new File(".")).separator + assayName;
    		
    		
    	} else {
    		fileName = parentFrame.getCurrentCellValue();
    	}
    }

    private void saveFile(){
        logger.info("Saving the file");
        
        FileWriter fw = new FileWriter();
        
        try {
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
        	tableReferenceObject = fl.loadFile(getFileName(), tableReferenceObject);
            Spreadsheet loadedSheet = new Spreadsheet(parentFrame, getIsaCreatorInfo().addTableRefSampleColumns(tableReferenceObject),"");   //To map the columns that we load from the file
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
        add(getIsaCreatorInfo().addSpreadsheetSampleColumns(sheet),BorderLayout.CENTER);  //Add all missing sample columns to the spreadsheet
        validate();
        
        // To test
        //info.setText("The sample file identifier is: " + getIsaCreatorInfo().getCurrentStudy().getStudySampleFileIdentifier());
        info.setText("The sample file identifier is: " + getIsaCreatorInfo().getCurrentStudySample().getIdentifier());
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
    	
		// Get the study sample data
		Assay studySample = isaCreatorInfo.getCurrentStudySample();
    	
    	// Check if we have to populate the sampledata
    	if (haveToFillSampleData(studySample)){

    		String termSourceREF="", termAccessionNumber="", organism = "", taxid="";
    		
    		int column = studySample.getSpreadsheetUI().getTable().getSpreadsheetFunctions().getModelIndexForColumn(SPECIEFIELD);
    	   	   		
    		SpreadsheetCell cell = (SpreadsheetCell)studySample.getSpreadsheetUI().getTable().getTable().getValueAt(0, column); 
    		
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
    	
		int column = studySample.getSpreadsheetUI().getTable().getSpreadsheetFunctions().getModelIndexForColumn(SPECIEFIELD);

		SpreadsheetCell cell = (SpreadsheetCell)studySample.getSpreadsheetUI().getTable().getTable().getValueAt(0, column);

        if (cell != null)
		    value = cell.toString();
    	
    	return !(value == null || value.equals(""));
    }


}
