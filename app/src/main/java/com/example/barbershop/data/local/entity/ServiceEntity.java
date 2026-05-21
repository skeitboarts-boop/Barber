package com.example.barbershop.data.local.entity;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "services")
public class ServiceEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String name = "";
    @NonNull
    private String description = "";
    private int price;
    private int durationMinutes;
    public ServiceEntity() {
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
    @NonNull
    public String getDescription() {
        return description;
    }
    public void setDescription(@NonNull String description) {
        this.description = description;
    }public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    @Override
    public String toString() {
        return name + " • " + price + " ₽";
    }
}