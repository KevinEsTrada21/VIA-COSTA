package com.viacosta.util;

public class Validators {

    /**
     * Valida que el DNI peruano tenga exactamente 8 dígitos numéricos.
     */
    public static boolean esDniValido(String dni) {
        if (dni == null) return false;
        return dni.trim().matches("^[0-9]{8}$");
    }

    /**
     * Valida que el RUC peruano tenga exactamente 11 dígitos numéricos.
     */
    public static boolean esRucValido(String ruc) {
        if (ruc == null) return false;
        return ruc.trim().matches("^(10|20)[0-9]{9}$");
    }

    /**
     * Valida que un teléfono peruano tenga 9 dígitos (o fijo de 7-9).
     */
    public static boolean esTelefonoValido(String tel) {
        if (tel == null || tel.trim().isEmpty()) return true; // Opcional
        return tel.trim().matches("^[0-9]{7,15}$");
    }

    /**
     * Valida formato de correo electrónico.
     */
    public static boolean esCorreoValido(String correo) {
        if (correo == null || correo.trim().isEmpty()) return true; // Opcional
        return correo.trim().matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Valida que el texto no sea nulo ni esté vacío.
     */
    public static boolean noVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
}
