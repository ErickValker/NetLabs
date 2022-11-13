package ru.nsu.g20206.valker.netLab1.model;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GlobalPlaceAPI {
    ArrayList<PlaceConfig> places = new ArrayList<>();

    public ArrayList<PlaceConfig> getPlaces(){
        return places;
    }

    public void parseUrlData(String text) throws IOException, ParseException {
        ParseManager parseManager = new ParseManager();
        URL url = new URL("https://graphhopper.com/api/1/geocode?q=" + text.replaceAll(" ", "%20") + "&key=" + parseManager.getPropertyValue("GRAPHHOPPER_API_KEY"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            JsonParse jsonParse = new JsonParse();
            places = jsonParse.globalPlaceParse(text, url);
        }
    }
}

