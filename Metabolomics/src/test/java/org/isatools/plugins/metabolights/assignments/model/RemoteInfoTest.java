package org.isatools.plugins.metabolights.assignments.model;

import static org.junit.Assert.*;

import org.isatools.plugins.metabolights.assignments.model.RemoteInfo.remoteProperties;
import org.isatools.plugins.metabolights.assignments.ui.EditorUI;
import org.junit.Test;


public class RemoteInfoTest {
	@Test
	public void testRemoteInfoProperties(){
		
		assertEquals("Version should be same as EditorUI", EditorUI.PLUGIN_VERSION, RemoteInfo.getProperty(remoteProperties.VERSION));
		assertEquals("Donwload URL now is google", "http://www.google.com", RemoteInfo.getProperty(remoteProperties.DOWNLOADURL));
		
	}

}
