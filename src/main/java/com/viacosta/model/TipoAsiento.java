package com.viacosta.model;

import java.math.BigDecimal;

public class TipoAsiento {
    private int idTipoAsiento;
    private String nombre; // 'Estandar', 'VIP', 'Panoramico'
    private String descripcion;
    private BigDecimal cargoAdicional;

    public TipoAsiento() {}

    public TipoAsiento(int idTipoAsiento, String nombre, String descripcion, BigDecimal cargoAdicional) {
        this.idTipoAsiento = idTipoAsiento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cargoAdicional = cargoAdicional;
    }

    public int getIdTipoAsiento() { return idTipoAsiento; }
    public void setIdTipoAsiento(int idTipoAsiento) { this.idTipoAsiento = idTipoAsiento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getCargoAdicional() { return cargoAdicional; }
    public void setCargoAdicional(BigDecimal cargoAdicional) { this.cargoAdicional = cargoAdicional; }

    @Override
    public String toString() {
        return nombre + " (+" + (cargoAdicional != null ? cargoAdicional.toString() : "0.00") + ")";
    }
}
