package ru.nsu.g20206.valker.netLab1.model;

import org.json.simple.parser.ParseException;
import ru.nsu.g20206.valker.netLab1.controller.ResultController;
import ru.nsu.g20206.valker.netLab1.controller.ParseMenuController;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ParseManager {
    private ArrayList<PlaceConfig> places = new ArrayList<>();
    private ArrayList<PlaceConfig> localPlaces = new ArrayList<>();
    private ParseMenuController parseMenuController;
    private PlaceConfig weatherInPlace;
    private ArrayList<PlaceConfig> placesInfo = new ArrayList<>();
    private ResultController resultController = new ResultController();
    private int flag = 0;

    public PlaceConfig getWeatherInPlace() {
        return weatherInPlace;
    }

    public ArrayList<PlaceConfig> getPlacesInfo() {
        return placesInfo;
    }

    public void setParseMenuController(ParseMenuController parseMenuController) {
        this.parseMenuController = parseMenuController;
    }

    public int getFlag() {
        return flag;
    }

    public ArrayList<PlaceConfig> getPlaces() {
        return places;
    }

    public String getPropertyValue(String propertyName) throws IOException {
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

    public void findGlobalPlace(String text) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> globalPlaces = CompletableFuture.runAsync(() -> {

        GlobalPlaceAPI globalPlaceAPI = new GlobalPlaceAPI();
            try {
                globalPlaceAPI.parseUrlData(text);
                places = globalPlaceAPI.getPlaces();
            } catch (IOException | ParseException e ) {
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
        LocalPlacesAPI localPlacesAPI = new LocalPlacesAPI();
        localPlacesAPI.readPlacesData(place);
        localPlaces = localPlacesAPI.getLocalPlaces();

    }

    public void findWeather(PlaceConfig place) throws Exception {
        WeatherAPI weatherAPI = new WeatherAPI();
        weatherAPI.readUrlData(place, weatherInPlace);
        weatherInPlace = weatherAPI.getWeatherInPlace();

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

            LocalPlacesAPI localPlacesAPI = new LocalPlacesAPI();
            try {
                localPlacesAPI.readPlacesInfoData(placeConfig);
                placesInfo.add(localPlacesAPI.getInfo());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
        findInfo.get();
    }

}
