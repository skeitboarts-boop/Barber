package com.example.barbershop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.barbershop.data.local.entity.UserEntity;

@Dao
public interface UserDao {

    @Insert
    long insert(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getUserById(long id);

    @Query("UPDATE users SET email = :email, phone = :phone, cityId = :cityId WHERE id = :userId")
    void updateProfile(long userId, String email, String phone, long cityId);

    @Query("UPDATE users SET passwordHash = :passwordHash, salt = :salt WHERE id = :userId")
    void updatePassword(long userId, String passwordHash, String salt);
}