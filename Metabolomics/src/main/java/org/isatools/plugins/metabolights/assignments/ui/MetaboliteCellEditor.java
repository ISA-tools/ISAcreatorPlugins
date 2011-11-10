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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.spreadsheet.SpreadsheetCell;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

public class MetaboliteCellEditor extends AbstractCellEditor implements TableCellEditor{
		
	JPanel panel;
	JLabel label;
	JTextField text;
	SpreadsheetCell cell;
	Metabolite[] metabolites;
	
	@InjectedResource
    private ImageIcon showMoreIcon;
	
    public MetaboliteCellEditor() {
		
			ResourceInjector.get("metabolights-fileeditor-package.style").inject(this);
			
			text = new JTextField() {
			    @Override public void setBorder(Border border) {
			        // No!
			    }
			};
			text.setHorizontalAlignment(JTextField.LEFT);
			text.setPreferredSize(new Dimension(50,20));
			
			final JLabel showMetList = new JLabel(showMoreIcon);
			showMetList.setToolTipText("There are more metabolites");
			showMetList.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent mouseEvent) {
	            	
	            	JOptionPane.showMessageDialog(null, "Show more metbolites");

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
			
			if (cell != null){
				text.setText(cell.toString());
			}else{
				text.setText("");
			}
			

		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			
			SpreadsheetCell newMetabolite = (SpreadsheetCell)value;

			updateData(newMetabolite, true, table);
			
			return panel;
		}

		public Object getCellEditorValue() {
			return text.getText();
		}
	}

