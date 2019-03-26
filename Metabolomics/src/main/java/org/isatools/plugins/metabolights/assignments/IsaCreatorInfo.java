package org.isatools.plugins.metabolights.assignments;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.isatools.isacreator.api.utils.SpreadsheetUtils;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
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

    public IsaCreatorInfo() {
    }

    public ISAcreator getIsacreator() {
        if (isacreator == null)
            isacreator = ApplicationManager.getCurrentApplicationInstance();

        return isacreator;
    }

    private DataEntryEnvironment getISACreatorEnvironment() {
        return getIsacreator().getDataEntryEnvironment();
    }

    public Assay getCurrentAssay() {

        if (ApplicationManager.getScreenInView() instanceof Assay) {
            return (Assay) ApplicationManager.getScreenInView();
        }
        return null;

    }
    /*
    Returns the current Assay, from the ISAcreator object.  This is the assay you are working on

     */
    public AssaySpreadsheet getCurrentAssaySpreadsheet() {

        if (ApplicationManager.getScreenInView() instanceof Assay) {
            return (AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection((Assay) ApplicationManager.getScreenInView());
        }
        return null;

    }

    /*
    Returns the current investigation, with assasy etc
     */
    public Investigation getCurrentInvestigation() {

        Investigation investigation = getISACreatorEnvironment().getInvestigation();
        logger.debug("Investigation id is '" + investigation.getInvestigationId() + "' title is " + investigation.getInvestigationTitle() + ", configuration used " + investigation.getLastConfigurationUsed());
        return investigation;

    }

    /*
    Returns a list of sample names from the assay spreadsheet in ISAcreator (GUI)
     */
    public List<String> getSampleColumns() {

        List<String> sampleData = new ArrayList<String>();

        if (getIsacreator() != null && getCurrentAssaySpreadsheet() != null) {

            Set<String> sampleRows = getColumnOnHeaderName("Assay Name");
            //Try to use the assay name first, in the file this is "MS Assay Name" or "NMR Assay Name", so " Assay Name" should be fine

            if (sampleRows == null)
                sampleRows = getColumnOnHeaderName("Sample Name");   //No MS/NMR Assay Name column in the Assay, use the sample name

            if (sampleRows == null)                     // If there are no MS/NMR Assay Name and no Sample Name
                sampleRows = getCurrentColumnValues(1); // Try Column 1 as this is the default sample column on the assay

            if (sampleRows != null) {  //Make sure we have some data
                for (String assayName : sampleRows) {
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
    public Set<String> getCurrentColumnValues(Integer columnNumber) {

        Set<String> currentSet = new ListOrderedSet<String>();

        if (getIsacreator() != null && getCurrentAssaySpreadsheet() != null && columnNumber != null) {

            Spreadsheet spreadsheet = getCurrentAssaySpreadsheet().getSpreadsheet();
            currentSet = SpreadsheetUtils.getDataInColumn(spreadsheet, columnNumber);

        }

        return currentSet;

    }

    public  Set<String> getColumnOnHeaderName(String columnHeader){

        Set<String> currentSet = new ListOrderedSet<String>();

        if (getIsacreator() != null && getCurrentAssaySpreadsheet() != null && columnHeader != null) {

            Spreadsheet spreadsheet = getCurrentAssaySpreadsheet().getSpreadsheet();
            currentSet = SpreadsheetUtils.findValuesForColumnInSpreadsheet(spreadsheet, columnHeader);
        }

        return currentSet;

    }

    public String getFileLocation() {
        File file = new File(".");

        if (getIsacreator() == null)
            try {
                return file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }

        // Get the reference
        String ref = getCurrentInvestigation().getReference();

        // If it has been saved
        if (ref != null) {
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
    public TableReferenceObject addTableRefSampleColumns(TableReferenceObject tableReferenceObject) {

        if (getIsacreator() != null) {
            List<String> assaySampleList = getSampleColumns();

            Iterator iterator = assaySampleList.iterator();
            while (iterator.hasNext()) {
                String sampleName = (String) iterator.next();
                if (sampleName != null && sampleName.length() > 0) { //Add the sample name, but there are lots of empty rows so need to test first
                    FieldObject fieldObject = new FieldObject(sampleName, "Sample description", DataTypes.STRING, "", false, false, false);   //New column to add to the definition

                    if (tableReferenceObject != null)
                        if (tableReferenceObject.getFieldByName(sampleName) == null) {
                            logger.debug("Adding optional column to the spreadsheet definition: " + sampleName);
                            tableReferenceObject.addField(fieldObject);
                        }

                }
            }
        }

        return tableReferenceObject;

    }

    public TableReferenceObject addDataFromFile(String fileName) {

        int count = 0;
        String[] nextLine;
        String[] colHeaders = null;
        TableReferenceObject tro = null;

        if (getIsacreator() != null) {

            if (fileName != null)
                tro = new TableReferenceObject(fileName);

            try {
                CSVReader reader = new CSVReader(new FileReader(fileName), '\t');

                while ((nextLine = reader.readNext()) != null) {
                    if (count == 0) {
                        colHeaders = nextLine;


                        Vector<String> preDefinedHeaders = new Vector<String>();
                        preDefinedHeaders.add("Row No.");

                        for (String h : nextLine) {
                            preDefinedHeaders.add(h);
                        }

                        if (preDefinedHeaders.size() > 0) {
                            tro.setPreDefinedHeaders(preDefinedHeaders);
                        }

                        count++;
                    } else {
                        tro.addRowData(colHeaders, nextLine);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tro;

    }


    /*
    Add all missing sample columns to the spreadsheet, the DEFINITION must be defined first
     */
    public Spreadsheet addSpreadsheetSampleColumns(Spreadsheet newSheet) {

        if (getIsacreator() != null) {
            System.out.println("Now, time to add the extra sample columns from the assas sheet");
            List<String> assaySampleList = getSampleColumns();      //From ISAcreator Assay sheet

            Iterator iter = assaySampleList.iterator();
            while (iter.hasNext()) {
                String sampleName = (String) iter.next();
                if (!newSheet.getSpreadsheetFunctions().checkColumnExists(sampleName) && sampleName.length() > 0) {
                    logger.debug("Adding optional column to the spreadsheet:" + sampleName);
                    newSheet.getSpreadsheetFunctions().addColumn(sampleName, true);

                } else {
                    logger.debug("Sample column already exists in the spreadsheet : " + sampleName);
                }
            }
        }

        return newSheet;
    }

    public OntologyTerm getOntologyTerm(String uniqueid) {

        OntologyTerm ontologyTerm = OntologyManager.getOntologySelectionHistory().get(uniqueid);
        return ontologyTerm;

    }


    /**
     * Gets as study object properly casted.
     *
     * @param object
     * @return
     */
    private Study getStudy(Object object) {

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
    public Study getCurrentStudy() {

        Object userObject = getISAStudyNode().getUserObject();
        return getStudy(userObject);

    }

    /**
     * Returns the study node
     */
    private DefaultMutableTreeNode getISAStudyNode() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getISACreatorEnvironment().getSelectedNodeInOverviewTree().getParent().getParent();
        return selectedNode;
    }

    /**
     * Returns an asssay with the study sample.
     *
     * @param object
     * @return
     */
    private Assay getStudySample(Object object) {

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
    public Assay getCurrentStudySample() {

        Object userObject = getISAStudySampleNode().getUserObject();
        return getStudySample(userObject);

    }

    /**
     * Returns the study sample node
     */
    private DefaultMutableTreeNode getISAStudySampleNode() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getISACreatorEnvironment().getSelectedNodeInOverviewTree().getParent();
        return selectedNode;
    }


}


