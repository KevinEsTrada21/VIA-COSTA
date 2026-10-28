package com.viacosta.model;

public class Empleado {
    private int idEmpleado;
    private int idSede;
    private String dni;
    private String nombre;
    private String apellido;
    private String usuario;
    private String claveHash;
    private String nombreRol;
    private String nombreSede;

    public Empleado() {}

    public Empleado(int idEmpleado, int idSede, String dni, String nombre, String apellido, String usuario, String claveHash) {
        this.idEmpleado = idEmpleado;
        this.idSede = idSede;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.claveHash = claveHash;
    }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public int getIdSede() { return idSede; }
    public void setIdSede(int idSede) { this.idSede = idSede; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClaveHash() { return claveHash; }
    public void setClaveHash(String claveHash) { this.claveHash = claveHash; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + usuario + " - " + nombreRol + ")";
    }
}
