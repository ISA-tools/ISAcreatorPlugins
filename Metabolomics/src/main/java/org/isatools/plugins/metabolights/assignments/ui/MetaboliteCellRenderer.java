package org.isatools.plugins.metabolights.assignments.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.isatools.isacreator.spreadsheet.SpreadsheetCellRenderer;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;
import org.isatools.plugins.metabolights.assignments.model.OptionalMetabolitesList;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

public class MetaboliteCellRenderer extends SpreadsheetCellRenderer{
	@InjectedResource
    private ImageIcon showMoreIcon;
	
	public MetaboliteCellRenderer(){
		super();
		ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
		// Set the position of the text related to the icon (we want the icon on the RIGHT)
		setHorizontalTextPosition(LEFT);
	}
	
	

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// If there are alternative metabolites
		String text = getText();
		
		Metabolite[] mets = OptionalMetabolitesList.getObject().getMetabolitesForTerm(text);
		
		if (mets != null && mets.length>1){
			setIcon(showMoreIcon);
		} else{
			setIcon(null);
		}
		
		// Set the alignment AFTER super call, otherwise it will be overridden.
		setHorizontalAlignment(JTextField.RIGHT);
		
		return this;
		
	}
	

}
