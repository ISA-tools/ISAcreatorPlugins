package org.isatools.plugins.metabolights.assignments.model;

public class Metabolite {

	String identifier;
	String formula;
	String description;
    String inchi;
    String smiles;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public String getInchi() {
        return inchi;
    }

    public void setInchi(String inchi) {
        this.inchi = inchi;
    }

    public Metabolite(String identifier, String formula, String description) {
		super();
		this.identifier = identifier;
		this.formula = formula;
		this.description = description;
	}

    public Metabolite(String identifier, String formula, String description, String inchi, String smiles) {
        super();
        this.identifier = identifier;
        this.formula = formula;
        this.description = description;
        this.smiles = smiles;
        this.inchi = inchi;
    }

	public Metabolite(){}

    //Do not remove!!
	public String toString(){
		return getDescription() + "(" + getIdentifier() + "), " + getFormula();
	}
}
