package org.isatools.plugins.metabolights.assignments;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.plugins.metabolights.assignments.ui.DataEntrySheet;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

        if (object instanceof Assay)
            assay = (Assay) object;

        logger.debug("Current Assay is '" + assay.getIdentifier() + "', technology is " + assay.getTechnologyType() + ", platform is " + assay.getAssayPlatform());
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
        logger.debug("Investigation id is '"+ investigation.getInvestigationId() + "' title is " + investigation.getInvestigationTitle() + ", configuration used "+ investigation.getLastConfigurationUsed());
        return investigation;

    }

    /*
    Returns a list of sample name from the assay spreadsheet in ISAcreator (GUI)
     */
    public List<String> getSampleColumns(){

        List<String> sampleData = new ArrayList<String>();

        if (getIsacreator() != null && getCurrentAssay() != null){

           Set<String> sampleRows = getCurrentColumnValues(1); //Column 1 is the sample column on the assay

           if (sampleRows != null){  //Make sure we have some data
               Iterator iterator = sampleRows.iterator();
               while (iterator.hasNext()){
                   String assayName = (String) iterator.next();
                   if (assayName != null)
                        sampleData.add(assayName);
               }
           }

        }

        return sampleData;

    }

    /*
    This method will return the current data in the Assay column you request
     */
    public Set<String> getCurrentColumnValues(Integer columnNumber){

        Set<String> currentSet = new ListOrderedSet<String>();

        if (getIsacreator() != null && getCurrentAssay() != null && columnNumber != null)
           currentSet = SpreadsheetUtils.getDataInColumn(getCurrentAssay().getSpreadsheetUI().getTable(), columnNumber);

       return currentSet;

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
                    FieldObject fieldObject = new FieldObject(sampleName, "Sample description", DataTypes.STRING, "", false, false, false);   //New column to add to the definition

                    if (tableReferenceObject != null)
                        if (tableReferenceObject.getFieldByName(sampleName) == null){
                            logger.debug("Adding optional column to the spreadsheet definition: " +sampleName);
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
                        logger.debug("Adding optional column to the spreadsheet:" + sampleName);
                        newSheet.getSpreadsheetFunctions().addColumn(sampleName);
                   } else {
                       logger.debug("Sample column already exists in the spreadsheet:" + sampleName);
                   }
             }
        }

        return newSheet;
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
    public void fillSampleData(DataEntrySheet newSheet){
    	
    	// Check if we have to populate the sampledata
    	if (haveToFillSampleData(newSheet)){
    		
    		String termSourceREF, termAccessionNUmber, organism;
    		
    		// Get the study sample data
    		Assay studySample = getCurrentStudySample();

            OntologyTerm ontologyTerm = getOntologyTerm(studySample);

            if (ontologyTerm != null){
                termSourceREF = ontologyTerm.getOntologySource();
                termAccessionNUmber = ontologyTerm.getOntologySourceAccession();
                organism = ontologyTerm.getOntologyTermName();
            }

    		
    		// Get the termSourceREF
    		System.out.println(studySample.getSpreadsheetUI().getTable().getColValAtRow("Characteristics[organism]", 1));

    		
    	}
    	
    }

    private Map<String, OntologyTerm> getOntologyInformation(Assay assay) {

        Map<String, OntologyTerm> definedOntologies = assay.getTableReferenceObject().getDefinedOntologies();
        return definedOntologies;
    }

    private OntologyTerm getOntologyTerm(Assay assay){
        OntologyTerm ontologyTerm = new OntologyTerm();

        Map<String, OntologyTerm> ontologyTermMap = getOntologyInformation(assay);

        if (ontologyTermMap != null)
            ontologyTerm = ontologyTermMap.entrySet().iterator().next().getValue();

        return ontologyTerm;
    }

    private boolean haveToFillSampleData(DataEntrySheet newSheet){
    	
    	// TODO: Check if there is already sample data in the spreadsheet (target)
    	boolean dataInTarget = false;
    	
    	// TODO: Check if there is data in the study sample assay (source)
    	boolean dataInSource = true;
    	
    	
    	return (dataInSource && !dataInTarget);
    }
    
    
    /**
     *  Gets as study object properly casted.
     * @param object
     * @return
     */
    private Study getStudy(Object object){

        Study study = new Study();

        if (object instanceof Study) {
            study = (Study) object;
        }

        logger.info("Current Study is '" + study.getStudySampleFileIdentifier());
        return study;

    }

    /**
     * Return current study
     */
    public Study getCurrentStudy(){

    	Object userObject = getISAStudyNode().getUserObject();
        return getStudy(userObject);

    }
    /**
     * Returns the study node
     */
    private DefaultMutableTreeNode getISAStudyNode(){
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getISACreatorEnvironment().getSelectedNodeInOverviewTree().getParent().getParent();
        return selectedNode;
    }
    
    /**
     * Returns an asssay with the study sample.
     * @param object
     * @return
     */
    private Assay getStudySample(Object object){

        Assay studySample = new Assay();

        if (object instanceof Assay) {
            studySample = (Assay) object;
        }

        logger.info("Current Study sample (an assay)  identifier is: " + studySample.getIdentifier());
        return studySample;

    }

    /**
     * Return current study sample
     */
    public Assay getCurrentStudySample(){

    	Object userObject = getISAStudySampleNode().getUserObject();
        return getStudySample(userObject);

    }
    /**
     * Returns the study sample node
     */
    private DefaultMutableTreeNode getISAStudySampleNode(){
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getISACreatorEnvironment().getSelectedNodeInOverviewTree().getParent();
        return selectedNode;
    }

    

}


