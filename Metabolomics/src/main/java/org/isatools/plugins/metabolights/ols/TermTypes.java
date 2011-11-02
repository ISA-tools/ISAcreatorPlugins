package org.isatools.plugins.metabolights.ols;

/**
 * Created by IntelliJ IDEA.
 * User: kenneth
 * Date: 26/10/2011
 * Time: 12:10
 */
public class TermTypes {
    //Term types from OLS
    public static final String FORMULA      = "FORMULA synonym";
    public static final String SMILES       = "SMILES synonym";
    public static final String NAME         = "preferred name";
    public static final String REL_SYNONYM  = "related synonym";
    public static final String SYNONYM      = "exact synonym";
    public static final String XREF         = "xref_analog";

    //Configuration mappings, xml files
    public static final String IDENTIFIER  = "identifier";
    public static final String DESCRIPTION = "description";

    //Supported ontologies or validation services
    public static final String CHEBI = "CHEBI";
    public static final String PUBMED = "PUBMED";
    public static final String KEGG = "KEGG";

}
