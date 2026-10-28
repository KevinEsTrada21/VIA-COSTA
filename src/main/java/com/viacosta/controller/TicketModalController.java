package com.viacosta.controller;

import com.viacosta.model.DetalleBoleta;
import com.viacosta.service.VentaService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.CurrencyFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TicketModalController {

    @FXML private Label lblEmpresa;
    @FXML private Label lblRuc;
    @FXML private Label lblTipoNumeroComprobante;
    @FXML private Label lblFechaEmision;

    @FXML private Label lblPasajero;
    @FXML private Label lblDni;
    @FXML private Label lblRuta;
    @FXML private Label lblFechaSalida;
    @FXML private Label lblHoraSalida;
    @FXML private Label lblBus;
    @FXML private Label lblMetodoPago;

    @FXML private VBox vBoxDetalleAsientos;
    @FXML private Label lblTotal;
    @FXML private Button btnCerrar;

    public void setDatosVenta(VentaService.ResultadoVenta res) {
        lblEmpresa.setText("Empresa de Transportes Vía Costa Express S.R.L.");
        lblRuc.setText("RUC: 20601234567 | Chimbote - Perú");

        String tipoComp = res.getComprobante().getTipoComprobante().toUpperCase();
        lblTipoNumeroComprobante.setText(tipoComp + " ELECTRÓNICA\n" + res.getComprobante().getNumeroComprobante());
        lblFechaEmision.setText("Emisión: " + res.getComprobante().getFechaEmisionFormateada());

        lblPasajero.setText(res.getCliente().getNombreCompleto());
        lblDni.setText(res.getCliente().getDni());
        lblRuta.setText(res.getViaje().getRutaCompleta());
        lblFechaSalida.setText(res.getViaje().getFechaSalidaFormateada());
        lblHoraSalida.setText(res.getViaje().getHoraSalidaFormateada());
        lblBus.setText(res.getViaje().getPlacaBus() + " (" + res.getViaje().getModeloBus() + ")");
        lblMetodoPago.setText(res.getCompra().getMetodoPago());

        vBoxDetalleAsientos.getChildren().clear();
        for (DetalleBoleta d : res.getDetalles()) {
            Label lbl = new Label("• Asiento #" + String.format("%02d", d.getNumeroAsiento()) +
                    " [" + d.getTipoAsiento() + "] : " + CurrencyFormatter.format(d.getPrecioUnitario()));
            lbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");
            vBoxDetalleAsientos.getChildren().add(lbl);
        }

        lblTotal.setText(CurrencyFormatter.format(res.getCompra().getTotal()));
    }

    @FXML
    public void onImprimir() {
        AlertHelper.showInfo("Impresión de Boleto", "Enviando a Cola de Impresión Térmica",
                "El boleto de viaje ha sido enviado al spooler de impresión local exitosamente.");
    }

    @FXML
    public void onCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
