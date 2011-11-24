package org.isatools.plugins.metabolights.assignments.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
 
public class RemoteInfo {
	
	static Properties props = getRemoteInfo();
	static public enum remoteProperties{
		VERSION, DOWNLOADURL, PRIORITYIDPATTERNS;
		public String toString(){
			return name().toString();
		}
	}
	static Properties getRemoteInfo(){
		String reqUrl = "https://raw.github.com/EBI-Metabolights/ISAcreatorPlugins/master/Metabolomics/remoteInfo.properties";
        try {
        	
        	Properties props = new Properties();
            URL url = new URL(reqUrl.toString());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            
            props.load(url.openStream());
            
            return props;
        } catch (IOException e) {
            e.printStackTrace();
           
        }
        return null;

	}
	static public String getProperty(remoteProperties propertyName){
		
		if (props == null) return null;
		
		return  props.getProperty(propertyName.toString());
		
	}
	static public String getProperty(remoteProperties propertyName, String defaultValue){
		
		String remoteValue = getProperty(propertyName);
		
		return remoteValue==null?defaultValue:remoteValue;
	}
	
	
}