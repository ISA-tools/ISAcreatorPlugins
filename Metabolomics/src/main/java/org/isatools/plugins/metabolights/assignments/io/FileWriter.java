package org.isatools.plugins.metabolights.assignments.io;

import org.apache.log4j.Logger;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.SpreadsheetFunctions;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/10/2011
 *         Time: 19:08
 */
public class FileWriter {

    private static Logger logger = Logger.getLogger(FileWriter.class);

    public void writeFile(String fileName, Spreadsheet sheet) throws FileNotFoundException {
        SpreadsheetFunctions functions = new SpreadsheetFunctions(sheet);
        functions.exportTable(new File(fileName), "\t", false);
        logger.debug("Writing file "+fileName);
    }
}
