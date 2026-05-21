package com.example.barbershop.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class PasswordUtils {

    private PasswordUtils() {
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hashPassword(String plainPassword, String saltBase64) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.decode(saltBase64, Base64.NO_WRAP));
            byte[] hashed = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hashed, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String saltBase64, String expectedHash) {
        String actualHash = hashPassword(plainPassword, saltBase64);
        return actualHash.equals(expectedHash);
    }
}