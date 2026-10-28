package com.viacosta.model;

import java.math.BigDecimal;

public class Asiento {
    private int idAsiento;
    private int idBus;
    private int idTipoAsiento;
    private int numeroAsiento;
    private BigDecimal precioBase;

    // Campos enriquecidos para consultas de negocio y presentación
    private String nombreTipo;
    private BigDecimal cargoAdicional;

    public Asiento() {}

    public Asiento(int idAsiento, int idBus, int idTipoAsiento, int numeroAsiento, BigDecimal precioBase) {
        this.idAsiento = idAsiento;
        this.idBus = idBus;
        this.idTipoAsiento = idTipoAsiento;
        this.numeroAsiento = numeroAsiento;
        this.precioBase = precioBase;
    }

    public int getIdAsiento() { return idAsiento; }
    public void setIdAsiento(int idAsiento) { this.idAsiento = idAsiento; }

    public int getIdBus() { return idBus; }
    public void setIdBus(int idBus) { this.idBus = idBus; }

    public int getIdTipoAsiento() { return idTipoAsiento; }
    public void setIdTipoAsiento(int idTipoAsiento) { this.idTipoAsiento = idTipoAsiento; }

    public int getNumeroAsiento() { return numeroAsiento; }
    public void setNumeroAsiento(int numeroAsiento) { this.numeroAsiento = numeroAsiento; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public BigDecimal getCargoAdicional() { return cargoAdicional; }
    public void setCargoAdicional(BigDecimal cargoAdicional) { this.cargoAdicional = cargoAdicional; }

    public BigDecimal getPrecioTotal() {
        BigDecimal base = (precioBase != null) ? precioBase : BigDecimal.ZERO;
        BigDecimal extra = (cargoAdicional != null) ? cargoAdicional : BigDecimal.ZERO;
        return base.add(extra);
    }

    @Override
    public String toString() {
        return "Asiento " + numeroAsiento + " (" + (nombreTipo != null ? nombreTipo : "Estándar") + ")";
    }
}
