package com.viacosta.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AsignacionBusRuta {
    private int idAsignacion;
    private int idBus;
    private int idRuta;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;

    // Campos enriquecidos desde JOIN
    private String placaBus;
    private String modeloBus;
    private String origen;
    private String destino;
    private BigDecimal duracionHoras;
    private int asientosDisponibles;
    private int capacidadTotal;

    public AsignacionBusRuta() {}

    public AsignacionBusRuta(int idAsignacion, int idBus, int idRuta, LocalDate fechaSalida, LocalTime horaSalida) {
        this.idAsignacion = idAsignacion;
        this.idBus = idBus;
        this.idRuta = idRuta;
        this.fechaSalida = fechaSalida;
        this.horaSalida = horaSalida;
    }

    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }

    public int getIdBus() { return idBus; }
    public void setIdBus(int idBus) { this.idBus = idBus; }

    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }

    public String getPlacaBus() { return placaBus; }
    public void setPlacaBus(String placaBus) { this.placaBus = placaBus; }

    public String getModeloBus() { return modeloBus; }
    public void setModeloBus(String modeloBus) { this.modeloBus = modeloBus; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public BigDecimal getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(BigDecimal duracionHoras) { this.duracionHoras = duracionHoras; }

    public int getAsientosDisponibles() { return asientosDisponibles; }
    public void setAsientosDisponibles(int asientosDisponibles) { this.asientosDisponibles = asientosDisponibles; }

    public int getCapacidadTotal() { return capacidadTotal; }
    public void setCapacidadTotal(int capacidadTotal) { this.capacidadTotal = capacidadTotal; }

    public String getRutaCompleta() {
        return (origen != null ? origen : "") + " -> " + (destino != null ? destino : "");
    }

    public String getFechaSalidaFormateada() {
        if (fechaSalida == null) return "";
        return fechaSalida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getHoraSalidaFormateada() {
        if (horaSalida == null) return "";
        return horaSalida.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public String toString() {
        return getRutaCompleta() + " | " + getFechaSalidaFormateada() + " " + getHoraSalidaFormateada() + " (Bus: " + placaBus + ")";
    }
}
