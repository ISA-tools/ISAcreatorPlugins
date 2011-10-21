package org.isatools.plugins.metabolights.assignments;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kenneth
 * Date: 13/10/2011
 * Time: 09:14
 */
public class IsaCreatorInfo {
	
	private static Logger logger = Logger.getLogger(IsaCreatorInfo.class);

    private ISAcreator isacreator;

    public IsaCreatorInfo(){
    }

    public ISAcreator getIsacreator() {
        if (isacreator == null)
            isacreator = ApplicationManager.getCurrentApplicationInstance();

        return isacreator;
    }

    private DataEntryEnvironment getISACreatorEnvironment(){
        return getIsacreator().getDataEntryEnvironment();
    }

    private DefaultMutableTreeNode getISANode(){
        DefaultMutableTreeNode selectedNode = getISACreatorEnvironment().getSelectedNodeInOverviewTree();
        return selectedNode;
    }

    private Assay getAssay(Object object){

        Assay assay = new Assay();

        if (object instanceof Assay) {
            assay = (Assay) object;
        }

        logger.info("Current Assay is '" + assay.getIdentifier() + "', technology is " + assay.getTechnologyType() + ", platform is " + assay.getAssayPlatform());
        return assay;

    }


    /*
    Returns the current Assay, from the ISAcreator object.  This is the assay you are working on
     */
    public Assay getCurrentAssay(){

        Object userObject = getISANode().getUserObject();
        return getAssay(userObject);

    }

    /*
    Returns the current investigation, with assasy etc
     */
    public Investigation getCurrentInvestigation(){

        Investigation investigation = getISACreatorEnvironment().getInvestigation();

        logger.info("Investigation id is '"+ investigation.getInvestigationId() + "' title is " + investigation.getInvestigationTitle() + ", configuration used "+ investigation.getLastConfigurationUsed());
        return investigation;

    }

    public List<String> getSampleColumns(){

        List<String> assayColumns = new ArrayList<String>();

        if (getIsacreator() != null && getCurrentAssay() != null){

            TableReferenceObject localTableReferenceObject = getCurrentAssay().getTableReferenceObject();

            if (localTableReferenceObject != null){   //Make sure we don't have an empty sheet
                List<List<String>> assayData = localTableReferenceObject.getData();

                Iterator iterator = assayData.listIterator();
                while (iterator.hasNext()){
                    List<String> assayRow = (List<String>) iterator.next();
                    String assayName = assayRow.get(0);  //Sample name is the first row
                    if (assayName != null)
                        assayColumns.add(assayName);
                }
            } else {
                logger.info("The assay is empty, can not find sample columns");
            }

        }

        return assayColumns;

    }

    public String getFileLocation() {
        File file = new File(".");

        if (getIsacreator() == null )
            try {
                return file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get the reference
            String ref = getCurrentInvestigation().getReference();
            
            // If it has been saved
            if (ref != null){
            	// Get the get the parent folder...
            	file = new File(getCurrentInvestigation().getReference());
                return file.getParentFile().getPath();
            }
            
            // Otherwise return null
            return null;
        
    }

    /*
    Add all missing sample columns to the spreadsheet *DEFINITION*
     */
    public TableReferenceObject addTableRefSampleColumns(TableReferenceObject tableReferenceObject){

        if (getIsacreator() != null){
            List<String> assaySampleList = getSampleColumns();
            Iterator iterator = assaySampleList.iterator();
            while (iterator.hasNext()){
                String sampleName = (String) iterator.next();
                if (sampleName != null && sampleName.length() > 0){ //Add the sample name, but there are lots of empty rows so need to test first
                    FieldObject fieldObject = new FieldObject(sampleName, "Sample description", DataTypes.STRING, "", false, false, false);

                    if (!fieldObject.equals(tableReferenceObject.getFieldByName(sampleName))){
                        logger.info("Adding optional column to the spreadsheet definition: " +sampleName);
                        tableReferenceObject.addField(fieldObject);
                    }
                }
            }
        }

        return tableReferenceObject;

    }

    /*
    Add all missing sample columns to the spreadsheet, the DEFINITION must be defined first
     */
    public Spreadsheet addSpreadsheetSampleColumns(Spreadsheet newSheet){

         if (getIsacreator() != null){
             List<String> assaySampleList = getSampleColumns();
             Iterator iter = assaySampleList.iterator();
             while (iter.hasNext()){
                String sampleName = (String) iter.next();
                   if (!newSheet.getSpreadsheetFunctions().checkColumnExists(sampleName) && sampleName.length() > 0){
                        logger.info("Adding optional column to the spreadsheet:" +sampleName);
                        newSheet.getSpreadsheetFunctions().addColumn(sampleName);
                   } else {
                       logger.info("Sample column already exists in the spreadsheet:" +sampleName);
                   }
             }
        }

        return newSheet;
    }


}


