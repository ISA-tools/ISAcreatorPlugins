package org.isatools.plugins.metabolights.assignments.actions;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub.DocSumType;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub.ItemType;

import java.awt.event.ActionEvent;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.AbstractAction;
import javax.swing.JTable;


import org.apache.commons.lang.StringUtils;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;



public class AutoCompletionAction extends AbstractAction{

	final String IDENTIFIER_COL_NAME = "identifier";
	final String FORMULA_COL_NAME = "chemical_formula";
	final String DESCRIPTION_COL_NAME = "description";
	
	SelectionRunner source;
	String currentCellValue;

	public void actionPerformed(ActionEvent e) {
		
		//Get the object that has generated the event
		source = (SelectionRunner) e.getSource();
		
		// Get the value of the source cell
		currentCellValue = source.getTable().getValueAt(source.getRow(), source.getCol()).toString();
		
		// If it's empty
		if (currentCellValue == null || currentCellValue.equals("")) return;
		
		// If there isn't anything to autocomplete
		if (!isThereAnythingToAutocomplete()) return;
		
		// At this point there is some autocompletion to do
		
		// Get the Metabolite object based on the column
		Metabolite met = getMetabolite();

		// Populate the Autocomplete columns
		autoCompleteColumns(met);
	}
	private void autoCompleteColumns(Metabolite met){
		
		
		setColumn(met.getDescription(), DESCRIPTION_COL_NAME);
		setColumn(met.getFormula(), FORMULA_COL_NAME);
		setColumn(met.getIdentifier(), IDENTIFIER_COL_NAME);
		
	}
	private void setColumn(String value, String columnName){
		
		
		// Only set the value if empty...
		if (isColumnEmpty(columnName)){
			
			int colIndex = getColIndexByName(columnName);
			
			source.getTable().setValueAt( value, source.getRow(), colIndex);
		}
		
	}
	
