package com.viacosta.util;

import javax.swing.JOptionPane;

/**
 * Utilidad para mostrar cuadros de diálogo estandarizados en Java Swing.
 */
public class AlertHelper {

    public static void showInfo(String titulo, String header, String mensaje) {
        String texto = construirMensaje(header, mensaje);
        JOptionPane.showMessageDialog(null, texto, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(String titulo, String header, String mensaje) {
        String texto = construirMensaje(header, mensaje);
        JOptionPane.showMessageDialog(null, texto, titulo, JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(String titulo, String header, String mensaje) {
        String texto = construirMensaje(header, mensaje);
        JOptionPane.showMessageDialog(null, texto, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirmation(String titulo, String header, String mensaje) {
        String texto = construirMensaje(header, mensaje);
        int opcion = JOptionPane.showConfirmDialog(
                null,
                texto,
                titulo,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return opcion == JOptionPane.YES_OPTION;
    }

    private static String construirMensaje(String header, String mensaje) {
        if (header == null || header.trim().isEmpty()) {
            return mensaje;
        }
        return header + "\n\n" + mensaje;
    }
}
