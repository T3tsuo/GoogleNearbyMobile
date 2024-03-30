package com.example.googlenearbymobile.LocationSharingLibJava.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class CookieReader {

    private String url;
    Map<String, String> params;
    Map<String, String> cookies;

    public CookieReader(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void run() {
        try {
            url = "https://www.google.com/maps/rpc/locationsharing/read";
            // Define the parameters map
            params = new HashMap<>();
            params.put("authuser", "2");
            params.put("hl", "en");
            params.put("gl", "us");
            params.put("pb", "!1m7!8m6!1m3!1i14!2i8413!3i5385!2i6!3x4095!2m3!1e0!2sm!3i407105169!3m7!2sen!5e1105!12m4!1e68!2m2!1sset!2sRoadmap!4e1!5m4!1e4!8m2!1e0!1e1!6m9!1e12!2i2!26m1!4b1!30m1!1f1.3953487873077393!39b1!44e1!50e0!23i4111425");
            
        } catch (Exception e) {
            // Print the exception message
            e.printStackTrace();
        }
    }

    public ArrayList<People> getPeoples() {
        // Call the sendGetRequest method and print the result
        String[] unprocessedData;
        try {
            unprocessedData = sendGetRequest(url, params, cookies).split("'");
            // seperate people's data in 2d array
            return dataToListFormat(unprocessedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<People> dataToListFormat(String[] unprocessedData) {
        // split people data
        unprocessedData = unprocessedData[1].split("null,null,null,null,null");
        // data storage
        ArrayList<People> peopleInfo = new ArrayList<>();
        String[] tempList;
        boolean isUser = false;
        for (int i = 0; i < unprocessedData.length; i++) {
            // removes square brackets, unnessary spaces and quotations, splits into list format
            tempList = unprocessedData[i].replace("[", "").replace("]", "").
                                replace("\"", "").replace(", ",",").split(",");
            if (tempList.length != 0) {
                if (i != unprocessedData.length - 1) {
                    peopleInfo.add(findPersonData(tempList, isUser));

                } else {
                    isUser = true;
                    peopleInfo.add(findPersonData(tempList, isUser));
                }
            }
        }
        return peopleInfo;
    }

    private static People findPersonData(String[] tempList, Boolean isUser) {
        if (isUser) {
            try {
                Double longitude = Double.parseDouble(tempList[tempList.length - 12]);
                Double latitude = Double.parseDouble(tempList[tempList.length - 13]);
                return new People("Current User", longitude, latitude);
            } catch (Exception e) {
                return new People("Current User");
            }
        }
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].contains("googleusercontent")) {
                String name = tempList[i + 2];
                if (!name.matches("^[A-Za-z- ]+")) {
                    return new People("Error");
                }
                try {
                    Double longitude = Double.parseDouble(tempList[i + 10]);
                    Double latitude = Double.parseDouble(tempList[i + 9]);
                    return new People(name, longitude, latitude);
                } catch (Exception e) {
                    return new People(name);
                }
            }
        }
        return null;
    }

    // Define a method to send a GET request and return the response as a string
    private static String sendGetRequest(String url, Map<String, String> params, Map<String, String> cookies) throws Exception {
        // Build the query string from the parameters map
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            query.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            query.append("=");
            query.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            query.append("&");
        }
        // Remove the last "&" if any
        if (query.length() > 0) {
            query.deleteCharAt(query.length() - 1);
        }
        // Append the query string to the url
        url = url + "?" + query;
        // Create a URL object
        URL obj = new URL(url);
        // Open a connection
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // Set the request method to GET
        con.setRequestMethod("GET");
        // Build the cookie string from the cookies map
        StringBuilder cookie = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookie.append(entry.getKey());
            cookie.append("=");
            cookie.append(entry.getValue());
            cookie.append("; ");
        }
        // Remove the last "; " if any
        if (cookie.length() > 0) {
            cookie.delete(cookie.length() - 2, cookie.length());
        }
        // Set the request property "Cookie" with the cookie string
        con.setRequestProperty("Cookie", cookie.toString());
        // Get the response code
        int responseCode = con.getResponseCode();
        // If the response code is 200 (OK), read the response body
        if (responseCode == 200) {
            // Create a buffered reader to read the input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            // Create a string builder to store the response
            StringBuilder response = new StringBuilder();
            // Read each line of the response
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Append the line to the response
                response.append(inputLine);
            }
            // Close the buffered reader
            in.close();
            // Return the response as a string
            return response.toString();
        } else {
            // If the response code is not 200 (OK), throw an exception
            throw new Exception("GET request failed: " + responseCode);
        }
    }
}

