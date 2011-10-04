package org.isatools.plugins.metabolights.assignments.io;

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

    public TableReferenceObject loadFile(String fileName, TableReferenceObject referenceObject) {
        SpreadsheetImport spreadsheetImport = new SpreadsheetImport();
        try {
            return spreadsheetImport.loadInTables(fileName, referenceObject);
        } catch (IOException e) {
            return null;
        } catch (MalformedInvestigationException e) {
            return null;
        }
    }
}
