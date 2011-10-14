package org.isatools.plugins.metabolights.assignments;

import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: kenneth
 * Date: 13/10/2011
 * Time: 09:14
 */
public class IsaCreatorInfo {

    private ISAcreator isacreator;

    public IsaCreatorInfo(){

    }

    public ISAcreator getIsacreator() {
        if (isacreator == null)
            isacreator = ApplicationManager.getCurrentApplicationInstance();

        return isacreator;
    }

    public void setIsacreator(ISAcreator isacreator) {
        this.isacreator = isacreator;
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

        System.out.println("Current Assay is '" + assay.getIdentifier() + "', technology is " + assay.getTechnologyType() + ", platform is " + assay.getAssayPlatform());
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

        System.out.println("Investigation id is '"+ investigation.getInvestigationId() + "' title is " + investigation.getInvestigationTitle() + ", configuration used "+ investigation.getLastConfigurationUsed());
        return investigation;

    }



}


