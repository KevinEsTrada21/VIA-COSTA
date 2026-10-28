package com.viacosta.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Compra {
    private int idCompra;
    private int idCliente;
    private int idEmpleado;
    private LocalDateTime fechaCompra;
    private BigDecimal total;
    private String metodoPago; // 'Efectivo', 'Tarjeta', 'Yape/Plin'

    // Campos enriquecidos
    private String clienteNombre;
    private String clienteDni;
    private String empleadoNombre;
    private String numeroComprobante;
    private String tipoComprobante;

    public Compra() {}

    public Compra(int idCompra, int idCliente, int idEmpleado, LocalDateTime fechaCompra, BigDecimal total, String metodoPago) {
        this.idCompra = idCompra;
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.fechaCompra = fechaCompra;
        this.total = total;
        this.metodoPago = metodoPago;
    }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }

    public String getEmpleadoNombre() { return empleadoNombre; }
    public void setEmpleadoNombre(String empleadoNombre) { this.empleadoNombre = empleadoNombre; }

    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getFechaCompraFormateada() {
        if (fechaCompra == null) return "";
        return fechaCompra.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "Compra #" + idCompra + " - Total: S/ " + total + " (" + metodoPago + ")";
    }
}
