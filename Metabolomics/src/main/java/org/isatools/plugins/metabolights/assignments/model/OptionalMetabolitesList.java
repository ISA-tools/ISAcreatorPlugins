package org.isatools.plugins.metabolights.assignments.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.saxon.expr.SingletonAtomizer;

/**
 * Holds all the metabolites that has been retrieved in pubChem and stores them per row
 * 
 * @author conesa
 *
 */
public class OptionalMetabolitesList {
	private Map<String,Metabolite[]> metaboliteArrayMap = new HashMap<String,Metabolite[]>();
	static private OptionalMetabolitesList singleton;
	
	// Make it a singleton...
	private OptionalMetabolitesList(){}
	static public OptionalMetabolitesList getObject(){
		
		if (singleton == null){
			singleton = new OptionalMetabolitesList();
		}
		
		return singleton;
	}
	
	public boolean isThereMetabolitesForTerm(String term){
		return metaboliteArrayMap.containsKey(term);
	}
	public Metabolite[] getMetabolitesForTerm (String term){
		
		return metaboliteArrayMap.get(term);
		
	}
	public void setMetabolitesForTerm(Metabolite[] metabolites, String term){
		
		metaboliteArrayMap.put(term, metabolites);
	}

}
