package com.viacosta.model;

public class Empresa {
    private int idEmpresa;
    private String ruc;
    private String razonSocial;
    private String direccion;

    public Empresa() {}

    public Empresa(int idEmpresa, String ruc, String razonSocial, String direccion) {
        this.idEmpresa = idEmpresa;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
    }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return razonSocial + " (" + ruc + ")";
    }
}
