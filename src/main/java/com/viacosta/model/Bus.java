package com.viacosta.model;

public class Bus {
    private int idBus;
    private String placa;
    private String modelo;
    private int capacidadAsientos;

    public Bus() {}

    public Bus(int idBus, String placa, String modelo, int capacidadAsientos) {
        this.idBus = idBus;
        this.placa = placa;
        this.modelo = modelo;
        this.capacidadAsientos = capacidadAsientos;
    }

    public int getIdBus() { return idBus; }
    public void setIdBus(int idBus) { this.idBus = idBus; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getCapacidadAsientos() { return capacidadAsientos; }
    public void setCapacidadAsientos(int capacidadAsientos) { this.capacidadAsientos = capacidadAsientos; }

    @Override
    public String toString() {
        return placa + " (" + modelo + " - " + capacidadAsientos + " asientos)";
    }
}
