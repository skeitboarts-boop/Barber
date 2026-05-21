package com.example.barbershop.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "barbers")
public class BarberEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long branchId;
    @NonNull private String name = "";
    @NonNull private String specialization = "";
    private int experienceYears;
    private double rating;
    @NonNull private String phone = "";
    public BarberEntity() {
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getBranchId() {
        return branchId;
    }
    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }
    @NonNull public String getName() {
        return name;
    }
    public void setName(@NonNull String name) {
        this.name = name;
    }
    @NonNull public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(@NonNull String specialization) {
        this.specialization = specialization;
    }
    public int getExperienceYears() {
        return experienceYears;
    }
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    @NonNull public String getPhone() {
        return phone;
    }
    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }
    @Override public String toString() {
        return name;
    }
}