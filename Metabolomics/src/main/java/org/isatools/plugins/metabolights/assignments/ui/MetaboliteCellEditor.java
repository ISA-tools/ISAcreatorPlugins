package org.isatools.plugins.metabolights.assignments.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.spreadsheet.SpreadsheetCell;
import org.isatools.plugins.metabolights.assignments.actions.AutoCompletionAction;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;
import org.isatools.plugins.metabolights.assignments.model.OptionalMetabolitesList;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.text.Caret;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class MetaboliteCellEditor extends DefaultCellEditor implements TableCellEditor{
		
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//For editing
	static JTextField text = new JTextField();
	
	// For customize options
	static JPanel panel;
	
	static JLabel showMetList;
	private DataEntrySheet des;
	private JTable table;
	private EventObject event;
	Metabolite[] metabolites;
	
	@InjectedResource
    private ImageIcon showMoreIcon;
	
    public MetaboliteCellEditor(DataEntrySheet des) {
			super(text);
			this.des =des;
			this.table = des.getSheet().getTable(); 
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
			text.setSelectedTextColor(Color.CYAN);
			//text.setPreferredSize(new Dimension(50,20));
			
			// Pre-configure the icon
			showMetList = new JLabel(showMoreIcon);
			showMetList.setToolTipText("There are more metabolites");
			showMetList.setFocusable(false);
			showMetList.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent mouseEvent) {

	            	//MESSAGE DIALOG OPTION	            	
	            	Metabolite met = chooseAMetabolite();
	            	
	            	if (met !=null){
	            		
	            		// Add it to the OptionalMetabolites to avoid a new search in pubchem
	            		OptionalMetabolitesList.getObject().setMetabolitesForTerm(new Metabolite[]{met}, met.getDescription());
	            		text.setText(met.getDescription());
	            		des.setForceAutoComplete(true);
	            		//table.setValueAt("", table.getEditingRow(), table.getColumnModel().getColumnIndex(AutoCompletionAction.FORMULA_COL_NAME));
	            		//table.setValueAt("", table.getEditingRow(), table.getColumnModel().getColumnIndex(AutoCompletionAction.IDENTIFIER_COL_NAME));
	            	}

	            }
	            private Metabolite chooseAMetabolite(){
	            	
	            	// Get the options  
	            	Metabolite[] mets = OptionalMetabolitesList.getObject().getMetabolitesForTerm(text.getText());
	
	            	Metabolite s = (Metabolite)JOptionPane.showInputDialog(
	            	                    panel,
	            	                    "We have found " + mets.length + " metabolites for \"" + text.getText() + "\".\nPlease choose the appropriate one:",
	            	                    "Choose a metabolite:\n",
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
			//panel = new JPanel();
			// From http://www.jroller.com/santhosh/entry/keyboard_handling_in_tablecelleditor
			// Override some methods in order to avoid the panel to get the focus.
			panel = new JPanel(new BorderLayout()){
//	            public void addNotify(){
//	                super.addNotify();
//	                text.requestFocus();
//	             
//	            }
				public void requestFocus(){
					text.requestFocus();
				}

	            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed){
	                InputMap map = text.getInputMap(condition);
	                ActionMap am = text.getActionMap();

	                if(map!=null && am!=null && isEnabled()){
	                    Object binding = map.get(ks);
	                    Action action = (binding==null) ? null : am.get(binding);
	                    if(action!=null){
	                        return SwingUtilities.notifyAction(action, ks, e, text,
	                                e.getModifiers());
	                    }
	                }
	                return false;
	            }
	        };

			// Add components (Text + icon)
	        panel.setRequestFocusEnabled(true);
			panel.add(text);
			panel.add(showMetList, BorderLayout.EAST);
			text.setFont(UIHelper.VER_11_PLAIN);
			
			
    }
		
	private void updateData(SpreadsheetCell newMetabolite, boolean isSelected, JTable table, int row) {
		
		String value = (newMetabolite != null)?newMetabolite.toString():"";
		
		// Custom option
		text.setText(value);
	
		panel.setForeground(UIHelper.BG_COLOR);
		panel.setBackground(UIHelper.DARK_GREEN_COLOR);
		
		text.setForeground(panel.getForeground());
		text.setBackground(panel.getBackground());
		
		// Avoid loosing the first character when editing with the keyboard
        if(event instanceof KeyEvent || event==null)
        {
            final Caret caret = text.getCaret();
            caret.setDot(0);
            text.setText("");                
        }

		// If there is more than one metabolite
		if (doWeHaveAListOfMetabolites(value)){
			showMetList.setIcon(showMoreIcon);
		}else{
			showMetList.setIcon(null);
		}
		
		
	}
	public boolean isCellEditable(EventObject anEvent)
    {
        event=anEvent;
        return super.isCellEditable(anEvent);
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

		updateData(newMetabolite, true, table, row);
		
		return panel;
	}

	public Object getCellEditorValue() {
		
		// Custom option
		return text.getText();
	}
}

