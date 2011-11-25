package org.isatools.plugins.metabolights.assignments.actions;


import org.isatools.plugins.metabolights.assignments.ui.ProgressTrigger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;

public class SelectionRunner extends AbstractAction{

	private JTable table;
	private Action action;
	private int col;
	private int row;
	private ProgressTrigger pt;

	public SelectionRunner(JTable table, Action actiontorun, ProgressTrigger pt ){
		this.table = table;
		this.action = actiontorun;
		this.pt = pt;
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		// We create a thread to be able to show the progress bar, otherwise it can not be shown.
		Thread performer = new Thread(new Runnable(){
		
			public void run(){
				// We don't mind the event...just go through the selection
				row = table.getSelectedRow();
				col = table.getSelectedColumn();
		
				// Inform observers that the process is going to start
				// TODO: get name from the action.
				pt.triggerProgressStart("Multiple cell process");
				
				// If there is something selected...
				if (row != -1) {
								
					for ( ;row <= (table.getSelectedRow() + table.getSelectedRowCount()-1) ; row++){
						
						// Calculate the cell to edit, row should be already correct and column as well
						
						// Create the event to pass a CellAutoComplete object
						ActionEvent event = new ActionEvent(new CellToAutoComplete(table, row, col,false),0,"NEW_CELL_FOUND");
						action.actionPerformed(event);
						
					}
				
				}
				
				pt.triggerPregressEnd();
			}
		});
		
		performer.start();
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