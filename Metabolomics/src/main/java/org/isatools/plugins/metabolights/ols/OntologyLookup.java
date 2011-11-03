package org.isatools.plugins.metabolights.ols;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.OLSClient;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;

import java.util.*;

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

    private OLSClient olsc;

    public OLSClient getOlsc() {

        if (olsc == null)
             olsc = new OLSClient();

        return olsc;
    }


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

    public Map<OntologySourceRefObject, List<OntologyTerm>> getIdByName(String identifier, String ontology) {

    	Ontology onto = null;
        Map<OntologySourceRefObject, List<OntologyTerm>> results = null;

        if (ontology.equals(TermTypes.CHEBI))
            onto = new Ontology("CHEBI",null,"CHEBI","Chemical Entities of Biological Interest");

    	RecommendedOntology ro = new RecommendedOntology(onto);
    	results = olsc.getTermsByPartialNameFromSource(identifier, Arrays.asList(new RecommendedOntology[]{ro}));

        return results;
    }

}
