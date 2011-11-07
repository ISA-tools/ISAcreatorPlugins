package org.isatools.plugins.metabolights.assignments.model;

public class Metabolite {

	String identifier;
	String formula;
	String description;

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
	public Metabolite(String identifier, String formula, String description) {
		super();
		this.identifier = identifier;
		this.formula = formula;
		this.description = description;
	}
	public Metabolite(){}
}
