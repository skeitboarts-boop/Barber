package com.example.barbershop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.barbershop.data.local.entity.BarberEntity;

import java.util.List;

@Dao
public interface BarberDao {

    @Query("SELECT * FROM barbers WHERE branchId = :branchId ORDER BY rating DESC, experienceYears DESC, name ASC")
    List<BarberEntity> getBarbersByBranchId(long branchId);

    @Query("SELECT * FROM barbers WHERE id = :id LIMIT 1")
    BarberEntity getBarberById(long id);
}