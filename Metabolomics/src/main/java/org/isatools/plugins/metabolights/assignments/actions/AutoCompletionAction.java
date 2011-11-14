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
import org.isatools.plugins.metabolights.assignments.model.OptionalMetabolitesList;



public class AutoCompletionAction extends AbstractAction{

	static final String IDENTIFIER_COL_NAME = "identifier";
	static final String FORMULA_COL_NAME = "chemical_formula";
	static final String DESCRIPTION_COL_NAME = "description";
	
	CellToAutoComplete source;
	JTable table;
	String currentCellValue;

	public void actionPerformed(ActionEvent e) {
		
		//Get the object that has generated the event
		source = (CellToAutoComplete) e.getSource();
		table = source.getTable();
		
		// Get the value of the source cell
		currentCellValue = table.getValueAt(source.getRow(), source.getCol()).toString();
		
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
		
		// If metabolite is null there is nothing to fill
		if (met == null) return;
		
		setColumn(DESCRIPTION_COL_NAME, met.getDescription() );
		setColumn(FORMULA_COL_NAME, met.getFormula() );
		setColumn(IDENTIFIER_COL_NAME, met.getIdentifier() );
		
		table.validate();
		
	}
	private void setColumn(String columnName,String value){
		
		
		// Only set the value if empty...
		if (isColumnEmpty(columnName)){
			
			int colIndex = getColIndexByName(columnName);
			
			table.setValueAt( value, source.getRow(), colIndex);
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
		String value = table.getValueAt(source.getRow(), columnIndex).toString();
		
		// If It's empty
		if (value == null || value.equals("")) return true;
		
		// Otherwise
		return false;
		
	}
	private int getColIndexByName(String columnName){
		return table.getColumnModel().getColumnIndex(columnName);
	}
	
	
	// Get a metabolite instance based on the active cell
	private Metabolite getMetabolite(){
		
		// Get the current column name
		String columnName = table.getColumnName(source.getCol());
		
		return getMetabolite(columnName, currentCellValue);
		
	}
	public static Metabolite getMetabolite(String columnName, String value){
		
		// If it the description column
		if (DESCRIPTION_COL_NAME.equals(columnName) || IDENTIFIER_COL_NAME.equals(columnName)){
			return getMetaboliteFromEntrez(value, "CompleteSynonym");
		} else if (FORMULA_COL_NAME.equals(columnName)){
			return getMetaboliteFromEntrez(value, "All Fields");
		}
		return null;
		
	}
	public static Metabolite getMetaboliteFromEntrez(String term , String field){
	
		
		
		
       try
       {
       	
    	   // If we have it cached
    	   if (OptionalMetabolitesList.getObject().isThereMetabolitesForTerm(term)){
    		   // Get the metabolites cached
    		   Metabolite[] mets = OptionalMetabolitesList.getObject().getMetabolitesForTerm(term);
    		   
    		   // If there isn't any
    		   if (mets == null) return null;
    		   
    		   //return the first
    		   return mets[0];
    		   
    	   }
    	   
    	   
    	 EUtilsServiceStub service = new EUtilsServiceStub();
           // call NCBI ESearch utility
           // NOTE: search term should be URL encoded
           EUtilsServiceStub.ESearchRequest req = new EUtilsServiceStub.ESearchRequest();
           // Search only in PubChem Compound
           req.setDb("pccompound");
           
           // prepare the term
           String modifiedTerm = prepareEntrezTerm(term);
           
           // Search on synonyms only 
           modifiedTerm = modifiedTerm + "[" + field + "]";
           req.setTerm(modifiedTerm);
           
           // Get the first one
           req.setRetMax("10");
           EUtilsServiceStub.ESearchResult res = service.run_eSearch(req);

           if (res.getIdList() == null){
        	 // Set to null the cached
        	 OptionalMetabolitesList.getObject().setMetabolitesForTerm(null, term);
        	 return null;
        	   
           }
           
           if (res.getIdList().getId() == null){
        	  
          	 // Set to null the cached
          	 OptionalMetabolitesList.getObject().setMetabolitesForTerm(null, term);
          	return null; 
           }
           
           // Declare an array of metabolites
           Metabolite[] mets;
           
           // Join id into one String separated by commas
           String ids = org.apache.commons.lang.StringUtils.join(res.getIdList().getId(), ",");
        	   
           // Get the the id of the first element
    	   mets = getMetabolitesFromPubChem(ids);
        	   
           // Add it to the cache
           OptionalMetabolitesList.getObject().setMetabolitesForTerm(mets, term);
           
           // Return the first
           return mets[0];
           
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
	public static Metabolite[] getMetabolitesFromPubChem(String id){
		// retrieves document Summaries by list of primary IDs
       try
       {
           EUtilsServiceStub service = new EUtilsServiceStub();
           // call NCBI ESummary utility
           EUtilsServiceStub.ESummaryRequest req = new EUtilsServiceStub.ESummaryRequest();
           req.setDb("pccompound");
           req.setId(id);//"2519");
           EUtilsServiceStub.ESummaryResult res = service.run_eSummary(req);
           
           Metabolite[] mets = new Metabolite[res.getDocSum().length];
           
           // results output
           for(int i=0; i<res.getDocSum().length; i++)
           {
        	   // Get the doc summary..
        	   DocSumType docSum = res.getDocSum()[i];
        	
        	   // Get a metabolite from the Summary
               Metabolite met = getMetaboliteFromDocSum(docSum);
               
               // Add it to the array
               mets[i] = met;
               
           }

           return mets;
       }
       catch(Exception e) { System.out.println(e.toString()); return null;}
	}
	/**
	 * @param docSum
	 * @param met
	 */
	private static Metabolite getMetaboliteFromDocSum(DocSumType docSum) {

		Metabolite met = new Metabolite();
		
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
				   continue;
			   }
			   else if ("SynonymList".equals(name)) {
				   
				   // Get the identifier
				   met.setIdentifier(getBestID(it));
				   
				   // Get the name provisionally, 
				   if (it.getItem()!= null){
					   // ...get the first name
					   met.setDescription(it.getItem()[0].getItemContent());
				   }
				   
			// TODO: This doesn't work always...where is the name?
//			   } else if ("MeSHHeadingList".equals(name)){
//				   
//				   // If there is a name in MeSHHeadingList
//				   if (it.getItem() != null){
//		    		   
//					   met.setDescription(it.getItem()[0].getItemContent());
//				   }
			   }
		   }
		   
		   return met;
	}
	
	public static String getBestID(ItemType synonymItem){
		
		//Prioirity list of ids
		String[] prioritylist = {"^CHEBI:[0-9]+$", "^HMDB[0-9]+$", "^LM[A-Z]{2}[0-9]+$", "^C[0-9]{5}$"};
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
    					if (priorityScore > i){
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
