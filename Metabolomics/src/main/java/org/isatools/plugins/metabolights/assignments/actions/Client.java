package org.isatools.plugins.metabolights.assignments.actions;



import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Client {

    private static Logger logger = Logger.getLogger(Client.class);
    public static String executeRequest(String requestURL, String method, String postBody) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            //String encodedUrl = URLEncoder.encode(requestURL, "UTF-8");

            URL url = new URL(requestURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);      // "GET"
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty( "charset", "utf-8");
            connection.setDoOutput(true);

            if (method.equalsIgnoreCase("post")) {
                if (!postBody.isEmpty()) {
                    OutputStream os = connection.getOutputStream();
                    os.write(postBody.getBytes());
                    os.flush();
                }
            }
          //  logger.info(requestURL + " " + method + " " + postBody);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                throw new RuntimeException("MetaboLights Java WS client: " + connection.getURL().toString() + "(" + method + ") request failed : HTTP error code : "
//                        + connection.getResponseCode());
                System.err.println("MetaboLights Java WS client: " + connection.getURL().toString() + "(" + method + ") request failed : HTTP error code : "
                        + connection.getResponseCode());
                logger.error("MetaboLights Java WS client: " + connection.getURL().toString() + "(" + method + ") request failed : HTTP error code : "
                        + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            String message = org.apache.commons.io.IOUtils.toString(br);

            connection.disconnect();

            return message;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Something went wrong while requesting: " + requestURL , e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    public static String encoded(String searchTerm) {
        try {
            searchTerm = searchTerm.replaceAll("/","__");
            searchTerm = searchTerm.replaceAll("\\.","_&_");
            String encoded = URLEncoder.encode(searchTerm, "UTF-8");
            encoded = encoded.replaceAll("\\+","%20");
            return encoded;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("Something went wrong while encoding " + searchTerm ,e);
            return searchTerm;
        }
    }
	
}    
