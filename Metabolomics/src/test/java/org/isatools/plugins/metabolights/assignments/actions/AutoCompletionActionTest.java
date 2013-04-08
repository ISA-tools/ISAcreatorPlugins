package org.isatools.plugins.metabolights.assignments.actions;

import static org.junit.Assert.*;

import org.isatools.plugins.metabolights.assignments.actions.AutoCompletionAction;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;
import org.junit.Test;

public class AutoCompletionActionTest {

	@Test
	public void testGetMetaboliteFromEntrez() {
		
		// http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=985&loc=ec_rcs
		// Palmitic acid: CHEBI:15756, LMFA01010001, C00249
		Metabolite met = AutoCompletionAction.getMetaboliteFromEntrez("Palmitic Acid","CompleteSynonym" );
		
		assertEquals("CHEBI:15756", met.getIdentifier());
		assertEquals("C16H32O2", met.getFormula());
		assertEquals("Hexadecanoic acid", met.getDescription());
		
		// Ganacaonin F (Not in CHEBI, but in LipidMaps)
		//http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pccompound&id=5317482
		met = AutoCompletionAction.getMetaboliteFromEntrez("5317482","uid" );
		
		assertEquals("LMPK12090043", met.getIdentifier());
		assertEquals("C21H16O6", met.getFormula());
		assertEquals("Gancaonin F", met.getDescription());
		
	}
	
	@Test
	public void testsGetMetaboliteFromPubChem(){
		
		// http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=985&loc=ec_rcs
		// Palmitic acid: CHEBI:15756, LMFA01010001, C00249
		Metabolite[] mets = AutoCompletionAction.getMetabolitesFromPubChem("985");
		Metabolite met = mets[0];
		assertEquals("CHEBI:15756", met.getIdentifier());
		assertEquals("C16H32O2", met.getFormula());
		assertEquals("Hexadecanoic acid", met.getDescription());
	}

	@Test
	public void testMatchRegEx() {
		
		// Test with KEGG compound id ("'C' followed by five-digit number")
		assertEquals(AutoCompletionAction.matchRegEx("C12345","^C[0-9]{5}$"), true);
		assertEquals(AutoCompletionAction.matchRegEx("C12345ABC","^C[0-9]{5}$"), false);
		assertEquals(AutoCompletionAction.matchRegEx("C1","^C[0-9]{5}$"), false);
		assertEquals(AutoCompletionAction.matchRegEx("AAAAAAC12345","^C[0-9]{5}$"), false);
		
		
		// CHEBI ID
		assertEquals(AutoCompletionAction.matchRegEx("CHEBI:12345","^CHEBI:[0-9]+$"), true);
		assertEquals(AutoCompletionAction.matchRegEx("CHEBI:123456789","^CHEBI:[0-9]+$"), true);
		assertEquals(AutoCompletionAction.matchRegEx("AAAACHEBI:12345","^CHEBI:[0-9]+$"), false);
		assertEquals(AutoCompletionAction.matchRegEx("CHEBI:","^CHEBI:[0-9]+$"), false);
		
		// LipidMapID
		assertEquals(AutoCompletionAction.matchRegEx("LMPK12090043","^LM[a-zA-Z]{2}[0-9]+$"), true);
		assertEquals(AutoCompletionAction.matchRegEx("LMLMPK12090043","^LM[a-zA-Z]{2}[0-9]+$"), false);
		assertEquals(AutoCompletionAction.matchRegEx("LMPK12090043 ","^LM[a-zA-Z]{2}[0-9]+$"), false);
		
		
		
		
	}

}
