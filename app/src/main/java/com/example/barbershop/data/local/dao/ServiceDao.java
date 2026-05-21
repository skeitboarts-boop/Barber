package com.example.barbershop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.barbershop.data.local.entity.ServiceEntity;

import java.util.List;

@Dao
public interface ServiceDao {

    @Query("SELECT * FROM services ORDER BY price ASC")
    List<ServiceEntity> getAllServices();

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    ServiceEntity getServiceById(long id);
}