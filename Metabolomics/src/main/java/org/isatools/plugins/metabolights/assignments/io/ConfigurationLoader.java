package org.isatools.plugins.metabolights.assignments.io;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.isatab.configurator.schema.IsaTabConfigurationType;
import org.isatools.isatab.configurator.schema.IsatabConfigFileDocument;

import java.io.IOException;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/10/2011
 *         Time: 19:08
 */
public class ConfigurationLoader {
	
	private static Logger logger = Logger.getLogger(ConfigurationLoader.class);

    private static final String MS_CONFIGURATION_FILE_LOCATION  = "/metabolomics_configuration/configuration_ms.xml";
    private static final String NMR_CONFIGURATION_FILE_LOCATION = "/metabolomics_configuration/configuration_nmr.xml";

    public ConfigurationLoader() {
    }

    //Defaults to MS
    public TableReferenceObject loadConfigurationXML() throws XmlException, IOException {
        logger.info("Load MS config file");
        return loadConfigurationXML(MS_CONFIGURATION_FILE_LOCATION);
    }

    public TableReferenceObject loadNMRConfigurationXML() throws XmlException, IOException {
    	logger.info("Load NMR config file");
        return loadConfigurationXML(NMR_CONFIGURATION_FILE_LOCATION);
    }

    private TableReferenceObject loadConfigurationXML(String configFile) throws XmlException, IOException {

    	logger.info("Plugin: Loading configuration file "+configFile);

        //Load the current settings file
        IsatabConfigFileDocument configurationFile = IsatabConfigFileDocument.Factory.parse(
                getClass().getResourceAsStream(configFile)
        );

        ConfigXMLParser parser = new ConfigXMLParser("");

        //Add columns defined in the configuration file
        for (IsaTabConfigurationType doc : configurationFile.getIsatabConfigFile().getIsatabConfigurationArray()) {
            parser.processTable(doc);
        }

        if (parser.getTables().size() > 0) {
            return parser.getTables().get(0);
        }

        return null;
    }
}
