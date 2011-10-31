package org.isatools.plugins.metabolights.assignments.io;

import org.apache.log4j.Logger;
import org.isatools.isacreator.io.importisa.SpreadsheetImport;
import org.isatools.isacreator.io.importisa.errorhandling.exceptions.MalformedInvestigationException;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;

import java.io.IOException;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/10/2011
 *         Time: 19:08
 */
public class FileLoader {

    private static Logger logger = Logger.getLogger(FileLoader.class);

    public TableReferenceObject loadFile(String fileName, TableReferenceObject referenceObject) {
        SpreadsheetImport spreadsheetImport = new SpreadsheetImport();
        try {
            return spreadsheetImport.loadInTables(fileName, referenceObject);
        } catch (IOException e) {
            logger.error(e.getMessage().toString());
            return null;
        } catch (MalformedInvestigationException e) {
            //If there are columns in the spreadsheet that are no longer present in the sheet definition, return the existing tableReferenceObject
            //the is-hidden="true" attribute in the configuration files will remove a column from the file
            //logger.error(e.getMessage().toString());
            return referenceObject;
        }
    }
}
