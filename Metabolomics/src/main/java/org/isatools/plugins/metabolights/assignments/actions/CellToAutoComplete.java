package org.isatools.plugins.metabolights.assignments.actions;

import javax.swing.JTable;

public class CellToAutoComplete {
	private JTable table;
	private int col;
	private int row;
	
	public CellToAutoComplete(JTable table, int row, int col){
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
}