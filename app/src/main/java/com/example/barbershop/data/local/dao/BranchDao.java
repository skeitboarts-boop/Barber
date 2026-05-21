package com.example.barbershop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.barbershop.data.local.entity.BranchEntity;

import java.util.List;

@Dao
public interface BranchDao {

    @Query("SELECT * FROM branches WHERE cityId = :cityId ORDER BY title ASC")
    List<BranchEntity> getBranchesByCityId(long cityId);

    @Query("SELECT * FROM branches WHERE id = :id LIMIT 1")
    BranchEntity getBranchById(long id);
}