package ru.nsu.g20206.valker.netLab1.model;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {

    private PlaceConfig weatherInPlace = new PlaceConfig();

    public PlaceConfig getWeatherInPlace() {
        return weatherInPlace;
    }

    public void readUrlData(PlaceConfig place, PlaceConfig weatherConfig) throws IOException, ParseException {
        ParseManager parseManager = new ParseManager();
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + place.getLatitude() + "&lon=" + place.getLongitude() + "&appid=" + parseManager.getPropertyValue("OPENWEATHERMAP_API_KEY"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            JsonParse jsonParse = new JsonParse();
            weatherInPlace = jsonParse.weatherParse(url, weatherConfig);
        }
    }
}