	private boolean isThereAnythingToAutocomplete(){
		
		int emptyCells = 0;
		
		if (isColumnEmpty(DESCRIPTION_COL_NAME)) emptyCells++;
		if (isColumnEmpty(FORMULA_COL_NAME)) emptyCells++;
		if (isColumnEmpty(IDENTIFIER_COL_NAME)) emptyCells++;
		
		return (emptyCells>0);
	}
	private boolean isColumnEmpty(String columnName){
	
		// Get the column index
		int columnIndex = getColIndexByName(columnName);
		
		// Get the value of the cell
		String value = source.getTable().getValueAt(source.getRow(), columnIndex).toString();
		
		// If It's empty
		if (value == null || value.equals("")) return true;
		
		// Otherwise
		return false;
		
	}
	private int getColIndexByName(String columnName){
		return source.getTable().getColumnModel().getColumnIndex(columnName);
	}
	
	
	// Get a metabolite instance based on the active cell
	private Metabolite getMetabolite(){
		
		// Get the current column name
		String columnName = source.getTable().getColumnName(source.getCol());
		
		// If it the description column
		if (DESCRIPTION_COL_NAME.equals(columnName) || IDENTIFIER_COL_NAME.equals(columnName)){
			return getMetaboliteFromEntrez(currentCellValue, "CompleteSynonym");
		} else if (FORMULA_COL_NAME.equals(columnName)){
			return getMetaboliteFromEntrez(currentCellValue, "All Fields");
		}
		return null;
		
		
	}
	public static Metabolite getMetaboliteFromEntrez(String term , String field){
	
       try
       {
       	
           EUtilsServiceStub service = new EUtilsServiceStub();
           // call NCBI ESearch utility
           // NOTE: search term should be URL encoded
           EUtilsServiceStub.ESearchRequest req = new EUtilsServiceStub.ESearchRequest();
           // Search only in PubChem Compound
           req.setDb("pccompound");
           
           // prepare the term
           term = prepareEntrezTerm(term);
           
           // Search on synonyms only 
           term = term + "[" + field + "]";
           req.setTerm(term);
           
           // Get the first one
           req.setRetMax("1");
           EUtilsServiceStub.ESearchResult res = service.run_eSearch(req);

           
           // results output
           if (res.getIdList().getId().length>0)
           {
               // Get the the id of the first element
        	   Metabolite met = getMetaboliteFromPubChem(res.getIdList().getId()[0]);
        	   return met;
           }
           
           return null;
       }
       catch (Exception e) { 
    	   
    	   System.out.println(e.toString());
    	   return null;
       }
       
	}
	public static String prepareEntrezTerm(String term){
		
		// If not is numeric, enclose the term in double quotes
		if (!StringUtils.isNumeric(term)){
			term = "\"" + term + "\"";
		}
		
		// Replace any special character for  white space (is how pubchem likes it)
		term = term.replace(":", " ");
		
		// Return the term.
		return term;
	}
	public static Metabolite getMetaboliteFromPubChem(String id){
		// retrieves document Summaries by list of primary IDs
       try
       {
           EUtilsServiceStub service = new EUtilsServiceStub();
           // call NCBI ESummary utility
           EUtilsServiceStub.ESummaryRequest req = new EUtilsServiceStub.ESummaryRequest();
           req.setDb("pccompound");
           req.setId(id);//"2519");
           EUtilsServiceStub.ESummaryResult res = service.run_eSummary(req);
           
           // Instantiate the metabolite class
           Metabolite met = new Metabolite();
           
           // results output
           for(int i=0; i<res.getDocSum().length; i++)
           {
        	   // Get the doc summary..
        	   DocSumType docSum = res.getDocSum()[i];
        	   
               // Go through all Items (properties)
               for (int k = 0; k < docSum.getItem().length; k++)
               {
            	   // Get the item
            	   ItemType it = docSum.getItem()[k];
            	   // Get the name
            	   String name =it.getName();
            	   
            	   // Populate met object
            	   if ("MolecularFormula".equals(name)){
            		   met.setFormula(it.getItemContent());
            		   
            		   // This should be the last property we are interested in.
            		   return met;
            	   }
            	   else if ("SynonymList".equals(name)) {
            		   met.setIdentifier(getBestID(it));
            		   
            		   
            	   } else if ("MeSHHeadingList".equals(name)){
            		   
            		   ItemType nameItem = it.getItem()[0];
            		   
            		   // If there is a name in MeSHHeadingList
            		   if (nameItem != null){
            			   met.setDescription(nameItem.getItemContent());
            		   }
            	   }
               }
               
           }
           return met;
       }
       catch(Exception e) { System.out.println(e.toString()); return null;}
	}
	
	public static String getBestID(ItemType synonymItem){
		
		//Prioirity list of ids
		String[] prioritylist = {"CHEBI:", "C[0-9]:", "LM"};
		String bestId = null;
		// Set the priority score to the lower
		int priorityScore = prioritylist.length;
		
		// Get the array of synonyms
     	ItemType[] synonyms = synonymItem.getItem();
    	
     	// Go through the synonyms
    	for (ItemType synonym: synonyms){
    		
    		// Get the synonym
    		String name = synonym.getItemContent();
    		
    		// Go through the priority list
    		for (int i = 0; i < prioritylist.length;i++){
    			
    			// If the synonym starts with the same text
    			if (matchRegEx(name,prioritylist[i])){
    				
    				// If the index is the first item (hi-priority item)...stop searching
    				if (i==0){
    					return name;
    				}else{
    					
    					// If previous synonym found is of less priority...
    					if (priorityScore< i){
    						//... highest priority item found
    						// Get the new synonym
    						bestId = name;
    						priorityScore = i;
    						
    					}
    				}
    				
    			}
    			
    		}
    		
    		
    	}
    	
		// Return the best Id
    	return bestId;
		
	}
	public static boolean matchRegEx(String text, String patternToSearch){
		
			Pattern pattern = Pattern.compile(patternToSearch);
			
            Matcher matcher = pattern.matcher(text);

            // Return true if found
            return matcher.find();
		
		
	}
}
