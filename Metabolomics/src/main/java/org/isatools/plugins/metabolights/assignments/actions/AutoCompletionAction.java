package org.isatools.plugins.metabolights.assignments.actions;

import org.apache.log4j.Logger;
import org.isatools.plugins.metabolights.assignments.model.Metabolite;
import org.isatools.plugins.metabolights.assignments.ui.ProgressTrigger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoCompletionAction extends AbstractAction {

    private static Logger logger = Logger.getLogger(AutoCompletionAction.class);

    public static final String IDENTIFIER_COL_NAME = "database_identifier";       //Changed from identifier
    public static final String FORMULA_COL_NAME = "chemical_formula";
    public static final String DESCRIPTION_COL_NAME = "metabolite_identification";   //Changed from identification
    public static final String INCHI = "inchi";                  //mzTAB define the InChIKey, but this is not really useful so we adopt InChI instead
    public static final String SMILES = "smiles";


    CellToAutoComplete source;
    JTable table;
    String currentCellValue;
    ProgressTrigger progressTrigger;


    public ProgressTrigger getProgressTrigger() {
        return progressTrigger;
    }

    public void setProgressTrigger(ProgressTrigger progressTrigger) {
        this.progressTrigger = progressTrigger;
    }

    public void actionPerformed(ActionEvent e) {

        //Get the object that has generated the event
        source = (CellToAutoComplete) e.getSource();
        table = source.getTable();

        // Get the value of the source cell
        currentCellValue = table.getValueAt(source.getRow(), source.getCol()).toString();

        // If it's empty
        if (currentCellValue == null || currentCellValue.equals("")) return;

        // If there isn't anything to autocomplete
        if (!isThereAnythingToAutocomplete()) return;

        //If we have a progress trigger instance
        if (progressTrigger != null)
            progressTrigger.triggerProgressStart("Looking up metabolites for " + currentCellValue);

        // Get the Metabolite object based on the column
        Metabolite met = getMetabolite();

        // Populate the Autocomplete columns
        autoCompleteColumns(met);

        //If we have a progress trigger instance
        if (progressTrigger != null) progressTrigger.triggerPregressEnd();
    }

    private void autoCompleteColumns(Metabolite met) {

        // If metabolite is null there is nothing to fill
        if (met == null) return;

        setColumn(FORMULA_COL_NAME, met.getFormula());
        setColumn(DESCRIPTION_COL_NAME, met.getDescription());
        setColumn(IDENTIFIER_COL_NAME, met.getIdentifier());
        setColumn(INCHI, met.getInchi());
        setColumn(SMILES, met.getSmiles());

        table.validate();

    }

    private void setColumn(String columnName, String value) {


        // Only set the value if empty...
        if (isColumnEmpty(columnName) || source.getForce()) {

            int colIndex = getColIndexByName(columnName);

            table.setValueAt(value, source.getRow(), colIndex);
        }

    }

    private boolean isThereAnythingToAutocomplete() {

        // If autocomplete is forced
        if (source.getForce()) return true;

        int emptyCells = 0;

        if (isColumnEmpty(FORMULA_COL_NAME)) emptyCells++;
        if (isColumnEmpty(DESCRIPTION_COL_NAME)) emptyCells++;
        if (isColumnEmpty(IDENTIFIER_COL_NAME)) emptyCells++;
        if (isColumnEmpty(SMILES)) emptyCells++;
        if (isColumnEmpty(INCHI)) emptyCells++;

        return (emptyCells > 0);
    }

    private boolean isColumnEmpty(String columnName) {

        // Get the column index
        int columnIndex = getColIndexByName(columnName);

        // Get the value of the cell
        String value = table.getValueAt(source.getRow(), columnIndex).toString();

        // If It's empty
        if (value == null || value.equals("")) return true;

        // Otherwise
        return false;

    }

    private int getColIndexByName(String columnName) {
        return table.getColumnModel().getColumnIndex(columnName);
    }

    // Get a metabolite instance based on the active cell
    private Metabolite getMetabolite() {

        // Get the current column name
        String columnName = table.getColumnName(source.getCol());
        return getMetaboliteFromMetaboLightWS(columnName, currentCellValue);

    }


    private static String[] splitValues(String inputString, String stringSeparator) {
        return inputString.split(stringSeparator);
    }

    public static Metabolite getMetaboliteFromMetaboLightWS(String columnName, String value) {

        String metabolightsWSpath = getMetaboLightsWSSearchPath(columnName);
        String[] values = splitValues(value, "\\|");
        int size = values.length;
        List<Metabolite> eachPipeEntry = new ArrayList<Metabolite>();
        try {
            for (int i = 0; i < size; i++) {
                String currentValue = values[i];
                if (containsUnknown(currentValue)) {
                    Metabolite metabolite = new Metabolite();
                    metabolite.setDescription("unknown");
                    eachPipeEntry.add(metabolite);
                } else {
                    String response = queryMetaboLightWS(currentValue, metabolightsWSpath);
                    List<Metabolite> metabolites = parseMetabolitesInfoFrom(response);
                    if (metabolites.size() != 0) {
                        eachPipeEntry.add(metabolites.get(0));
                    } else {
                        eachPipeEntry.add(new Metabolite());
                    }
                }
            }
        }catch(Exception e){
          logger.info("Something went wrong while querying: " + value, e);
        }

        return combineInfoFrom(eachPipeEntry);

    }

    private static boolean containsUnknown(String searchTerm) {
        if (searchTerm.toLowerCase().contains("unknown") || searchTerm.toLowerCase().contains("unidentified")) {
            logger.info("Ignoring " + searchTerm);
            return true;
        }
        return false;
    }

    public static String getMetaboLightsWSSearchPath(String columnName) {

        if (DESCRIPTION_COL_NAME.equals(columnName)) {
            return "name";
        } else if (INCHI.equals(columnName)) {
            return "inchi";
        } else if (SMILES.equals(columnName)) {
            return "smiles";
        } else if (IDENTIFIER_COL_NAME.equals(columnName)) {
            return "databaseid";  //todo
        } else {
            return "";
        }
    }

    private static String queryMetaboLightWS(String currentValue, String metabolightsWSpath) {
        //path to ws, construct ws path from input, send off query, parse json, populate metabolite
        String response = "", getURL = "";
        currentValue = currentValue.trim();
        if (metabolightsWSpath.equals("inchi") || metabolightsWSpath.equals("smiles")) {
            getURL = "https://www.ebi.ac.uk/metabolights/webservice/genericcompoundsearch/" + metabolightsWSpath;
            response = Client.executeRequest(getURL, "POST", currentValue);

        } else {
            getURL = "https://www.ebi.ac.uk/metabolights/webservice/genericcompoundsearch/" + metabolightsWSpath + "/" + Client.encoded(currentValue);
            response = Client.executeRequest(getURL, "GET", "");
        }
        return response;
    }

    private static List<Metabolite> parseMetabolitesInfoFrom(String searchResponse) {
        if (searchResponse == null) {
            return null;
        }
        try {
            JSONObject searchResult = new JSONObject(searchResponse);
            JSONArray compoundHits = searchResult.getJSONArray("content");
            return parse(compoundHits);

        } catch (JSONException e) {
            e.printStackTrace();
            logger.error("Something went wrong while parsing searchResponse: " + searchResponse , e);
            return new ArrayList<Metabolite>();
        }
    }


    private static List<Metabolite> parse(JSONArray compoundHits) {
        List<Metabolite> parsedHits = new ArrayList<Metabolite>();
        for (int i = 0; i < compoundHits.length(); i++) {
            try {
                //convert json string to object
                parsedHits.add(parseObject(compoundHits.get(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parsedHits;

    }

    private static Metabolite parseObject(Object o) {
        JSONObject obj = (JSONObject) o;
        Metabolite metabolite = new Metabolite();
        try {
//            metabolite.setIdentifier(obj.getString("chebiId"));
//            metabolite.setInchi(obj.getString("inchi"));
//            metabolite.setSmiles(obj.getString("smiles"));
//            metabolite.setFormula(obj.getString("formula"));
//            metabolite.setDescription(obj.getString("name"));
            metabolite.setIdentifier(get(obj, "databaseId"));
            metabolite.setInchi(get(obj, "inchi"));
            metabolite.setSmiles(get(obj, "smiles"));
            metabolite.setFormula(get(obj, "formula"));
            metabolite.setDescription(get(obj, "name"));

        } catch (Exception e) {
            logger.error("Something went wrong while parsing :" + o , e);
            e.printStackTrace();
        }
        return metabolite;
    }

    private static String get(JSONObject object, String field) throws JSONException {
        String value = object.getString(field);
        return value == null || value.equals("null") ? "" : value;
    }

    private static Metabolite combineInfoFrom(List<Metabolite> metabolites) {
        if (!metabolites.isEmpty()) {
            if (metabolites.size() == 1) {
                return metabolites.get(0);
            } else {
                Metabolite firstMetabolite = metabolites.get(0);
                for (int i = 1; i < metabolites.size(); i++) {
                    firstMetabolite.setIdentifier(concat(firstMetabolite.getIdentifier(), metabolites.get(i).getIdentifier()));
                    firstMetabolite.setDescription(concat(firstMetabolite.getDescription(), metabolites.get(i).getDescription()));
                    firstMetabolite.setSmiles(concat(firstMetabolite.getSmiles(), metabolites.get(i).getSmiles()));
                    firstMetabolite.setInchi(concat(firstMetabolite.getInchi(), metabolites.get(i).getInchi()));
                    firstMetabolite.setFormula(concat(firstMetabolite.getFormula(), metabolites.get(i).getFormula()));
                }
                return firstMetabolite;
            }
        }
        return null;
    }

    private static String concat(String firstMetabolite, String currentMetabolite) {
        String concated = "";
        if (firstMetabolite != null) {
            if (currentMetabolite != null) {
                concated = firstMetabolite + "|" + currentMetabolite;
                return concated;
            }
            return firstMetabolite + "|";
        } else {
            if (currentMetabolite != null) {
                concated = "|" + currentMetabolite;
                return concated;
            }
            return "|";
        }
    }

    public static boolean matchRegEx(String text, String patternToSearch) {

        Pattern pattern = Pattern.compile(patternToSearch);

        Matcher matcher = pattern.matcher(text);

        // Return true if found
        return matcher.find();


    }

}
