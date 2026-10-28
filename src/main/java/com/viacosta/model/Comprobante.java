package com.viacosta.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comprobante {
    private int idComprobante;
    private String tipoComprobante; // 'Boleta', 'Factura'
    private String numeroComprobante; // e.g. 'B001-000002'
    private LocalDateTime fechaEmision;

    public Comprobante() {}

    public Comprobante(int idComprobante, String tipoComprobante, String numeroComprobante, LocalDateTime fechaEmision) {
        this.idComprobante = idComprobante;
        this.tipoComprobante = tipoComprobante;
        this.numeroComprobante = numeroComprobante;
        this.fechaEmision = fechaEmision;
    }

    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getFechaEmisionFormateada() {
        if (fechaEmision == null) return "";
        return fechaEmision.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public String toString() {
        return tipoComprobante + " " + numeroComprobante;
    }
}
