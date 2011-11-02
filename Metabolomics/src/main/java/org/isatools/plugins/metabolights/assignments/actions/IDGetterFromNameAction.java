package org.isatools.plugins.metabolights.assignments.actions;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.OLSClient;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;


public class IDGetterFromNameAction extends AbstractAction{

	public void actionPerformed(ActionEvent e) {
		
		//Get the object that has generated the event
		SelectionRunner source = (SelectionRunner) e.getSource();
		
		// Print coordinates
		System.out.print("Row is " + source.getRow() + ", Col is " + source.getCol());
		
		// Get the value of the cell
		String value = source.getTable().getValueAt(source.getRow(), source.getCol()).toString();
		
		// If is not null or empty
		if (value != null || !value.equals("")){
			
			// Get the id
			String ID = getIDByName(value);
			
			// Set the value of the ID column
			source.getTable().setValueAt( ID, source.getRow(), source.getTable().getColumnModel().getColumnIndex("identifier"));
		}
		
	}

	public static String getIDByName(String name){
		
		// Get a Ontology lookup service client
		OLSClient olsc = new OLSClient();
    	
		// Configure the ontology to lookup for
    	Ontology onto = new Ontology("CHEBI",null,"CHEBI","Chemical Entities of Biological Interest");
    	RecommendedOntology ro = new RecommendedOntology(onto);
    	
    	// Ask the ontology terms retrieved by the ontology
    	Map<OntologySourceRefObject, List<OntologyTerm>> results = olsc.getTermsByPartialNameFromSource(name, Arrays.asList(new RecommendedOntology[] {ro}));

    	// If there is any result
    	if (results.size()!=0){
    		
    		// Get the first...
    		OntologyTerm ot = results.values().iterator().next().get(0);
    		
    		// Return the result properly formatted ("CHEBI:12345")
    		return (ot.getOntologySource()+":"+ ot.getOntologySourceAccession());
    	}
    	
    	return "";
	}

}
