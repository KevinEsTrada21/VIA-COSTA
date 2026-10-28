package com.viacosta.model;

public class Sede {
    private int idSede;
    private String nombreSede;
    private String ciudad;
    private int idEmpresa;

    public Sede() {}

    public Sede(int idSede, String nombreSede, String ciudad, int idEmpresa) {
        this.idSede = idSede;
        this.nombreSede = nombreSede;
        this.ciudad = ciudad;
        this.idEmpresa = idEmpresa;
    }

    public int getIdSede() { return idSede; }
    public void setIdSede(int idSede) { this.idSede = idSede; }

    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    @Override
    public String toString() {
        return ciudad + " - " + nombreSede;
    }
}
