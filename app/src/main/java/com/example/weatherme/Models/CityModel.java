package com.example.weatherme.Models;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Define as an entity in database (sql)
@Entity(tableName = "cities") //table
public class CityModel {

    //properties
    @PrimaryKey @NonNull
    private String cityName;
    private String cityState;
    private String cityCountry;

    //constructor
    public CityModel(String cityName, String cityState, String cityCountry) {
        this.cityName = cityName;
        this.cityState = cityState;
        this.cityCountry = cityCountry;
    }

    //getters //immutable class
    public String getCityName() {
        return cityName;
    }

    public String getCityState() {
        return cityState;
    }

    public String getCityCountry() {
        return cityCountry;
    }

}
