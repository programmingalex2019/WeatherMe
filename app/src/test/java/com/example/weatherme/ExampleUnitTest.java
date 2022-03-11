package com.example.weatherme;

import com.example.weatherme.Models.CityModel;

import org.junit.Assert;
import org.junit.Test;

public class ExampleUnitTest {

    static String cityName = "CityName";
    static String cityState = "CityName";
    static String cityCountry = "CityName";

    @Test
    public void CityModel_Getter_ReturnsValidString() {

        //Arrange
        CityModel cityModel = new CityModel(cityName, cityState, cityCountry);

        //Act
        String cityNameGet = cityModel.getCityName();
        String cityStateGet = cityModel.getCityState();
        String cityCountryGet = cityModel.getCityCountry();

        //Assert
        Assert.assertTrue(cityNameGet.equals(cityName) && cityStateGet.equals(cityState) && cityCountryGet.equals(cityCountry));

    }

}