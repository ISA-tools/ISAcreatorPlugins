package org.isatools.plugins.metabolights.assignments.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;

public class SelectionRunner extends AbstractAction{

	private JTable table;
	private Action action;
	private int col;
	private int row;
	
	public SelectionRunner(JTable table, Action actiontorun ){
		this.table = table;
		this.action = actiontorun;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		// We don't mind the event...just go through the selection
		row = table.getSelectedRow();
		col = table.getSelectedColumn();
		
		// If there is something selected...
		if (row != -1) {
						
			for ( ;row <= (table.getSelectedRow() + table.getSelectedRowCount()-1) ; row++){
				
				// Calculate the cell to edit, row should be already correct and column as well
				
				// Create the event to pass to the action
				ActionEvent event = new ActionEvent(this,0,"NEW_CELL_FOUND");
				action.actionPerformed(event);
				
			}
		
			table.validate();
		}
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