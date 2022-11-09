package ru.nsu.g20206.valker.netLab1.model;

public class PlaceConfig {
    private double longitude;
    private double latitude;
    private String lang;
    private String cityName;
    private String placeName;
    private String localName;
    private String state;
    private String region;
    private String xID;
    private double temp;
    private String weather;
    private String country;
    private String kinds;

    public String getKinds() {
        return kinds;
    }

    public String getCountry() {
        return country;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTemp() {
        return temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }

    public String getxID() {
        return xID;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}