package org.isatools.plugins.metabolights.assignments.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.JTable;

import org.isatools.isacreator.spreadsheet.SpreadsheetCellRenderer;
import org.isatools.isacreator.spreadsheet.SpreadsheetFunctions;
import org.isatools.plugins.metabolights.assignments.actions.AutoCompletionAction;
import org.isatools.plugins.metabolights.assignments.model.RemoteInfo;
import org.isatools.plugins.metabolights.assignments.model.RemoteInfo.remoteProperties;

	public class MetaboliteLinkCellRenderer extends SpreadsheetCellRenderer implements MouseListener{

		int col, row;
		JTable table;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


			if (canWeLinkIt(value.toString())){
				setText("<html><u><font color='blue'>" + value.toString());
				setToolTipText("To activate the link: Press \"Control Key\" while clicking with the mouse.");
			}else{
				setText(value.toString());
				setToolTipText("");
			}




			return this;

		}

	private void populateCoordinates(MouseEvent e){

		table = (JTable)e.getSource();
	    Point pt = e.getPoint();
	    col = table.columnAtPoint(pt);
	    row = table.rowAtPoint(pt);

	}
	public void mouseClicked(MouseEvent e) {

	    // If not double click
	    if (!(e.getClickCount()==1)) return;
	    if (!e.isControlDown()) return;

	    populateCoordinates(e);

	    // Get the index of the column. based on the name
	    int index = table.getColumnModel().getColumnIndex(AutoCompletionAction.IDENTIFIER_COL_NAME);

	    // TODO do not use 1, use the name of the column
	    if (col == index){

	    	browseMetabolite(table.getValueAt(row, col).toString());

	    }

	      //try{
	      //  Desktop.getDesktop().browse(url.toURI());
	      //}catch(Exception ex) {
	      //  ex.printStackTrace();
	      //}
	}
	private boolean canWeLinkIt(String value){

		// Get the patterns..
		String[] idPatterns = RemoteInfo.getProperty(remoteProperties.PRIORITYIDPATTERNS, "~");


		//Check if the value of the cell matches any of them
		for (int i =0;i<idPatterns.length; i++){

			String pattern = idPatterns[i];
			if (value.matches(pattern)) return true;
		}

		return false;
	}
	private void browseMetabolite(String value){
    	/**
    	 * URL Samples for ID:
    	 * CHEBI:		https://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:15365			(Whole value)
    	 * HMDB:		https://www.hmdb.ca/metabolites/HMDB03459							(Whole value)
    	 * LIPIDMAPS:	https://www.lipidmaps.org/data/LMSDRecord.php?LMID=LMFA01010001		(Whole Value)
    	 * KEGG:		https://www.genome.jp/dbget-bin/www_bget?cpd:C01401					(Whole Value)
    	 */



    	// If value is not null
    	if (value != null){

    		String[] remotePriorityPatterns = RemoteInfo.getProperty(remoteProperties.PRIORITYIDPATTERNS, "~");
    		String[] remoteAccessionURL = RemoteInfo.getProperty(remoteProperties.ACCESSION_URLS, "~");

    		for (int i =0; i <remotePriorityPatterns.length; i++){

    			// Get the pattern
    			String pattern = remotePriorityPatterns[i];

    			// If the value matches the pattern...
    			if (value.matches(pattern)){

    				// Get the url
    				String url = remoteAccessionURL[i];

    				// If the url is not empty...
    				if (!url.isEmpty()){

    					// Append the id at the end...
    					if(value.contains("CSID")){
							String[] split = value.split(" ");
							url = url + split[1] + ".html";

						}   else{
							url = url + value;
						}

    					// TODO: Editor has a static method, but this method should be better placed in an Utils class or similar..
    					EditorUI.openUrl(url);
    				}

    			}

    		}


    	}
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
