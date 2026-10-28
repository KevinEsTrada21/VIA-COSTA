package com.viacosta.controller;

import com.viacosta.model.AsignacionBusRuta;
import com.viacosta.model.Bus;
import com.viacosta.model.Ruta;
import com.viacosta.service.ViajeService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ViajesController {

    @FXML private TableView<AsignacionBusRuta> tblSalidas;
    @FXML private TableColumn<AsignacionBusRuta, String> colId;
    @FXML private TableColumn<AsignacionBusRuta, String> colFecha;
    @FXML private TableColumn<AsignacionBusRuta, String> colHora;
    @FXML private TableColumn<AsignacionBusRuta, String> colRuta;
    @FXML private TableColumn<AsignacionBusRuta, String> colBus;
    @FXML private TableColumn<AsignacionBusRuta, String> colCapacidad;
    @FXML private TableColumn<AsignacionBusRuta, String> colDisponibles;
    @FXML private TableColumn<AsignacionBusRuta, String> colOcupacion;

    @FXML private ListView<String> lvRutas;
    @FXML private ListView<String> lvBuses;

    @FXML private Label lblTotalSalidas;
    @FXML private Label lblTotalBuses;
    @FXML private Label lblTotalRutas;

    private final ViajeService viajeService;
    private final ObservableList<AsignacionBusRuta> salidasList = FXCollections.observableArrayList();

    public ViajesController() {
        this.viajeService = new ViajeService();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getIdAsignacion())));
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFechaSalidaFormateada()));
        colHora.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getHoraSalidaFormateada()));
        colRuta.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRutaCompleta()));
        colBus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPlacaBus()));
        colCapacidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCapacidadTotal() + " asientos"));
        colDisponibles.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAsientosDisponibles() + " libres"));
        colOcupacion.setCellValueFactory(cell -> {
            int tot = cell.getValue().getCapacidadTotal();
            int lib = cell.getValue().getAsientosDisponibles();
            int ocu = tot - lib;
            double pct = tot > 0 ? (ocu * 100.0 / tot) : 0;
            return new SimpleStringProperty(String.format("%.1f %% (%d/%d)", pct, ocu, tot));
        });

        tblSalidas.setItems(salidasList);
    }

    private void cargarDatos() {
        new Thread(() -> {
            List<AsignacionBusRuta> salidas = viajeService.listarSalidasVigentes();
            List<Ruta> rutas = viajeService.listarRutas();
            List<Bus> buses = viajeService.listarBuses();

            Platform.runLater(() -> {
                salidasList.setAll(salidas);
                lblTotalSalidas.setText(String.valueOf(salidas.size()));
                lblTotalBuses.setText(String.valueOf(buses.size()));
                lblTotalRutas.setText(String.valueOf(rutas.size()));

                lvRutas.getItems().clear();
                for (Ruta r : rutas) {
                    lvRutas.getItems().add(r.getRutaCompleta() + " (Duración: " + r.getDuracionHoras() + " hrs)");
                }

                lvBuses.getItems().clear();
                for (Bus b : buses) {
                    lvBuses.getItems().add("Placa: " + b.getPlaca() + " | " + b.getModelo() + " (" + b.getCapacidadAsientos() + " asientos)");
                }
            });
        }).start();
    }

    @FXML
    public void onActualizar() {
        cargarDatos();
    }
}
