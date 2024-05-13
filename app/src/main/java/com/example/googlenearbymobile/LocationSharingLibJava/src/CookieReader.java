package com.example.googlenearbymobile.LocationSharingLibJava.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
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
        ParseString parser = new ParseString();
        // remove all quotations around strings and remove spaces between commas
        // makes it easier to parse
        List<Object> nearbyData = parser.parseString(unprocessedData[1].
                replace("\"", "").replace(", ", ","));

        // data storage
        ArrayList<People> peopleInfo = new ArrayList<>();

        // add every persons name, lat and long except for current user
        for (int i = 0; i < parser.
                grabInnerList(nearbyData, new ArrayList<>(List.of(0))).size(); i++) {
            try {
                peopleInfo.add(new People(
                        parser.grabInnerData(nearbyData, new ArrayList<>(List.of(0, i, 0, 3))),
                        Double.parseDouble(parser.grabInnerData(nearbyData,
                                new ArrayList<>(List.of(0, i, 1, 1, 2)))),
                        Double.parseDouble(parser.grabInnerData(nearbyData,
                                new ArrayList<>(List.of(0, i, 1, 1, 1))))));
            }
            // if person is offline, add null
            catch (Exception e) {
                peopleInfo.add(null);
            }
        }

        try {
            // do the same with current user
            peopleInfo.add(new People("Current User",
                    Double.parseDouble(parser.grabInnerData(nearbyData,
                            new ArrayList<>(List.of(nearbyData.size() - 1, 1, 1, 2)))),
                    Double.parseDouble(parser.grabInnerData(nearbyData,
                            new ArrayList<>(List.of(nearbyData.size() - 1, 1, 1, 1))))));
        } catch (Exception e) {
            peopleInfo.add(null);
        }

        return peopleInfo;
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

