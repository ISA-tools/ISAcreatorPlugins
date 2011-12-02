package org.isatools.plugins.metabolights.assignments.model;

import static org.junit.Assert.*;

import org.isatools.plugins.metabolights.assignments.model.RemoteInfo.remoteProperties;
import org.isatools.plugins.metabolights.assignments.ui.EditorUI;
import org.junit.Test;


public class RemoteInfoTest {
	@Test
	public void testRemoteInfoProperties(){
		
		assertEquals("Version should be same as EditorUI", remoteProperties.VERSION.getDefaultValue(), RemoteInfo.getProperty(remoteProperties.VERSION));
		assertEquals("Donwload URL now is under metabilights", "http://www.ebi.ac.uk/metabolights/downloadplugin", RemoteInfo.getProperty(remoteProperties.DOWNLOADURL));
		
	}

}
