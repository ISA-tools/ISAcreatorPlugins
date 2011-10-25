package org.isatools.plugins.metabolights.assignments.ui;

import org.apache.log4j.Logger;
import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.plugins.metabolights.assignments.IsaCreatorInfo;
import org.isatools.plugins.metabolights.assignments.io.FileLoader;
import org.isatools.plugins.metabolights.assignments.io.FileWriter;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import java.awt.*;
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
    
    private IsaCreatorInfo isaCreatorInfo;

    private IsaCreatorInfo getIsaCreatorInfo() {
        if (isaCreatorInfo == null)
            isaCreatorInfo = new IsaCreatorInfo();
        return isaCreatorInfo;
    }

    @InjectedResource
    private ImageIcon saveIcon, saveIconOver, loadIcon, loadIconOver, okIcon, okIconOver;

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
        //addChangesListener();
    }

    //Trying to listen to the changes of the Table. This method is called from createGUI and update updateSpreadsheet (now commented).
    private void addChangesListener(){
    	
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
        info.setText("This is the info label");	
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
        //addChangesListener();
        add(getIsaCreatorInfo().addSpreadsheetSampleColumns(sheet),BorderLayout.CENTER);  //Add all missing sample columns to the spreadsheet
        validate();
        
        // To test
        //info.setText("The sample file identifier is: " + getIsaCreatorInfo().getCurrentStudy().getStudySampleFileIdentifier());
        info.setText("The sample file identifier is: " + getIsaCreatorInfo().getCurrentStudySample().getIdentifier());
    }


}
