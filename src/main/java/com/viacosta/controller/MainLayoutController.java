package com.viacosta.controller;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.model.Empleado;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainLayoutController {

    @FXML private Label lblTituloSeccion;
    @FXML private Label lblSubtituloSeccion;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;
    @FXML private Label lblSedeUsuario;
    @FXML private Label lblReloj;
    @FXML private Label lblDbStatus;

    @FXML private Button btnNavVentas;
    @FXML private Button btnNavClientes;
    @FXML private Button btnNavViajes;
    @FXML private Button btnNavHistorial;

    @FXML private StackPane centerPane;

    private final Map<String, Node> viewCache = new HashMap<>();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        configurarDatosUsuario();
        iniciarReloj();
        configurarEstadoBD();

        // Cargar vista inicial por defecto: Venta de Pasajes
        mostrarVentas();
    }

    private void configurarDatosUsuario() {
        Empleado usuario = SessionManager.getUsuarioActual();
        if (usuario != null) {
            lblNombreUsuario.setText(usuario.getNombreCompleto());
            lblRolUsuario.setText(usuario.getNombreRol() != null ? usuario.getNombreRol().toUpperCase() : "OPERADOR");
            lblSedeUsuario.setText(usuario.getNombreSede() != null ? usuario.getNombreSede() : "Agencia Chimbote");
        } else {
            lblNombreUsuario.setText("Colaborador VIACOSTA");
            lblRolUsuario.setText("TAQUILLERO");
            lblSedeUsuario.setText("Sede Principal");
        }
    }

    private void configurarEstadoBD() {
        boolean esH2 = DatabaseConnection.isH2Fallback();
        if (esH2) {
            lblDbStatus.setText("🔵 Motor: H2 Fallback");
            lblDbStatus.setStyle("-fx-background-color: #075985; -fx-text-fill: #7dd3fc; -fx-background-radius: 4px; -fx-padding: 3px 8px; -fx-font-size: 11px;");
        } else {
            lblDbStatus.setText("🟢 Motor: MySQL 8.0 Activo");
            lblDbStatus.setStyle("-fx-background-color: #064e3b; -fx-text-fill: #a7f3d0; -fx-background-radius: 4px; -fx-padding: 3px 8px; -fx-font-size: 11px;");
        }
    }

    private void iniciarReloj() {
        Thread clockThread = new Thread(() -> {
            while (true) {
                try {
                    String time = LocalDateTime.now().format(timeFormatter);
                    Platform.runLater(() -> {
                        if (lblReloj != null) {
                            lblReloj.setText(time);
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        clockThread.setDaemon(true);
        clockThread.start();
    }

    @FXML
    public void mostrarVentas() {
        resaltarBoton(btnNavVentas);
        lblTituloSeccion.setText("Venta de Pasajes en Taquilla");
        lblSubtituloSeccion.setText("Selección de salida, asignación interactiva de asientos y emisión de comprobantes");
        cargarVista("/fxml/VentasView.fxml");
    }

    @FXML
    public void mostrarClientes() {
        resaltarBoton(btnNavClientes);
        lblTituloSeccion.setText("Gestión de Pasajeros y Clientes");
        lblSubtituloSeccion.setText("Registro, actualización, consulta por DNI y sincronización con API RENIEC");
        cargarVista("/fxml/ClientesView.fxml");
    }

    @FXML
    public void mostrarViajes() {
        resaltarBoton(btnNavViajes);
        lblTituloSeccion.setText("Programación Operativa de Salidas");
        lblSubtituloSeccion.setText("Consulta de itinerarios, rutas activas, flota de buses y niveles de ocupación");
        cargarVista("/fxml/ViajesView.fxml");
    }

    @FXML
    public void mostrarHistorial() {
        resaltarBoton(btnNavHistorial);
        lblTituloSeccion.setText("Registro de Compras y Boletas Emitidas");
        lblSubtituloSeccion.setText("Auditoría de operaciones comerciales, transacciones persistidas y detalles de viaje");
        cargarVista("/fxml/HistorialVentasView.fxml");
    }

    @FXML
    public void onCerrarSesion() {
        boolean confirmar = AlertHelper.showConfirmation("Cerrar Sesión", "¿Está seguro de cerrar sesión?",
                "Se cerrará la sesión actual del colaborador y volverá a la pantalla de acceso.");
        if (confirmar) {
            SessionManager.cerrarSesion();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) centerPane.getScene().getWindow();
                Scene scene = new Scene(root, 850, 650);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setTitle("VIACOSTA - Inicio de Sesión");
                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resaltarBoton(Button activo) {
        Button[] botones = {btnNavVentas, btnNavClientes, btnNavViajes, btnNavHistorial};
        for (Button b : botones) {
            if (b != null) {
                b.getStyleClass().remove("nav-button-active");
                if (b == activo) {
                    b.getStyleClass().add("nav-button-active");
                }
            }
        }
    }

    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();
            centerPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Error al cargar pantalla", "No se pudo cargar la vista: " + fxmlPath, e.getMessage());
        }
    }
}
