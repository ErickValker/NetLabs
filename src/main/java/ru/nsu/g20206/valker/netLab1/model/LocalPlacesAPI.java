package ru.nsu.g20206.valker.netLab1.model;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LocalPlacesAPI {

    private ArrayList<PlaceConfig> localPlaces = new ArrayList<>();
    private ParseManager parseManager = new ParseManager();
    private ArrayList<PlaceConfig> placesInfo = new ArrayList<>();
    private PlaceConfig info = new PlaceConfig();

    public ArrayList<PlaceConfig> getLocalPlaces() {
        return localPlaces;
    }

    public PlaceConfig getInfo() {
        return info;
    }

    public void readPlacesData(PlaceConfig place) throws IOException, ParseException {

        URL url = new URL("https://api.opentripmap.com/0.1/" + place.getLang() + "/places/radius?radius=2000&lon=" + place.getLongitude() + "&lat=" + place.getLatitude() + "&apikey=" + parseManager.getPropertyValue("OPENTRIMMAP_API_KEY"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            JsonParse jsonParse = new JsonParse();
            localPlaces = jsonParse.localPlacesParse(url, place, localPlaces);
        }
    }

    public void readPlacesInfoData(PlaceConfig placeConfig) throws IOException, ParseException {
        URL url = new URL("https://api.opentripmap.com/0.1/" + placeConfig.getLang() + "/places/xid/" + placeConfig.getxID() + "?apikey=" + parseManager.getPropertyValue("OPENTRIMMAP_API_KEY"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            JsonParse jsonParse = new JsonParse();
            info = jsonParse.localPlacesInfoParse(url, placeConfig, placesInfo);
        }
    }

}
