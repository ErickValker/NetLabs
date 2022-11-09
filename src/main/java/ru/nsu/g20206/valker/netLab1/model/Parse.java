package ru.nsu.g20206.valker.netLab1.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.nsu.g20206.valker.netLab1.controller.ResultController;
import ru.nsu.g20206.valker.netLab1.controller.ParseMenuController;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Parse {
    private Vector<PlaceConfig> places = new Vector<>();
    private Vector<PlaceConfig> localPlaces = new Vector<>();
    private ParseMenuController parseMenuController;
    private PlaceConfig weatherInPlace;
    private Vector<PlaceConfig> placesInfo = new Vector<>();
    private ResultController resultController = new ResultController();
    private int flag = 0;

    public PlaceConfig getWeatherInPlace() {
        return weatherInPlace;
    }

    public Vector<PlaceConfig> getPlacesInfo() {
        return placesInfo;
    }

    public void setParseMenuController(ParseMenuController parseMenuController) {
        this.parseMenuController = parseMenuController;
    }

    public int getFlag() {
        return flag;
    }

    public Vector<PlaceConfig> getPlaces() {
        return places;
    }

    private String getPropertyValue(String propertyName) throws IOException {
        String propertyValue = "";
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("./src/main/resources/App.properties")) {
            properties.load(fileInputStream);
            propertyValue = properties.getProperty(propertyName);
        } catch (IIOException e) {
            System.out.println(e);
        }
        return propertyValue;
    }

    private StringBuilder readUrlStream(URL url) throws IOException {
        StringBuilder inLine = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            inLine.append(scanner.nextLine());
        }
        scanner.close();
        return inLine;
    }

    public void findGlobalPlace(String text) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> globalPlaces = CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL("https://graphhopper.com/api/1/geocode?q=" + text.replaceAll(" ", "%20") + "&key=" + getPropertyValue("GRAPHHOPPER_API_KEY"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                } else {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        globalPlaces.get();
        globalPlaces.thenRun(() -> {
            try {
                parseMenuController.drawCases();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void findPlacesAndWeather(String string) throws Exception {
        PlaceConfig ourPlace = new PlaceConfig();
        for (int i = 0; i < places.size(); i++) {
            if (string.contains(places.get(i).getCountry())
                    && string.contains(places.get(i).getPlaceName())
                    && string.contains(places.get(i).getRegion())
                    && string.contains(places.get(i).getState())
                    && string.contains(places.get(i).getCityName())) {
                ourPlace = places.get(i);
                break;
            }
        }

        PlaceConfig finalOurPlace = ourPlace;
        CompletableFuture<Void> globalPlaces = CompletableFuture.runAsync(() -> {
            try {
                findPlaces(finalOurPlace);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRun(() -> {
            try {
                findPlaceInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<Void> weatherPlaces = CompletableFuture.runAsync(() -> {
            try {
                findWeather(finalOurPlace);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        globalPlaces.get();
        weatherPlaces.get();
        parseMenuController.drawResult(resultController);
    }


    public void findPlaces(PlaceConfig place) throws Exception {
        URL url = new URL("https://api.opentripmap.com/0.1/" + place.getLang() + "/places/radius?radius=2000&lon=" + place.getLongitude() + "&lat=" + place.getLatitude() + "&apikey=" + getPropertyValue("OPENTRIMMAP_API_KEY"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
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
        }
    }

    public void findWeather(PlaceConfig place) throws Exception {
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + place.getLatitude() + "&lon=" + place.getLongitude() + "&appid=" + getPropertyValue("OPENWEATHERMAP_API_KEY"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {

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
        }
    }

    public void findPlaceInfo() throws ExecutionException, InterruptedException {

        for (int i = 0; i < localPlaces.size(); i++) {
            PlaceConfig placeConfig = localPlaces.get(i);
            findingInfo(placeConfig);
        }
        flag = 1;
    }

    private void findingInfo(PlaceConfig placeConfig) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> findInfo = CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL("https://api.opentripmap.com/0.1/" + placeConfig.getLang() + "/places/xid/" + placeConfig.getxID() + "?apikey=" + getPropertyValue("OPENTRIMMAP_API_KEY"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                } else {
                    StringBuilder inLine = readUrlStream(url);

                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(inLine.toString());

                    ObjectMapper mapper = new ObjectMapper();
                    String JSONString;
                    PlaceConfig placesInfo;
                    JSONString = "{\"kinds\":\"" + jsonObject.get("kinds") + "\"}";
                    placesInfo = mapper.readValue(JSONString, PlaceConfig.class);
                    placesInfo.setLocalName(placeConfig.getLocalName());
                    placesInfo.setState(placeConfig.getState());
                    placesInfo.setCityName(placeConfig.getCityName());
                    placesInfo.setRegion(placeConfig.getRegion());
                    placesInfo.setState(placeConfig.getState());
                    this.placesInfo.add(placesInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findInfo.get();
    }

}
