package com.viacosta.model;

import java.math.BigDecimal;

public class Ruta {
    private int idRuta;
    private String origen;
    private String destino;
    private BigDecimal duracionHoras;

    public Ruta() {}

    public Ruta(int idRuta, String origen, String destino, BigDecimal duracionHoras) {
        this.idRuta = idRuta;
        this.origen = origen;
        this.destino = destino;
        this.duracionHoras = duracionHoras;
    }

    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public BigDecimal getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(BigDecimal duracionHoras) { this.duracionHoras = duracionHoras; }

    public String getNombreRuta() {
        return origen + " -> " + destino;
    }

    public String getRutaCompleta() {
        return getNombreRuta();
    }

    @Override
    public String toString() {
        return getNombreRuta() + " (" + duracionHoras + " hrs)";
    }
}
