package org.isatools.plugins.metabolights.assignments;

import org.apache.log4j.Logger;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;

import javax.swing.tree.DefaultMutableTreeNode;
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
    private static final String SAMPLE_PREFIX = "opt_sample_";

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

        if (getIsacreator() != null){

            List<List<String>> assayData = getCurrentAssay().getTableReferenceObject().getData();

            Iterator iterator = assayData.listIterator();
            while (iterator.hasNext()){
                List<String> assayRow = (List<String>) iterator.next();
                String assayName = assayRow.get(0);  //Sample name is the first row
                assayColumns.add(SAMPLE_PREFIX + assayName);
            }

        }

        return assayColumns;

    }



}


