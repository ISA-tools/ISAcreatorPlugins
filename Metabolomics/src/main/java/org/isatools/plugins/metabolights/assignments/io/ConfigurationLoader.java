package org.isatools.plugins.metabolights.assignments.io;

import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.isatab.configurator.schema.IsaTabConfigFileType;
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

    private static final String CONFIGURATION_FILE_LOCATION = "/metabolomics_configuration/configuration.xml";

    public ConfigurationLoader() {
    }

    public TableReferenceObject loadConfigurationXML() throws XmlException, IOException {

        IsatabConfigFileDocument configurationFile = IsatabConfigFileDocument.Factory.parse(
                getClass().getResourceAsStream(CONFIGURATION_FILE_LOCATION));

        ConfigXMLParser parser = new ConfigXMLParser("");

        for (IsaTabConfigurationType doc : configurationFile.getIsatabConfigFile().getIsatabConfigurationArray()) {
            parser.processTable(doc);
        }

        if (parser.getTables().size() > 0) {
            return parser.getTables().get(0);
        }

        return null;
    }
}
