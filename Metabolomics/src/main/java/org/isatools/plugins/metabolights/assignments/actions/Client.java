package org.isatools.plugins.metabolights.assignments.actions;

import gov.nih.nlm.ncbi.www.soap.eutils.*;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub.ItemType;

public class Client {
    public static void main(String[] args) throws Exception
    {
        //getDatabases();
    	//queryTermInAllDB();
    	queryPubChem("\"CHEBI 27732\"");
    	//queryPubChem("PC%2816%3A0%2F18%3A1%29%5BAll%20Fields%5D");
    	
    }

	/**
	 * 
	 */
	private static void getDatabases() {
		// eInfo utility returns a list of available databases
        try
        {
            EUtilsServiceStub service = new EUtilsServiceStub();
           
            // call NCBI EInfo utility
            EUtilsServiceStub.EInfoRequest req = new EUtilsServiceStub.EInfoRequest();
            EUtilsServiceStub.EInfoResult res = service.run_eInfo(req);
            // results output
            for(int i=0; i<res.getDbList().getDbName().length; i++)
            {
                System.out.println(res.getDbList().getDbName()[i]);
            }
        }
        catch(Exception e) { System.out.println(e.toString()); }
	}
    
	/**
	 * 
	 */
	private static void queryTermInAllDB() {

        // run_eGquery provides Entrez database counts for a single search
        try
        {
            EUtilsServiceStub service = new EUtilsServiceStub();
            // call NCBI eGQuery utility
            EUtilsServiceStub.EGqueryRequest req = new EUtilsServiceStub.EGqueryRequest();
            req.setTerm("caffeine");
            EUtilsServiceStub.Result res = service.run_eGquery(req);
            // results output
            System.out.println("Search term: " + res.getTerm());
            System.out.println("Results: ");
            for (int i = 0; i < res.getEGQueryResult().getResultItem().length; i++)
            {
                System.out.println("  " + res.getEGQueryResult().getResultItem()[i].getDbName() +
                                   ": " + res.getEGQueryResult().getResultItem()[i].getCount());
            }
        }
        catch (Exception e) { System.out.println(e.toString()); }
	}
	private static void queryPubChem(String term){
		 // search in PubMed Central for stem cells in free fulltext articles
        try
        {
        	
        	
            EUtilsServiceStub service = new EUtilsServiceStub();
            // call NCBI ESearch utility
            // NOTE: search term should be URL encoded
            EUtilsServiceStub.ESearchRequest req = new EUtilsServiceStub.ESearchRequest();
            req.setDb("pccompound");
            //req.setTerm("caffeine[completesynonym]");
            req.setTerm(term);
            req.setRetMax("15");
            EUtilsServiceStub.ESearchResult res = service.run_eSearch(req);
            // results output
            System.out.println("Original query: " + term);
            System.out.println("Found ids: " + res.getCount());
            System.out.print("First " + res.getRetMax() + " ids: ");
            for (int i = 0; i < res.getIdList().getId().length; i++)
            {
                System.out.print(res.getIdList().getId()[i] + " ");
                querySumaryInPubChem(res.getIdList().getId()[i]);
            }
            System.out.println();
        }
        catch (Exception e) { System.out.println(e.toString()); }
	}
	private static void querySumaryInPubChem(String id){
		// retrieves document Summaries by list of primary IDs
        try
        {
            EUtilsServiceStub service = new EUtilsServiceStub();
            // call NCBI ESummary utility
            EUtilsServiceStub.ESummaryRequest req = new EUtilsServiceStub.ESummaryRequest();
            req.setDb("pccompound");
            req.setId(id);//"2519");
            EUtilsServiceStub.ESummaryResult res = service.run_eSummary(req);
            // results output
            for(int i=0; i<res.getDocSum().length; i++)
            {
                System.out.println("ID: "+res.getDocSum()[i].getId());
                for (int k = 0; k < res.getDocSum()[i].getItem().length; k++)
                {
                	
                	ItemType item = res.getDocSum()[i].getItem()[k];
                	
                	if ("SynonymList".equals(item.getName())){
                		
                		ItemType[] synonyms = item.getItem();
                		
                		System.out.println("SYNONYMS:");
                		for (ItemType synonym: synonyms){
                			
                			System.out.println("       " + synonym.getName() +
                                    ": " + synonym.getItemContent());
                		}
                		
                		
                	}else{
                		
                		System.out.println("    " + item.getName() +
                                       ": " + item.getItemContent());
                	}
                }
            }
            System.out.println("-----------------------\n");
        }
        catch(Exception e) { System.out.println(e.toString()); }
	}
	
}    
