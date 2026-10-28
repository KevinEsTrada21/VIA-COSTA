package com.viacosta.controller;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.model.Empleado;
import com.viacosta.service.AuthService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;
    @FXML private Label lblEstadoConexion;
    @FXML private Button btnIngresar;

    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    public void initialize() {
        verificarConexionBD();
    }

    private void verificarConexionBD() {
        new Thread(() -> {
            try {
                DatabaseConnection.getConnection();
                boolean esH2 = DatabaseConnection.isH2Fallback();
                Platform.runLater(() -> {
                    if (lblEstadoConexion != null) {
                        if (esH2) {
                            lblEstadoConexion.setText("🔵 Motor: H2 Fallback (Memoria)");
                            lblEstadoConexion.setStyle("-fx-text-fill: #38bdf8;");
                        } else {
                            lblEstadoConexion.setText("🟢 Motor: MySQL 8.0 (viacosta)");
                            lblEstadoConexion.setStyle("-fx-text-fill: #10b981;");
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (lblEstadoConexion != null) {
                        lblEstadoConexion.setText("🔴 Error de conexión BD");
                        lblEstadoConexion.setStyle("-fx-text-fill: #ef4444;");
                    }
                });
            }
        }).start();
    }

    @FXML
    public void onLogin(ActionEvent event) {
        String usuario = txtUsuario.getText() != null ? txtUsuario.getText().trim() : "";
        String pass = txtPassword.getText() != null ? txtPassword.getText() : "";

        if (usuario.isEmpty() || pass.isEmpty()) {
            mostrarError("Por favor ingrese usuario y contraseña.");
            return;
        }

        btnIngresar.setDisable(true);
        lblMensaje.setText("Verificando credenciales...");
        lblMensaje.setStyle("-fx-text-fill: #38bdf8;");

        new Thread(() -> {
            Optional<Empleado> empOpt = authService.autenticar(usuario, pass);
            Platform.runLater(() -> {
                btnIngresar.setDisable(false);
                if (empOpt.isPresent()) {
                    abrirVentanaPrincipal();
                } else {
                    mostrarError("Credenciales incorrectas o usuario no activo.");
                    txtPassword.clear();
                    txtPassword.requestFocus();
                }
            });
        }).start();
    }

    @FXML
    public void onUsarAdmin() {
        txtUsuario.setText("admin");
        txtPassword.setText("admin123");
    }

    @FXML
    public void onUsarVendedor() {
        txtUsuario.setText("vendedor");
        txtPassword.setText("vendedor123");
    }

    private void mostrarError(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.setStyle("-fx-text-fill: #ef4444;");
    }

    private void abrirVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnIngresar.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 750);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            stage.setTitle("VIACOSTA - Sistema de Venta de Pasajes y Transporte Interprovincial");
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Error de Navegación", "No se pudo abrir la ventana principal", e.getMessage());
        }
    }
}
