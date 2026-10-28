package com.viacosta.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DetalleBoleta {
    private int idDetalle;
    private int idCompra;
    private int idAsiento;
    private int idAsignacion;
    private int idComprobante;
    private LocalDate fechaViaje;
    private LocalTime horaViaje;
    private BigDecimal precioUnitario;

    // Campos enriquecidos
    private int numeroAsiento;
    private String tipoAsiento;
    private String origen;
    private String destino;
    private String placaBus;
    private String numeroComprobante;

    public DetalleBoleta() {}

    public DetalleBoleta(int idDetalle, int idCompra, int idAsiento, int idAsignacion, int idComprobante,
                         LocalDate fechaViaje, LocalTime horaViaje, BigDecimal precioUnitario) {
        this.idDetalle = idDetalle;
        this.idCompra = idCompra;
        this.idAsiento = idAsiento;
        this.idAsignacion = idAsignacion;
        this.idComprobante = idComprobante;
        this.fechaViaje = fechaViaje;
        this.horaViaje = horaViaje;
        this.precioUnitario = precioUnitario;
    }

    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public int getIdAsiento() { return idAsiento; }
    public void setIdAsiento(int idAsiento) { this.idAsiento = idAsiento; }

    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }

    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }

    public LocalDate getFechaViaje() { return fechaViaje; }
    public void setFechaViaje(LocalDate fechaViaje) { this.fechaViaje = fechaViaje; }

    public LocalTime getHoraViaje() { return horaViaje; }
    public void setHoraViaje(LocalTime horaViaje) { this.horaViaje = horaViaje; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getNumeroAsiento() { return numeroAsiento; }
    public void setNumeroAsiento(int numeroAsiento) { this.numeroAsiento = numeroAsiento; }

    public String getTipoAsiento() { return tipoAsiento; }
    public void setTipoAsiento(String tipoAsiento) { this.tipoAsiento = tipoAsiento; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getPlacaBus() { return placaBus; }
    public void setPlacaBus(String placaBus) { this.placaBus = placaBus; }

    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

    public String getFechaViajeFormateada() {
        if (fechaViaje == null) return "";
        return fechaViaje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getHoraViajeFormateada() {
        if (horaViaje == null) return "";
        return horaViaje.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public String toString() {
        return "Asiento #" + numeroAsiento + " | " + origen + " -> " + destino + " | S/ " + precioUnitario;
    }
}
