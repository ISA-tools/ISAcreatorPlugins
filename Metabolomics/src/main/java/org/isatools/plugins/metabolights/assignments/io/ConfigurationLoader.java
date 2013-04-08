package org.isatools.plugins.metabolights.assignments.io;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
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

    private static final String BASEFILE ="/metabolomics_configuration/configuration_";
    private static final String MS_CONFIGURATION  = "ms";
    private static final String NMR_CONFIGURATION = "nmr";
    private static final String FILEEXT = ".xml";


    public ConfigurationLoader() {
    }

    //Defaults to MS
    public TableReferenceObject loadConfigurationXML() throws XmlException, IOException {
        logger.info("Load MS config file");
        return loadConfigurationXML(BASEFILE + MS_CONFIGURATION + FILEEXT);
    }

    public TableReferenceObject loadNMRConfigurationXML() throws XmlException, IOException {
    	logger.info("Load NMR config file");
        return loadConfigurationXML(BASEFILE + NMR_CONFIGURATION + FILEEXT);
    }

    /*Load a specific *old* version of the config files, defaults to the current MS file if empty
    * param ConfigFileVersion, like V1, V2, V3 etc
    * param TechnologyType, like MS or NMR
    */
    public TableReferenceObject loadGenericConfig(int fileVersion ,String techologyType) throws XmlException, IOException {

        String fileToLoad;

        // MS or NMR ??
        if (techologyType.contains("NMR")){
            fileToLoad = BASEFILE + NMR_CONFIGURATION + "_v" + fileVersion + FILEEXT;
        } else {
            fileToLoad = BASEFILE + MS_CONFIGURATION  + "_v" + fileVersion + FILEEXT;
        }

        logger.info("Load " + fileToLoad + "config file");


        if (fileToLoad == null)
            fileToLoad = BASEFILE + MS_CONFIGURATION + FILEEXT;

        return loadConfigurationXML(fileToLoad);

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
