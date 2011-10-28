package org.isatools.plugins.metabolights.assignments.ui;

import org.apache.log4j.Logger;
import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.ontologyselectiontool.OntologySourceManager;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.SpreadsheetCell;
import org.isatools.isacreator.spreadsheet.SpreadsheetCellRange;
import org.isatools.isacreator.spreadsheet.StringEditor;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.plugins.metabolights.assignments.IsaCreatorInfo;
import org.isatools.plugins.metabolights.assignments.TableCellListener;
import org.isatools.plugins.metabolights.assignments.io.FileLoader;
import org.isatools.plugins.metabolights.assignments.io.FileWriter;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    private String fileName;
    private JLabel info;
    
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
    private ImageIcon saveIcon, saveIconOver, loadIcon, loadIconOver, okIcon, okIconOver, importSpecieIcon, importSpecieIconOver;

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
        addChangesListener3();
    }

    //Trying to listen to the changes of the Table. This method is called from createGUI and update updateSpreadsheet (now commented).
    private void addChangesListener1(){
    	
    	sheet.getTableModel().addTableModelListener(
    	new TableModelListener() {

    	    public void tableChanged(TableModelEvent e) {
    	        int row = e.getFirstRow();
    	        int column = e.getColumn();
    	        TableModel model = (TableModel)e.getSource();
    	        //String columnName = model.getColumnName(column);
    	        Object data = model.getValueAt(row, column);

    	        // Do something with the data...
    	        info.setText("Changed: row " + row + ", column " + column + ", value: " + data);
    	    }
    	}
    	);
    	
    	sheet.getTableModel().fireTableCellUpdated(1, 1);

    }
    private void addChangesListener2(){
    	
    	TableColumnModel model = sheet.getTable().getColumnModel();
    	TableColumn col = model.getColumn(1);
    	
    	CellEditor ce =  col.getCellEditor();
    	
    	ce.addCellEditorListener(new myListener(sheet));
    	
    	
    }
    private class myListener implements CellEditorListener{
		Spreadsheet sheet;
		int row;
		public myListener(Spreadsheet sheet){
			this.sheet = sheet; 
		}
		public void editingStopped(ChangeEvent e) {
			
			int newRow = sheet.getTableModel().getTable().getEditingRow();
			
			if (newRow != row) {
				
				row = newRow;
				info.setText("NEW ROW: Editing column 1, row " + row);
				
			}else{
				info.setText("Editing column 1, row " + row);
			}
			
			
			//StringEditor ce = (StringEditor)e.getSource();
			
			
		}
		public void editingCanceled(ChangeEvent arg0) {
			// TODO Auto-generated method stub
			info.setText("Edition in a cell in column 1 has been cancelled.");
		}
	}
    
    public void addChangesListener3(){
    	Action action = new AbstractAction()
    	{
    		public void actionPerformed(ActionEvent e)
    	    {
    	        TableCellListener tcl = (TableCellListener)e.getSource();
//    	        System.out.println("Row   : " + tcl.getRow());
//    	        System.out.println("Column: " + tcl.getColumn());
//    	        System.out.println("Old   : " + tcl.getOldValue());
//    	        System.out.println("New   : " + tcl.getNewValue());
    	        if (tcl.getColumn() == 1){
    	        	tcl.getTable().setValueAt(tcl.getNewValue(), tcl.getRow(), 2);
    	        }
    	    
    	    }
    	};

    	TableCellListener tcl = new TableCellListener(sheet.getTable(), action);
    	//sheet.getTable().setBackground(Color.RED);
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
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(UIHelper.BG_COLOR);

        Box buttonContainer = Box.createHorizontalBox();
        buttonContainer.setBackground(UIHelper.BG_COLOR);

        info = new JLabel();
        buttonContainer.add(info);
        //info.setText("This is the info label");	
//        final JLabel loadButton = new JLabel(loadIcon);
//        loadButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent mouseEvent) {
//                loadButton.setIcon(loadIcon);
//                loadFile();
//                
//            }
//
//            @Override
//            public void mouseExited(MouseEvent mouseEvent) {
//                loadButton.setIcon(loadIcon);
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent mouseEvent) {
//                loadButton.setIcon(loadIconOver);
//            }
//        });
//
//        final JLabel saveButton = new JLabel(saveIcon);
//        saveButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent mouseEvent) {
//                saveButton.setIcon(saveIcon);
//                saveFile();
//            }
//
//            @Override
//            public void mouseExited(MouseEvent mouseEvent) {
//                saveButton.setIcon(saveIcon);
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent mouseEvent) {
//                saveButton.setIcon(saveIconOver);
//            }
//        });
//
//        
//        buttonContainer.add(saveButton);
//        buttonContainer.add(Box.createHorizontalStrut(5));
//        buttonContainer.add(loadButton);
        
      final JLabel importSpecieButton = new JLabel(importSpecieIcon);
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
        addChangesListener3();
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
    	
		int column = studySample.getSpreadsheetUI().getTable().getSpreadsheetFunctions().getModelIndexForColumn(SPECIEFIELD);
	   		
		SpreadsheetCell cell = (SpreadsheetCell)studySample.getSpreadsheetUI().getTable().getTable().getValueAt(0, column); 
		
		String value = cell.toString();
    	
    	return !(value == null || value.equals(""));
    }


}
