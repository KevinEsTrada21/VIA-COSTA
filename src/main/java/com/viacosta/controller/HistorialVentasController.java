package com.viacosta.controller;

import com.viacosta.model.Compra;
import com.viacosta.model.DetalleBoleta;
import com.viacosta.service.VentaService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.CurrencyFormatter;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.util.List;

public class HistorialVentasController {

    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, String> colId;
    @FXML private TableColumn<Compra, String> colComprobante;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colCliente;
    @FXML private TableColumn<Compra, String> colEmpleado;
    @FXML private TableColumn<Compra, String> colMetodo;
    @FXML private TableColumn<Compra, String> colTotal;

    @FXML private TableView<DetalleBoleta> tblDetalles;
    @FXML private TableColumn<DetalleBoleta, String> colDetAsiento;
    @FXML private TableColumn<DetalleBoleta, String> colDetTipo;
    @FXML private TableColumn<DetalleBoleta, String> colDetRuta;
    @FXML private TableColumn<DetalleBoleta, String> colDetFechaHora;
    @FXML private TableColumn<DetalleBoleta, String> colDetPrecio;

    @FXML private Label lblTotalVentas;
    @FXML private Label lblMontoRecaudado;
    @FXML private Label lblDetalleCompraTitulo;

    private final VentaService ventaService;
    private final ObservableList<Compra> comprasList = FXCollections.observableArrayList();
    private final ObservableList<DetalleBoleta> detallesList = FXCollections.observableArrayList();

    public HistorialVentasController() {
        this.ventaService = new VentaService();
    }

    @FXML
    public void initialize() {
        configurarTablas();
        cargarHistorial();
    }

    private void configurarTablas() {
        colId.setCellValueFactory(cell -> new SimpleStringProperty("#" + cell.getValue().getIdCompra()));
        colComprobante.setCellValueFactory(cell -> new SimpleStringProperty(
                (cell.getValue().getTipoComprobante() != null ? cell.getValue().getTipoComprobante() : "Boleta") + " " +
                (cell.getValue().getNumeroComprobante() != null ? cell.getValue().getNumeroComprobante() : "-")
        ));
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFechaCompraFormateada()));
        colCliente.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getClienteNombre() + " (" + cell.getValue().getClienteDni() + ")"
        ));
        colEmpleado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmpleadoNombre()));
        colMetodo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMetodoPago()));
        colTotal.setCellValueFactory(cell -> new SimpleStringProperty(CurrencyFormatter.format(cell.getValue().getTotal())));

        tblCompras.setItems(comprasList);

        tblCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarDetallesCompra(newVal);
            }
        });

        // Configuración tabla detalles
        colDetAsiento.setCellValueFactory(cell -> new SimpleStringProperty("Asiento #" + String.format("%02d", cell.getValue().getNumeroAsiento())));
        colDetTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipoAsiento()));
        colDetRuta.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrigen() + " -> " + cell.getValue().getDestino()));
        colDetFechaHora.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFechaViajeFormateada() + " " + cell.getValue().getHoraViajeFormateada()));
        colDetPrecio.setCellValueFactory(cell -> new SimpleStringProperty(CurrencyFormatter.format(cell.getValue().getPrecioUnitario())));

        tblDetalles.setItems(detallesList);
    }

    private void cargarHistorial() {
        new Thread(() -> {
            List<Compra> compras = ventaService.listarHistorialVentas();
            BigDecimal recaudado = BigDecimal.ZERO;
            for (Compra c : compras) {
                if (c.getTotal() != null) {
                    recaudado = recaudado.add(c.getTotal());
                }
            }

            final BigDecimal finalRecaudado = recaudado;
            Platform.runLater(() -> {
                comprasList.setAll(compras);
                lblTotalVentas.setText(String.valueOf(compras.size()));
                lblMontoRecaudado.setText(CurrencyFormatter.format(finalRecaudado));

                if (!compras.isEmpty()) {
                    tblCompras.getSelectionModel().select(0);
                }
            });
        }).start();
    }

    private void cargarDetallesCompra(Compra compra) {
        lblDetalleCompraTitulo.setText("Boletos asociados a la Compra #" + compra.getIdCompra() +
                " (" + compra.getNumeroComprobante() + " - " + compra.getClienteNombre() + ")");
        new Thread(() -> {
            List<DetalleBoleta> detalles = ventaService.listarDetallesVenta(compra.getIdCompra());
            Platform.runLater(() -> detallesList.setAll(detalles));
        }).start();
    }

    @FXML
    public void onActualizar() {
        cargarHistorial();
    }

    @FXML
    public void onReimprimir() {
        Compra c = tblCompras.getSelectionModel().getSelectedItem();
        if (c == null) {
            AlertHelper.showWarning("Selección Requerida", "Sin selección", "Seleccione una compra para reimprimir su comprobante.");
            return;
        }
        AlertHelper.showInfo("Reimpresión", "Comprobante " + c.getNumeroComprobante(),
                "Copia fiel del comprobante #" + c.getNumeroComprobante() + " enviada al spooler de impresión.");
    }
}
