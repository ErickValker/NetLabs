package ru.nsu.g20206.valker.netLab1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class JsonParse {

    private ArrayList<PlaceConfig> places = new ArrayList<>();

    public StringBuilder readUrlStream(URL url) throws IOException {
        StringBuilder inLine = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            inLine.append(scanner.nextLine());
        }
        scanner.close();
        return inLine;
    }

    public ArrayList<PlaceConfig> globalPlaceParse(String text, URL url) throws IOException, ParseException {
        StringBuilder inLine = readUrlStream(url);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(inLine.toString());
        JSONArray placesArr = (JSONArray) jsonObject.get("hits");
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < placesArr.size(); i++) {
            JSONObject placeObject = (JSONObject) placesArr.get(i);
            if (placeObject.get("name").equals(text)) {
                JSONObject pointObject = (JSONObject) placeObject.get("point");
                String JSONString = "{\"longitude\":" + pointObject.get("lng") +
                        ",\"latitude\":" + pointObject.get("lat") +
                        ",\"state\":\"" + placeObject.get("osm_value") + "\"" +
                        ",\"country\":\"" + placeObject.get("country") + "\"" +
                        ",\"region\":\"" + placeObject.get("state") + "\"" +
                        ",\"lang\":\"" + jsonObject.get("locale") + "\"" +
                        ",\"placeName\":\"" + placeObject.get("name") + "\"" +
                        ",\"cityName\":\"" + placeObject.get("city") + "\"" + "}";

                PlaceConfig placeConfig = mapper.readValue(JSONString, PlaceConfig.class);
                places.add(placeConfig);
            }
        }
        return places;
    }

    public ArrayList<PlaceConfig> localPlacesParse(URL url, PlaceConfig place, ArrayList<PlaceConfig> localPlaces) throws IOException, ParseException {
        StringBuilder inLine = readUrlStream(url);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(inLine.toString());
        JSONArray placesArr = (JSONArray) jsonObject.get("features");
        ObjectMapper mapper = new ObjectMapper();
        String JSONString;
        PlaceConfig localPlace;
        for (int i = 0; i < placesArr.size(); i++) {
            JSONObject placeObject = (JSONObject) placesArr.get(i);
            JSONObject propertiesObject = (JSONObject) placeObject.get("properties");
            JSONString = "{\"xID\":\"" + propertiesObject.get("xid") + "\"" + ",\"localName\":\"" + propertiesObject.get("name").toString().replaceAll("\"", "\\\\\"") + "\"}";
            localPlace = mapper.readValue(JSONString, PlaceConfig.class);
            localPlace.setCityName(place.getPlaceName());
            localPlace.setLang(place.getLang());
            localPlace.setPlaceName(place.getPlaceName());
            localPlace.setRegion(place.getRegion());
            localPlace.setState(place.getState());
            localPlaces.add(localPlace);
        }
        return localPlaces;
    }

    public PlaceConfig localPlacesInfoParse(URL url, PlaceConfig placeConfig, ArrayList<PlaceConfig> placesInfo) throws IOException, ParseException {

        StringBuilder inLine = readUrlStream(url);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(inLine.toString());
        ObjectMapper mapper = new ObjectMapper();
        String JSONString;
        PlaceConfig info;
        JSONString = "{\"kinds\":\"" + jsonObject.get("kinds") + "\"}";
        info = mapper.readValue(JSONString, PlaceConfig.class);
        info.setLocalName(placeConfig.getLocalName());
        info.setState(placeConfig.getState());
        info.setCityName(placeConfig.getCityName());
        info.setRegion(placeConfig.getRegion());
        info.setState(placeConfig.getState());
        placesInfo.add(info);
        return info;
    }

    public PlaceConfig weatherParse(URL url, PlaceConfig weatherInPlace) throws IOException, ParseException {
        StringBuilder inLine = readUrlStream(url);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(inLine.toString());
        JSONArray weatherArr = (JSONArray) jsonObject.get("weather");
        JSONObject weatherDesc = (JSONObject) weatherArr.get(0);
        JSONObject temp = (JSONObject) jsonObject.get("main");
        ObjectMapper mapper = new ObjectMapper();
        String JSONString = "{\"weather\":\"" + weatherDesc.get("description") + "\"" + ",\"temp\":" + temp.get("temp") + "}";
        weatherInPlace = mapper.readValue(JSONString, PlaceConfig.class);
        weatherInPlace.setTemp(Math.round((weatherInPlace.getTemp() - 273.0) * 10) / 10.0);

        return weatherInPlace;

    }

}
