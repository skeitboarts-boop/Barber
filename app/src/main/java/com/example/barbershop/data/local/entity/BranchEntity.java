package com.example.barbershop.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "branches")
public class BranchEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long cityId;
    @NonNull private String title = "";
    @NonNull private String address = "";
    public BranchEntity() {
    }
    public BranchEntity(long cityId, @NonNull String title, @NonNull String address) {
        this.cityId = cityId;
        this.title = title;
        this.address = address;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getCityId() {
        return cityId;
    }
    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
    @NonNull public String getTitle() {
        return title;
    }
    public void setTitle(@NonNull String title) {
        this.title = title;
    }
    @NonNull public String getAddress() {
        return address;
    }
    public void setAddress(@NonNull String address) {
        this.address = address;
    }
    @Override public String toString() {
        return title + " — " + address;
    }
}