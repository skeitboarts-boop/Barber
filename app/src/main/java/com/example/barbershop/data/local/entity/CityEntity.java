package com.example.barbershop.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cities")
public class CityEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name = "";

    public CityEntity() {
    }

    public CityEntity(@NonNull String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}