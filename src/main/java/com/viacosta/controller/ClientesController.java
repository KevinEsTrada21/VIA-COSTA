package com.viacosta.controller;

import com.viacosta.model.Cliente;
import com.viacosta.service.ClienteService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.Validators;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class ClientesController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colDireccion;

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnConsultarReniec;
    @FXML private Label lblTotalClientes;

    private final ClienteService clienteService;
    private final ObservableList<Cliente> clientesList = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    public ClientesController() {
        this.clienteService = new ClienteService();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarClientes("");

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> cargarClientes(newVal));
    }

    private void configurarTabla() {
        colDni.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDni()));
        colNombres.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colApellidos.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getApellido()));
        colTelefono.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelefono() != null ? cell.getValue().getTelefono() : "-"));
        colCorreo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCorreo() != null ? cell.getValue().getCorreo() : "-"));
        colDireccion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDireccion() != null ? cell.getValue().getDireccion() : "-"));

        tblClientes.setItems(clientesList);

        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            clienteSeleccionado = newVal;
            if (newVal != null) {
                mostrarEnFormulario(newVal);
                btnEliminar.setDisable(false);
            } else {
                btnEliminar.setDisable(true);
            }
        });
    }

    private void cargarClientes(String filtro) {
        new Thread(() -> {
            List<Cliente> lista = clienteService.listarClientes(filtro);
            Platform.runLater(() -> {
                clientesList.setAll(lista);
                lblTotalClientes.setText(String.valueOf(lista.size()));
            });
        }).start();
    }

    private void mostrarEnFormulario(Cliente c) {
        txtDni.setText(c.getDni());
        txtNombre.setText(c.getNombre());
        txtApellido.setText(c.getApellido());
        txtTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
        txtCorreo.setText(c.getCorreo() != null ? c.getCorreo() : "");
        txtDireccion.setText(c.getDireccion() != null ? c.getDireccion() : "");
    }

    @FXML
    public void onConsultarReniec() {
        String dni = txtDni.getText() != null ? txtDni.getText().trim() : "";
        if (!Validators.esDniValido(dni)) {
            AlertHelper.showWarning("DNI Inválido", "Formato de DNI no admitido",
                    "El DNI debe contener exactamente 8 dígitos numéricos peruanos.");
            txtDni.requestFocus();
            return;
        }

        btnConsultarReniec.setDisable(true);
        btnConsultarReniec.setText("Consultando...");

        new Thread(() -> {
            Optional<Cliente> opt = clienteService.buscarPorDni(dni);
            if (opt.isEmpty()) {
                opt = clienteService.consultarEnReniec(dni);
            }

            final Optional<Cliente> finalOpt = opt;
            Platform.runLater(() -> {
                btnConsultarReniec.setDisable(false);
                btnConsultarReniec.setText("🔍 Consultar RENIEC");
                if (finalOpt.isPresent()) {
                    Cliente c = finalOpt.get();
                    txtNombre.setText(c.getNombre());
                    txtApellido.setText(c.getApellido());
                    if (c.getTelefono() != null) txtTelefono.setText(c.getTelefono());
                    if (c.getCorreo() != null) txtCorreo.setText(c.getCorreo());
                    if (c.getDireccion() != null) txtDireccion.setText(c.getDireccion());
                } else {
                    AlertHelper.showInfo("Consulta RENIEC", "No hallado", "Complete los nombres y apellidos manualmente.");
                }
            });
        }).start();
    }

    @FXML
    public void onGuardar() {
        String dni = txtDni.getText() != null ? txtDni.getText().trim() : "";
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String apellido = txtApellido.getText() != null ? txtApellido.getText().trim() : "";

        if (!Validators.esDniValido(dni)) {
            AlertHelper.showWarning("DNI Inválido", "Validación Fallida", "El DNI debe tener exactamente 8 dígitos.");
            txtDni.requestFocus();
            return;
        }
        if (nombre.isEmpty() || apellido.isEmpty()) {
            AlertHelper.showWarning("Campos Requeridos", "Nombres Obligatorios", "Ingrese nombres y apellidos del cliente.");
            txtNombre.requestFocus();
            return;
        }

        Cliente cliente = new Cliente(
                clienteSeleccionado != null ? clienteSeleccionado.getIdCliente() : 0,
                dni,
                nombre,
                apellido,
                txtTelefono.getText(),
                txtDireccion.getText(),
                txtCorreo.getText()
        );

        btnGuardar.setDisable(true);
        new Thread(() -> {
            try {
                clienteService.guardarCliente(cliente);
                Platform.runLater(() -> {
                    btnGuardar.setDisable(false);
                    AlertHelper.showInfo("Cliente Guardado", "Operación Exitosa", "Los datos del cliente se registraron correctamente.");
                    onLimpiar();
                    cargarClientes(txtBuscar.getText());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnGuardar.setDisable(false);
                    AlertHelper.showError("Error al Guardar", "No se pudo registrar cliente", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void onEliminar() {
        if (clienteSeleccionado == null) return;

        boolean confirmar = AlertHelper.showConfirmation("Eliminar Cliente",
                "¿Está seguro de eliminar al cliente " + clienteSeleccionado.getNombreCompleto() + "?",
                "Esta acción no se puede deshacer.");
        if (confirmar) {
            new Thread(() -> {
                boolean ok = clienteService.eliminarCliente(clienteSeleccionado.getIdCliente());
                Platform.runLater(() -> {
                    if (ok) {
                        AlertHelper.showInfo("Eliminado", "Cliente Eliminado", "El cliente fue retirado de la base de datos.");
                        onLimpiar();
                        cargarClientes(txtBuscar.getText());
                    } else {
                        AlertHelper.showError("Error", "No se pudo eliminar",
                                "El cliente no se puede eliminar porque tiene compras o boletas asociadas (Integridad Referencial).");
                    }
                });
            }).start();
        }
    }

    @FXML
    public void onLimpiar() {
        clienteSeleccionado = null;
        tblClientes.getSelectionModel().clearSelection();
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();
        btnEliminar.setDisable(true);
        txtDni.requestFocus();
    }
}
