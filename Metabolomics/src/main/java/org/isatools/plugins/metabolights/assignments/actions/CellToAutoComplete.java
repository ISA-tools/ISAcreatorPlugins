package org.isatools.plugins.metabolights.assignments.actions;

import javax.swing.JTable;

public class CellToAutoComplete {
	private JTable table;
	private int col;
	private int row;
	private boolean force;
	
	public CellToAutoComplete(JTable table, int row, int col, boolean force){
		this.force = force;
		this.table = table;
		this.row = row;
		this.col = col;
	}

	public JTable getTable() {
		return table;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}
	public boolean getForce(){
		return this.force;
	}
}