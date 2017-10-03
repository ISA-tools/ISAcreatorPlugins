package org.isatools.plugins.metabolights.assignments.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

public class RemoteInfo {

	static Properties props = getRemoteInfo();

	static public enum remoteProperties{

		// When starting a new version:
		// 1.- Change the VERSION default value to the new version
		// 2.- Create a new remoteProperty file --> remoteInfo2.properties
		// 3.- Change the url to point to the new property file (reqUrl in getRemoteInfo method)
		VERSION ("0.4"),
		DOWNLOADURL("https://www.ebi.ac.uk/metabolights/?message=Please%20download%20a%20new%20version%20of%20the%20plugin%20from%20ftp://ftp.ebi.ac.uk/pub/databases/metabolights/submissionTool/plugin/"),
		PRIORITYIDPATTERNS("^CHEBI:[0-9]+$~^CSID\\s[0-9]+$~^HMDB[0-9]+$~^LM[A-Z]{2}[0-9]+$~^C[0-9]{5}$"),
		ACCESSION_URLS("https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" +
				      "~https://www.chemspider.com/Chemical-Structure." +
					  "~https://www.hmdb.ca/metabolites/" +
					  "~https://www.lipidmaps.org/data/LMSDRecord.php?LMID=" +
					  "~https://www.genome.jp/dbget-bin/www_bget?cpd:"),
		PUBCHEMFIELD_FOR_DESCRIPTION("CompleteSynonym"),
		PUBCHEMFIELD_FOR_FORMULA("All Fields"),
		PUBCHEMFIELD_FOR_ID("CompleteSynonym"),
		PUBCHEM_MAX_RECORD("10"),
		PUBCHEM_BLACKLIST("~unknown~");


		String defaultValue;
		private remoteProperties(String defaultValue){
			this.defaultValue= defaultValue;
		}

		public String getDefaultValue(){
			return this.defaultValue;
		}

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

	static private String getProperty(String propertyName){

		if (props == null) return null;

		return  props.getProperty(propertyName);

	}

	static public String getProperty(remoteProperties propertyName){

		String remoteValue = getProperty(propertyName.toString());

		return remoteValue==null?propertyName.getDefaultValue():remoteValue;
	}

	static public String[] getProperty(remoteProperties propertyName, String splitText){
		String property = getProperty(propertyName);

		return property.split(splitText);
	}


}
