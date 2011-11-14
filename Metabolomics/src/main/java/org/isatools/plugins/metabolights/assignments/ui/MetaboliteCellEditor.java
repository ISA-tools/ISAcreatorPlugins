package org.isatools.plugins.metabolights.assignments.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
		
	JPanel panel;
	JLabel showMetList;
	JTextField text;
	JComboBox metList;
	
	Metabolite[] metabolites;
	
	@InjectedResource
    private ImageIcon showMoreIcon;
	
    public MetaboliteCellEditor() {
		
			ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
			
			setUpOldStyle();
			//comboSetUp();
    }
    private void setUpOldStyle(){
			
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
	            	
//	            	Metabolite[] mets = OptionalMetabolitesList.getObject().getMetabolitesForTerm(text.getText());
//	            	
//	            	JList list = new JList(mets); //data has type Object[]
//	            	list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//	            	list.setLayoutOrientation(JList.VERTICAL);
//	            	list.setVisibleRowCount(5);
//	            	JScrollPane listScroller = new JScrollPane(list);
//	            	listScroller.setPreferredSize(new Dimension(250, 80));
//	            	panel.add(list);
//	            	panel.setComponentZOrder(list, 0);
//	            	list.setVisible(true);
//	            	
//	            	
	            	

//MESSAGE DIALOG OPTION	            	
	            	String metabolites = "";
	            	
	            	Metabolite[] mets = OptionalMetabolitesList.getObject().getMetabolitesForTerm(text.getText());
	            	
	            	for (Metabolite met:mets){
	            		metabolites = metabolites + met.getDescription() + "(" + met.getIdentifier() + "), " + met.getFormula() + "\n";
	            	}
	            	
	            	JOptionPane.showMessageDialog(null, metabolites);

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
    private void comboSetUp(){
    	
    	
    	
    	text = new JTextField() {
		    @Override public void setBorder(Border border) {
		        // No!
		    }
		};
    	
    	metList = new JComboBox();
    	metList.setEditable(true);
    	UIHelper.setJComboBoxAsHeavyweight(metList);
    	//patternList.addActionListener(this);
    	
    	
		// Create a panel to contain the text and the icon
		panel = new JPanel();

		
		// Using springlayout:  see http://download.oracle.com/javase/tutorial/uiswing/layout/spring.html
		SpringLayout lo = new SpringLayout();
		
		panel.setLayout(lo);
		
		// Create a constrain to force the icon to be on the right
		//lo.putConstraint(SpringLayout.EAST, metList, 0, SpringLayout.EAST, panel);
		//lo.putConstraint(SpringLayout.WEST, metList, 0, SpringLayout.WEST, panel);
		
		
		// Add components (Text + icon)
		//panel.add(metList);
    	
    	
    }
		
		private void updateData(SpreadsheetCell newMetabolite, boolean isSelected, JTable table) {
			//this.metabolite = metabolite;
			
			String value = (newMetabolite != null)?newMetabolite.toString():"";
			
			
			// Combo option
//			metList.setSelectedItem(value);
//			text.setText(value);
//			fillEditorWithMetabolites(value);
			

			// Custom option
			text.setText(value);
			if (!OptionalMetabolitesList.getObject().isThereMetabolitesForTerm(text.getText())){
				showMetList.setIcon(null);
			}else{
				showMetList.setIcon(showMoreIcon);
			}
			
			
		}
		private void fillEditorWithMetabolites(String value){

			metList.removeAllItems();
			
			// If there are optional metabolites for this text
			if (OptionalMetabolitesList.getObject().isThereMetabolitesForTerm(value)){
				
				Metabolite[] mets = OptionalMetabolitesList.getObject().getMetabolitesForTerm(value);
				
				// If there is more than one metabolte
				if (mets.length>1){
					// Go through all of them
					for (int i =0; i<mets.length; i++){
						
						//metList.addItem(mets[i].getDescription() + " - " + mets[i].getFormula() + " (" + mets[i].getIdentifier() + ")");
						metList.addItem(mets[i].getDescription());
						
					}
				}
			}
			
		}
		private Metabolite[] getMetaboliteList(String value){
			// If there are optional metabolites for this text
			if (OptionalMetabolitesList.getObject().isThereMetabolitesForTerm(value)){
				
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
			// Combo option
//			if (metList.getItemCount() == 0){
//				return text;
//			}else{
//
//				return metList;
//			}
		}

		public Object getCellEditorValue() {
			
			// Custom option
			return text.getText();
			
//			if (metList.getItemCount()==0){
//				return text.getText();
//			}else{
//				return (metList.getSelectedItem()==null)?"":metList.getSelectedItem().toString();
//			}
		}
	}

