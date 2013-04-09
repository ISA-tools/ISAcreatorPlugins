package org.isatools.plugins.metabolights.assignments.model;



import org.isatools.plugins.metabolights.assignments.model.RemoteInfo.remoteProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class RemoteInfoTest {
	@Test
	public void testRemoteInfoProperties(){
		
		assertEquals("Version should be same as EditorUI", remoteProperties.VERSION.getDefaultValue(), RemoteInfo.getProperty(remoteProperties.VERSION));
		assertEquals("Donwload URL now is under metabilights", "http://www.ebi.ac.uk/metabolights/downloadplugin", RemoteInfo.getProperty(remoteProperties.DOWNLOADURL));
		
	}

}
