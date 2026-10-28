package com.viacosta.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AsientoEstadoFecha {
    private int idEstado;
    private int idAsiento;
    private int idAsignacion;
    private String estado; // 'Disponible', 'Reservado', 'Ocupado'
    private LocalDateTime fechaActualizacion;

    // Campos enriquecidos para la UI interactiva del bus
    private int numeroAsiento;
    private String nombreTipo; // 'Estandar', 'VIP', 'Panoramico'
    private BigDecimal precioBase;
    private BigDecimal cargoAdicional;

    public AsientoEstadoFecha() {}

    public AsientoEstadoFecha(int idEstado, int idAsiento, int idAsignacion, String estado, LocalDateTime fechaActualizacion) {
        this.idEstado = idEstado;
        this.idAsiento = idAsiento;
        this.idAsignacion = idAsignacion;
        this.estado = estado;
        this.fechaActualizacion = fechaActualizacion;
    }

    public int getIdEstado() { return idEstado; }
    public void setIdEstado(int idEstado) { this.idEstado = idEstado; }

    public int getIdAsiento() { return idAsiento; }
    public void setIdAsiento(int idAsiento) { this.idAsiento = idAsiento; }

    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public int getNumeroAsiento() { return numeroAsiento; }
    public void setNumeroAsiento(int numeroAsiento) { this.numeroAsiento = numeroAsiento; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public BigDecimal getCargoAdicional() { return cargoAdicional; }
    public void setCargoAdicional(BigDecimal cargoAdicional) { this.cargoAdicional = cargoAdicional; }

    public BigDecimal getPrecioFinal() {
        BigDecimal base = (precioBase != null) ? precioBase : BigDecimal.ZERO;
        BigDecimal extra = (cargoAdicional != null) ? cargoAdicional : BigDecimal.ZERO;
        return base.add(extra);
    }

    public boolean isDisponible() {
        return "Disponible".equalsIgnoreCase(estado);
    }

    public boolean isOcupado() {
        return "Ocupado".equalsIgnoreCase(estado);
    }

    @Override
    public String toString() {
        return "Asiento #" + numeroAsiento + " [" + nombreTipo + "] - " + estado + " (S/ " + getPrecioFinal() + ")";
    }
}
