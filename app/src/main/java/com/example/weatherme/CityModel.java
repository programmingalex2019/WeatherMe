package com.example.weatherme;

public class CityModel {

    private String cityName;
    private String cityState;
    private String cityCountry;

    public CityModel(String cityName, String cityState, String cityCountry) {
        this.cityName = cityName;
        this.cityState = cityState;
        this.cityCountry = cityCountry;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityState() {
        return cityState;
    }

    public String getCityCountry() {
        return cityCountry;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityState(String cityState) {
        this.cityState = cityState;
    }

    public void setCityCountry(String cityCountry) {
        this.cityCountry = cityCountry;
    }
}
