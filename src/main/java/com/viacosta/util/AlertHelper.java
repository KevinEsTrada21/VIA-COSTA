package com.viacosta.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

public class AlertHelper {

    public static void showInfo(String titulo, String header, String mensaje) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, header, mensaje);
    }

    public static void showWarning(String titulo, String header, String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, titulo, header, mensaje);
    }

    public static void showError(String titulo, String header, String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, header, mensaje);
    }

    public static boolean showConfirmation(String titulo, String header, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        estilarDialogo(alert.getDialogPane());

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        estilarDialogo(alert.getDialogPane());
        alert.showAndWait();
    }

    private static void estilarDialogo(DialogPane pane) {
        try {
            String css = AlertHelper.class.getResource("/css/styles.css").toExternalForm();
            if (css != null) {
                pane.getStylesheets().add(css);
                pane.getStyleClass().add("custom-dialog");
            }
        } catch (Exception ignored) {
        }
    }
}
