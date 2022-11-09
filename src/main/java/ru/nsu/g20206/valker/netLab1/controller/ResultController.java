package ru.nsu.g20206.valker.netLab1.controller;

import javafx.stage.Stage;

import ru.nsu.g20206.valker.netLab1.model.PlaceConfig;
import ru.nsu.g20206.valker.netLab1.view.ResultUI;

import java.util.Vector;

import static ru.nsu.g20206.valker.netLab1.model.ConstClass.*;

public class ResultController {

    public void drawResult(Stage stage, PlaceConfig weatherInPlace, Vector<PlaceConfig> localPlacesInfo) {
        String str = checkWeather(weatherInPlace.getWeather());
        ResultUI resultUI = new ResultUI();
        resultUI.start(stage);
        resultUI.drawWeather(weatherInPlace.getTemp(), str, weatherInPlace.getWeather());
        resultUI.drawPlaces(localPlacesInfo);
    }

    public String checkWeather(String string) {
        if (string.contains("rain"))
            return PATH_TO_RAIN_WEATHER;
        if (string.contains("cloud"))
            return PATH_TO_CLOUD_WEATHER;
        if (string.contains("clear"))
            return PATH_TO_CLEAR_WEATHER;
        if (string.contains("snow"))
            return PATH_TO_SNOW_WEATHER;
        return "";
    }
}