package com.viacosta.controller;

import com.viacosta.model.*;
import com.viacosta.service.ClienteService;
import com.viacosta.service.VentaService;
import com.viacosta.service.ViajeService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.CurrencyFormatter;
import com.viacosta.util.SessionManager;
import com.viacosta.util.Validators;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class VentasController {

    // Controles de Selección de Viaje
    @FXML private ComboBox<String> cmbRutas;
    @FXML private DatePicker dpFechaViaje;
    @FXML private TableView<AsignacionBusRuta> tblSalidas;
    @FXML private TableColumn<AsignacionBusRuta, String> colHora;
    @FXML private TableColumn<AsignacionBusRuta, String> colRuta;
    @FXML private TableColumn<AsignacionBusRuta, String> colBus;
    @FXML private TableColumn<AsignacionBusRuta, String> colDisponibles;

    // Controles de Cabina y Asientos
    @FXML private GridPane gridAsientos;
    @FXML private Label lblSalidaSeleccionada;
    @FXML private Label lblBusInfo;
    @FXML private Label lblAsientosSeleccionados;
    @FXML private Label lblImporteTotal;

    // Controles de Pasajero
    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private Button btnConsultarReniec;

    // Controles de Pago y Emisión
    @FXML private ComboBox<String> cmbTipoComprobante;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private Button btnEmitirBoleto;

    // Capa de Servicios
    private final ViajeService viajeService;
    private final ClienteService clienteService;
    private final VentaService ventaService;

    // Estado en memoria de la pantalla
    private final ObservableList<AsignacionBusRuta> salidasList = FXCollections.observableArrayList();
    private AsignacionBusRuta salidaActual;
    private final List<AsientoEstadoFecha> asientosActuales = new ArrayList<>();
    private final List<AsientoEstadoFecha> asientosSeleccionados = new ArrayList<>();
    private final Map<Integer, Button> mapaBotonesAsientos = new HashMap<>();

    public VentasController() {
        this.viajeService = new ViajeService();
        this.clienteService = new ClienteService();
        this.ventaService = new VentaService();
    }

    @FXML
    public void initialize() {
        configurarFiltros();
        configurarTablaSalidas();
        configurarFormulario();
        cargarSalidas();
    }

    private void configurarFiltros() {
        cmbRutas.setItems(FXCollections.observableArrayList(
                "TODAS LAS RUTAS",
                "Chimbote -> Huaraz",
                "Huaraz -> Chimbote"
        ));
        cmbRutas.getSelectionModel().select(0);
        dpFechaViaje.setValue(LocalDate.now());

        cmbRutas.setOnAction(e -> filtrarSalidas());
        dpFechaViaje.setOnAction(e -> filtrarSalidas());
    }

    private void configurarTablaSalidas() {
        colHora.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getHoraSalidaFormateada()));
        colRuta.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRutaCompleta()));
        colBus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPlacaBus() + " (" + cell.getValue().getModeloBus() + ")"));
        colDisponibles.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getAsientosDisponibles() + " / " + cell.getValue().getCapacidadTotal() + " libres"
        ));

        tblSalidas.setItems(salidasList);

        tblSalidas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                seleccionarSalida(newVal);
            }
        });
    }

    private void configurarFormulario() {
        cmbTipoComprobante.setItems(FXCollections.observableArrayList("Boleta", "Factura"));
        cmbTipoComprobante.getSelectionModel().select("Boleta");

        cmbMetodoPago.setItems(FXCollections.observableArrayList("Efectivo", "Tarjeta Débito/Crédito", "Yape / Plin"));
        cmbMetodoPago.getSelectionModel().select("Efectivo");

        actualizarTotales();
    }

    private void cargarSalidas() {
        salidasList.clear();
        salidasList.addAll(viajeService.listarSalidasVigentes());
        if (!salidasList.isEmpty()) {
            tblSalidas.getSelectionModel().select(0);
        }
    }

    @FXML
    public void filtrarSalidas() {
        String seleccion = cmbRutas.getValue();
        String origen = null;
        String destino = null;

        if (seleccion != null && seleccion.contains("->")) {
            String[] partes = seleccion.split("->");
            origen = partes[0].trim();
            destino = partes[1].trim();
        }

        LocalDate fecha = dpFechaViaje.getValue();
        List<AsignacionBusRuta> res = viajeService.buscarSalidas(origen, destino, fecha);
        salidasList.setAll(res);

        if (!salidasList.isEmpty()) {
            tblSalidas.getSelectionModel().select(0);
        } else {
            salidaActual = null;
            lblSalidaSeleccionada.setText("No hay salidas programadas para el criterio.");
            lblBusInfo.setText("-");
            gridAsientos.getChildren().clear();
            asientosSeleccionados.clear();
            actualizarTotales();
        }
    }

    private void seleccionarSalida(AsignacionBusRuta salida) {
        this.salidaActual = salida;
        lblSalidaSeleccionada.setText(salida.getRutaCompleta() + " | Fecha: " +
                salida.getFechaSalidaFormateada() + " a las " + salida.getHoraSalidaFormateada());
        lblBusInfo.setText("Unidad: " + salida.getPlacaBus() + " (" + salida.getModeloBus() + ") - 30 Asientos");

        asientosSeleccionados.clear();
        mapaBotonesAsientos.clear();
        actualizarTotales();
        renderizarMapaAsientos(salida.getIdAsignacion(), salida.getIdBus());
    }

    /**
     * Renderiza la distribución de asientos del bus simulando la cabina real
     * (2 columnas izquierda, pasillo central, 2 columnas derecha).
     */
    private void renderizarMapaAsientos(int idAsignacion, int idBus) {
        gridAsientos.getChildren().clear();
        asientosActuales.clear();

        List<AsientoEstadoFecha> lista = viajeService.obtenerAsientosParaViaje(idAsignacion, idBus);
        asientosActuales.addAll(lista);

        // Agrupar asientos por número
        Map<Integer, AsientoEstadoFecha> mapaPorNumero = new HashMap<>();
        for (AsientoEstadoFecha a : lista) {
            mapaPorNumero.put(a.getNumeroAsiento(), a);
        }

        // Diseño: 8 filas de asientos (Filas 1 a 7 con 4 asientos, Fila 8 con 2 asientos = 30 asientos)
        // Columna 0: Ventana Izq, Columna 1: Pasillo Izq, Columna 2: Pasillo Central, Columna 3: Pasillo Der, Columna 4: Ventana Der
        int numeroAsiento = 1;

        for (int fila = 0; fila < 8; fila++) {
            // Columna 0 (Izquierda Ventana)
            if (numeroAsiento <= 30) {
                agregarBotonAsiento(mapaPorNumero.get(numeroAsiento), 0, fila);
                numeroAsiento++;
            }
            // Columna 1 (Izquierda Pasillo)
            if (numeroAsiento <= 30) {
                agregarBotonAsiento(mapaPorNumero.get(numeroAsiento), 1, fila);
                numeroAsiento++;
            }

            // Columna 2: Pasillo Central (Espacio visual)
            Region aisle = new Region();
            aisle.getStyleClass().add("bus-aisle");
            gridAsientos.add(aisle, 2, fila);

            // Columna 3 (Derecha Pasillo)
            if (numeroAsiento <= 30) {
                agregarBotonAsiento(mapaPorNumero.get(numeroAsiento), 3, fila);
                numeroAsiento++;
            }
            // Columna 4 (Derecha Ventana)
            if (numeroAsiento <= 30) {
                agregarBotonAsiento(mapaPorNumero.get(numeroAsiento), 4, fila);
                numeroAsiento++;
            }
        }
    }

    private void agregarBotonAsiento(AsientoEstadoFecha aef, int col, int fila) {
        if (aef == null) return;

        Button btn = new Button();
        btn.getStyleClass().add("seat-btn");
        btn.setText(String.format("%02d", aef.getNumeroAsiento()));

        // Tooltip con detalles del asiento y precio
        Tooltip tip = new Tooltip("Asiento #" + aef.getNumeroAsiento() + "\nCategoría: " + aef.getNombreTipo() +
                "\nPrecio: S/ " + aef.getPrecioFinal() + "\nEstado: " + aef.getEstado());
        Tooltip.install(btn, tip);

        if (aef.isOcupado()) {
            btn.getStyleClass().add("seat-occupied");
            btn.setDisable(true);
        } else {
            btn.getStyleClass().add("seat-available");
            btn.setOnAction(e -> toggleSeleccionAsiento(aef, btn));
        }

        mapaBotonesAsientos.put(aef.getNumeroAsiento(), btn);
        gridAsientos.add(btn, col, fila);
    }

    private void toggleSeleccionAsiento(AsientoEstadoFecha aef, Button btn) {
        if (asientosSeleccionados.contains(aef)) {
            asientosSeleccionados.remove(aef);
            btn.getStyleClass().remove("seat-selected");
            btn.getStyleClass().add("seat-available");
        } else {
            asientosSeleccionados.add(aef);
            btn.getStyleClass().remove("seat-available");
            btn.getStyleClass().add("seat-selected");
        }
        actualizarTotales();
    }

    private void actualizarTotales() {
        if (asientosSeleccionados.isEmpty()) {
            lblAsientosSeleccionados.setText("Ninguno seleccionado");
            lblImporteTotal.setText("S/ 0.00");
            btnEmitirBoleto.setDisable(true);
        } else {
            StringBuilder sb = new StringBuilder();
            BigDecimal total = BigDecimal.ZERO;
            for (AsientoEstadoFecha a : asientosSeleccionados) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("#").append(a.getNumeroAsiento()).append(" (").append(a.getNombreTipo()).append(")");
                total = total.add(a.getPrecioFinal());
            }
            lblAsientosSeleccionados.setText(sb.toString());
            lblImporteTotal.setText(CurrencyFormatter.format(total));
            btnEmitirBoleto.setDisable(false);
        }
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
            // 1. Buscar en BD local de clientes
            Optional<Cliente> optCliente = clienteService.buscarPorDni(dni);

            // 2. Si no está en BD, consultar el participante externo RENIEC (BPMN)
            if (optCliente.isEmpty()) {
                optCliente = clienteService.consultarEnReniec(dni);
            }

            final Optional<Cliente> finalCliente = optCliente;
            Platform.runLater(() -> {
                btnConsultarReniec.setDisable(false);
                btnConsultarReniec.setText("🔍 Consultar RENIEC");

                if (finalCliente.isPresent()) {
                    Cliente c = finalCliente.get();
                    txtNombre.setText(c.getNombre());
                    txtApellido.setText(c.getApellido());
                    if (c.getTelefono() != null) txtTelefono.setText(c.getTelefono());
                    if (c.getCorreo() != null) txtCorreo.setText(c.getCorreo());
                    if (c.getDireccion() != null) txtDireccion.setText(c.getDireccion());
                } else {
                    AlertHelper.showInfo("Consulta RENIEC", "Documento no hallado",
                            "No se encontró registro para el DNI " + dni + ". Complete los datos manualmente.");
                    txtNombre.requestFocus();
                }
            });
        }).start();
    }

    @FXML
    public void onLimpiarCliente() {
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();
        txtDni.requestFocus();
    }

    @FXML
    public void onEmitirBoleto() {
        if (salidaActual == null) {
            AlertHelper.showWarning("Falta Selección", "No ha seleccionado una salida", "Por favor seleccione un viaje del itinerario.");
            return;
        }

        if (asientosSeleccionados.isEmpty()) {
            AlertHelper.showWarning("Falta Asiento", "No ha seleccionado asientos", "Por favor elija al menos un asiento en el mapa del bus.");
            return;
        }

        String dni = txtDni.getText() != null ? txtDni.getText().trim() : "";
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String apellido = txtApellido.getText() != null ? txtApellido.getText().trim() : "";

        if (!Validators.esDniValido(dni)) {
            AlertHelper.showWarning("Datos de Pasajero", "DNI Inválido", "Ingrese un DNI válido de 8 dígitos.");
            txtDni.requestFocus();
            return;
        }

        if (nombre.isEmpty() || apellido.isEmpty()) {
            AlertHelper.showWarning("Datos de Pasajero", "Nombres Incompletos", "Por favor ingrese el nombre y apellido del pasajero.");
            txtNombre.requestFocus();
            return;
        }

        Cliente cliente = new Cliente(
                0,
                dni,
                nombre,
                apellido,
                txtTelefono.getText(),
                txtDireccion.getText(),
                txtCorreo.getText()
        );

        Empleado empleado = SessionManager.getUsuarioActual();
        String tipoComprobante = cmbTipoComprobante.getValue();
        String metodoPago = cmbMetodoPago.getValue();

        btnEmitirBoleto.setDisable(true);
        btnEmitirBoleto.setText("Procesando Venta Atómica...");

        new Thread(() -> {
            try {
                VentaService.ResultadoVenta resultado = ventaService.procesarVenta(
                        cliente,
                        empleado,
                        salidaActual,
                        new ArrayList<>(asientosSeleccionados),
                        tipoComprobante,
                        metodoPago
                );

                Platform.runLater(() -> {
                    btnEmitirBoleto.setDisable(false);
                    btnEmitirBoleto.setText("💳  Confirmar y Emitir Boleto");

                    // Refrescar mapa de asientos y tabla de cupos
                    asientosSeleccionados.clear();
                    actualizarTotales();
                    renderizarMapaAsientos(salidaActual.getIdAsignacion(), salidaActual.getIdBus());
                    cargarSalidas();

                    // Abrir comprobante / ticket térmico oficial
                    abrirModalTicket(resultado);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnEmitirBoleto.setDisable(false);
                    btnEmitirBoleto.setText("💳  Confirmar y Emitir Boleto");
                    AlertHelper.showError("Error en Transacción de Venta", "No se pudo registrar la venta", e.getMessage());
                    // Refrescar asientos por si cambió la disponibilidad
                    renderizarMapaAsientos(salidaActual.getIdAsignacion(), salidaActual.getIdBus());
                });
            }
        }).start();
    }

    private void abrirModalTicket(VentaService.ResultadoVenta resultado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TicketModal.fxml"));
            Parent root = loader.load();

            TicketModalController controller = loader.getController();
            controller.setDatosVenta(resultado);

            Stage modalStage = new Stage();
            modalStage.setTitle("VIACOSTA - Emisión de Comprobante Oficial #" + resultado.getComprobante().getNumeroComprobante());
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(btnEmitirBoleto.getScene().getWindow());

            Scene scene = new Scene(root, 480, 680);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            modalStage.setScene(scene);
            modalStage.setResizable(false);
            modalStage.centerOnScreen();
            modalStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showInfo("Venta Registrada", "Comprobante " + resultado.getComprobante().getNumeroComprobante(),
                    "La venta fue guardada con éxito en la base de datos.");
        }
    }
}
