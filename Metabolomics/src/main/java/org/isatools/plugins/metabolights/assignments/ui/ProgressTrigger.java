package org.isatools.plugins.metabolights.assignments.ui;
import java.awt.Cursor;
import java.beans.*;

import javax.swing.SwingUtilities;

public class ProgressTrigger {

    private PropertyChangeSupport mPcs = new PropertyChangeSupport(this);
    public static final String PROGRESS_START = "progressStart";
    public static final String PROGRESS_END = "progressEnd";
    
    private String processDescription;
    
    public String getProcessDescription() {
		return processDescription;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        
		mPcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        
    	mPcs.removePropertyChangeListener(listener);
    }
    public void triggerProgressStart(String processDescription){
    	//At the second paste it leaves a Thread open and then mouse clicks doesn't work anymore.
    	mPcs.firePropertyChange(PROGRESS_START, null, null);
		
    	
    }
    public void triggerPregressEnd(){
    	
    	mPcs.firePropertyChange(PROGRESS_END, null, null);
		
    }
}