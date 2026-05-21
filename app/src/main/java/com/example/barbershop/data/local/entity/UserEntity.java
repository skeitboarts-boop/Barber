package com.example.barbershop.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull private String fullName = "";
    @NonNull private String email = "";
    @NonNull private String phone = "";
    @NonNull private String passwordHash = "";
    @NonNull private String salt = "";
    private long cityId;
    private long createdAt;
    public UserEntity() {
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @NonNull public String getFullName() {
        return fullName;
    }
    public void setFullName(@NonNull String fullName) {
        this.fullName = fullName;
    }
    @NonNull public String getEmail() {
        return email;
    }
    public void setEmail(@NonNull String email) {
        this.email = email;
    }
    @NonNull public String getPhone() {
        return phone;
    }
    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }
    @NonNull public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(@NonNull String passwordHash) {
        this.passwordHash = passwordHash;
    }




    @NonNull public String getSalt() {
        return salt;
    }
    public void setSalt(@NonNull String salt) {
        this.salt = salt;
    }
    public long getCityId() {
        return cityId;
    }
    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}