package ru.nsu.g20206.valker.netLab1.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import ru.nsu.g20206.valker.netLab1.model.ParseManager;
import ru.nsu.g20206.valker.netLab1.model.PlaceConfig;
import ru.nsu.g20206.valker.netLab1.view.ParseMenuUI;

import java.util.Objects;
import java.util.ArrayList;

public class ParseMenuController {

    private static Stage stage;
    private ParseManager parserOfLocation = new ParseManager();
    private ArrayList<PlaceConfig> places;
    private ParseMenuUI parseMenuUI;
    private PlaceConfig weatherInPlace;
    private ArrayList<PlaceConfig> localPlacesInfo;

    public void setWeatherInPlace(PlaceConfig weatherInPlace) {
        this.weatherInPlace = weatherInPlace;
    }

    public void setLocalPlacesInfo(ArrayList<PlaceConfig> localPlacesInfo) {
        this.localPlacesInfo = localPlacesInfo;
    }

    private ResultController resultController;

    public void setResultController(ResultController resultController) {
        this.resultController = resultController;
    }

    public void setParseMenuUI(ParseMenuUI parseMenuUI) {
        this.parseMenuUI = parseMenuUI;
    }

    public ParseMenuController() {
        parserOfLocation.setParseMenuController(this);
    }

    public void clickSearchButton(Stage primaryStage, Button newAppButton, TextField text) {
        newAppButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                try {
                    stage = primaryStage;
                    if (!Objects.equals(text.getText(), "")) {
                        parserOfLocation.findGlobalPlace(text.getText());
                    } else
                        System.out.println("Enter the text");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public ListView<String> makeCase() {
        ObservableList<String> langs = FXCollections.observableArrayList();
        fillPlaces(langs);
        ListView<String> langsListView = new ListView<>(langs);
        return langsListView;
    }

    public void listenTheCase(ListView<String> langsListView) {
        MultipleSelectionModel<String> langsSelectionModel = langsListView.getSelectionModel();
        langsSelectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                try {
                    parserOfLocation.findPlacesAndWeather(newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void drawCases() {
        places = parserOfLocation.getPlaces();
        parseMenuUI.drawCases();
    }

    public ObservableList<String> fillPlaces(ObservableList<String> langs) {
        for (int i = 0; i < places.size(); i++) {
            langs.add(places.get(i).getCountry() + " " +
                    places.get(i).getPlaceName() + " " +
                    places.get(i).getCityName() + " " +
                    places.get(i).getRegion() + " " +
                    places.get(i).getState());
        }
        return langs;
    }

    public void drawResult(ResultController resultController) throws Exception {
        while (true) {
            if (parserOfLocation.getFlag() == 1) {
                setLocalPlacesInfo(parserOfLocation.getPlacesInfo());
                setWeatherInPlace(parserOfLocation.getWeatherInPlace());
                setResultController(resultController);
                resultController.drawResult(stage, weatherInPlace, localPlacesInfo);
                break;
            }
        }
    }

}