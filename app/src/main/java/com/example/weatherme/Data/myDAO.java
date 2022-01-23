package com.example.weatherme.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weatherme.Models.CityModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface myDAO {

    @Insert
    void insert(CityModel... cities);

    @Query("SELECT * FROM cities")
    List<CityModel> getAllModules();

    @Update
    void update(final CityModel module);
    @Delete
    void delete(final CityModel module);


}
