package org.isatools.plugins.metabolights.ols;

import org.apache.log4j.Logger;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kenneth
 * Date: 26/10/2011
 * Time: 11:19
 */
public class OntologyLookup {

    private static Logger logger = Logger.getLogger(OntologyLookup.class);

    /*
    public static void main(String[] args) {

       String ontol = "CHEBI";
       String termName = getNameByIdAndOntology("CHEBI:15377", ontol);
       Map<String, String> allTerms = getAllTermsByNameAndOntology("water",ontol, false);

       List<String> specificTerms = getByNameAndOntologyAndType(termName, TermTypes.FORMULA, ontol, false);

    }
    */

    public Map<String, String> getAllTermsByNameAndOntology(String termName, String ontologyName, boolean includeSynonyms){
        Map map = new HashMap();
        try {
            QueryService locator = new QueryServiceLocator();
            Query qs = locator.getOntologyQuery();
            map = qs.getTermsByName(termName, ontologyName, includeSynonyms);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public String getNameByIdAndOntology(String accession, String ontologyName){
        String name = null;
        try {
            QueryService locator = new QueryServiceLocator();
            Query qs = locator.getOntologyQuery();
            name = qs.getTermById(accession, ontologyName);
            System.out.println("getNAmeByIdAndOntology:"+name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }


    public List<String> getByNameAndOntologyAndType(String termName, String termType, String ontologyName, boolean includeSynonyms){

        List<String> termsFound = new ArrayList<String>();

        try {

            QueryService locator = new QueryServiceLocator();
            Query qs = locator.getOntologyQuery();


            System.out.println("test");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return termsFound;

    }



}
