package org.isatools.plugins.metabolights.assignments.actions;

import static org.junit.Assert.*;

import org.isatools.plugins.metabolights.assignments.actions.AutoCompletionAction;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;
import org.junit.Test;

public class AutoCompletionActionTest {

    @Test
    public void testGetMetaboliteFromMetaboLightWS() {

        // search by compound name
        Metabolite met = AutoCompletionAction.getMetaboliteFromMetaboLightWS(AutoCompletionAction.DESCRIPTION_COL_NAME, "Palmitic Acid");

        // Palmitic acid found in chebi
        assertEquals("CHEBI:15756", met.getIdentifier());
        assertEquals("C16H32O2", met.getFormula());
        assertEquals("hexadecanoic acid", met.getDescription());

        // S-lactoyl-glutathione (Not in CHEBI, but in Chemspider)
        // Example: http://www.ebi.ac.uk/metabolights/webservice/genericcompoundsearch/name/S-lactoyl-glutathione
        met = AutoCompletionAction.getMetaboliteFromMetaboLightWS(AutoCompletionAction.DESCRIPTION_COL_NAME, "S-lactoyl-glutathione");

        assertEquals("CSID 389032", met.getIdentifier());
        assertEquals("C13H21N3O8S", met.getFormula());
        assertEquals("S-Lactoylglutathione", met.getDescription());

        // search by database id (At the moment only ChEBI is searchable by id)
        // Example: http://www.ebi.ac.uk/metabolights/webservice/genericcompoundsearch/databaseid/CHEBI:48669
        met = AutoCompletionAction.getMetaboliteFromMetaboLightWS(AutoCompletionAction.IDENTIFIER_COL_NAME, "CHEBI:48669");
        assertEquals("NC[C@H]1CC[C@@H](CC1)C(O)=O", met.getSmiles());
        assertEquals("C8H15NO2", met.getFormula());
        assertEquals("tranexamic acid", met.getDescription());

        //search by smiles
        // Example: http://www.ebi.ac.uk/metabolights/webservice/genericcompoundsearch/smiles
        met = AutoCompletionAction.getMetaboliteFromMetaboLightWS(AutoCompletionAction.SMILES, "NC[C@H]1CC[C@@H](CC1)C(O)=O");
        assertEquals("CHEBI:48669", met.getIdentifier());

        //search by inchi
        //Example: http://www.ebi.ac.uk/metabolights/webservice/genericcompoundsearch/inchi
        met = AutoCompletionAction.getMetaboliteFromMetaboLightWS(AutoCompletionAction.INCHI, "InChI=1S/C8H15NO2/c9-5-6-1-3-7(4-2-6)8(10)11/h6-7H,1-5,9H2,(H,10,11)/t6-,7-");
        assertEquals("CHEBI:48669", met.getIdentifier());

    }


    @Test
    public void testMatchRegEx() {

        // Test with KEGG compound id ("'C' followed by five-digit number")
        assertEquals(AutoCompletionAction.matchRegEx("C12345", "^C[0-9]{5}$"), true);
        assertEquals(AutoCompletionAction.matchRegEx("C12345ABC", "^C[0-9]{5}$"), false);
        assertEquals(AutoCompletionAction.matchRegEx("C1", "^C[0-9]{5}$"), false);
        assertEquals(AutoCompletionAction.matchRegEx("AAAAAAC12345", "^C[0-9]{5}$"), false);


        // CHEBI ID
        assertEquals(AutoCompletionAction.matchRegEx("CHEBI:12345", "^CHEBI:[0-9]+$"), true);
        assertEquals(AutoCompletionAction.matchRegEx("CHEBI:123456789", "^CHEBI:[0-9]+$"), true);
        assertEquals(AutoCompletionAction.matchRegEx("AAAACHEBI:12345", "^CHEBI:[0-9]+$"), false);
        assertEquals(AutoCompletionAction.matchRegEx("CHEBI:", "^CHEBI:[0-9]+$"), false);

        // LipidMapID
        assertEquals(AutoCompletionAction.matchRegEx("LMPK12090043", "^LM[a-zA-Z]{2}[0-9]+$"), true);
        assertEquals(AutoCompletionAction.matchRegEx("LMLMPK12090043", "^LM[a-zA-Z]{2}[0-9]+$"), false);
        assertEquals(AutoCompletionAction.matchRegEx("LMPK12090043 ", "^LM[a-zA-Z]{2}[0-9]+$"), false);


    }

}
