package org.isatools.plugins.metabolights.assignments.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.spreadsheet.SpreadsheetCell;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;
import org.isatools.plugins.metabolights.assignments.model.OptionalMetabolitesList;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

public class MetaboliteCellEditor extends AbstractCellEditor implements TableCellEditor{
		
	// For both options
	JTextField text;
	
	// For customize options
	JPanel panel;
	JLabel showMetList;
	
	Metabolite[] metabolites;
	
	@InjectedResource
    private ImageIcon showMoreIcon;
	
    public MetaboliteCellEditor() {
		
			ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
			
			customeStyleSetUp();
    }
    private void customeStyleSetUp(){
			
			text = new JTextField() {
			    @Override public void setBorder(Border border) {
			        // No!
			    }
			};
			text.setHorizontalAlignment(JTextField.LEFT);
			text.setPreferredSize(new Dimension(50,20));
			
			showMetList = new JLabel(showMoreIcon);
			showMetList.setToolTipText("There are more metabolites");
			showMetList.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent mouseEvent) {

	            	//MESSAGE DIALOG OPTION	            	
	            	//showMetabolites();
	            	Metabolite met = chooseAMetabolite();
	            	
	            	if (met !=null){
	            		text.setText(met.getDescription());
	            	}

	            }
	            private Metabolite chooseAMetabolite(){
	            	
	            	// Get the options
	            	Metabolite[] mets = OptionalMetabolitesList.getObject().getMetabolitesForTerm(text.getText());
	
	            	Metabolite s = (Metabolite)JOptionPane.showInputDialog(
	            	                    panel,
	            	                    "Choose a metabolite:\n",
	            	                    "Chose a metabolite",
	            	                    JOptionPane.PLAIN_MESSAGE,
	            	                    null,
	            	                    mets,
	            	                    text.getText());

	            	//If a string was returned, say so.
	            	if ((s != null)) {
	            	    
	            	    return s;
	            	}

	            	return null;
	            }
	        });
			
			// Create a panel to contain the text and the icon
			panel = new JPanel();
			
			// Using springlayout:  see http://download.oracle.com/javase/tutorial/uiswing/layout/spring.html
			SpringLayout lo = new SpringLayout();
			
			panel.setLayout(lo);
			
			// Create a constrain to force the icon to be on the right
			lo.putConstraint(SpringLayout.EAST, showMetList, 0, SpringLayout.EAST, panel);
			lo.putConstraint(SpringLayout.EAST, text, 1, SpringLayout.WEST, showMetList);
			lo.putConstraint(SpringLayout.WEST, text, 0, SpringLayout.WEST, panel);
			
			
			// Add components (Text + icon)
			panel.add(text);
			panel.add(showMetList);
			panel.setForeground(UIHelper.BG_COLOR);
			panel.setBackground(UIHelper.DARK_GREEN_COLOR);
			text.setForeground(panel.getForeground());
			text.setBackground(panel.getBackground());
			text.setFont(UIHelper.VER_11_PLAIN);
			//text.setMargin(new Insets(-10, -10, -10, -10));
			
    }
		
	private void updateData(SpreadsheetCell newMetabolite, boolean isSelected, JTable table) {
		//this.metabolite = metabolite;
		
		String value = (newMetabolite != null)?newMetabolite.toString():"";
		
		
		// Custom option
		text.setText(value);
		
		// If there is more than one metabolite
		if (doWeHaveAListOfMetabolites(value)){
			showMetList.setIcon(showMoreIcon);
		}else{
			showMetList.setIcon(null);
		}
		
	}
		
	private Metabolite[] getMetaboliteList(String value){
		// If there are optional metabolites for this text
		if (OptionalMetabolitesList.getObject().areThereMetabolitesForTerm(value)){
			
			return OptionalMetabolitesList.getObject().getMetabolitesForTerm(value);
		}else{
			return null;
		}
	}
	private boolean doWeHaveAListOfMetabolites(String value){
		
		Metabolite[] mets = getMetaboliteList(value);
		
		if (mets == null || mets.length==1){
			return false;
		} else{
			return true;
		}
	}
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		
		SpreadsheetCell newMetabolite = (SpreadsheetCell)value;

		updateData(newMetabolite, true, table);
		
		return panel;
	}

	public Object getCellEditorValue() {
		
		// Custom option
		return text.getText();
	}
}

