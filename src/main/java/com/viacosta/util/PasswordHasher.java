package com.viacosta.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    /**
     * Genera el resumen criptográfico SHA-256 en formato hexadecimal.
     * @param password Contraseña en texto plano
     * @return Hash SHA-256 de 64 caracteres
     */
    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo de hash no disponible: " + e.getMessage(), e);
        }
    }

    /**
     * Compara una contraseña en texto plano contra su hash almacenado.
     */
    public static boolean verify(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        return hash(rawPassword).equalsIgnoreCase(hashedPassword);
    }
}
