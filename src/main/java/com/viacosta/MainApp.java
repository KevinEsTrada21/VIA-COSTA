package com.viacosta;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada principal para el Sistema de Escritorio "VIACOSTA".
 * Venta de Pasajes y Gestión de Transporte Interprovincial.
 * Curso Integrador I: Sistemas Software (Avance 3 - UTP).
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 850, 650);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            primaryStage.setTitle("VIACOSTA - Sistema de Venta de Pasajes y Transporte");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();
            primaryStage.show();

            System.out.println("[MainApp] Sistema VIACOSTA iniciado con éxito en JavaFX 17.");

        } catch (Exception e) {
            System.err.println("[MainApp] Error crítico al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
