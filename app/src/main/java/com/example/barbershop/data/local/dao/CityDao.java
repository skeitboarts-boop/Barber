package com.example.barbershop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.barbershop.data.local.entity.CityEntity;

import java.util.List;

@Dao
public interface CityDao {

    @Insert
    long insert(CityEntity city);

    @Query("SELECT * FROM cities ORDER BY name ASC")
    List<CityEntity> getAllCities();

    @Query("SELECT * FROM cities WHERE id = :id LIMIT 1")
    CityEntity getCityById(long id);

    @Query("SELECT COUNT(*) FROM cities")
    int getCitiesCount();
}