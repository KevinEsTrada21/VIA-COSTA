package com.viacosta.dao.impl;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.dao.EmpleadoDAO;
import com.viacosta.model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class EmpleadoDAOImpl implements EmpleadoDAO {

    @Override
    public Optional<Empleado> autenticar(String usuario, String claveHash) {
        String sql =
                "SELECT e.id_empleado, e.id_sede, e.dni, e.nombre, e.apellido, e.usuario, e.clave_hash, " +
                "s.nombre_sede, r.nombre_rol " +
                "FROM empleados e " +
                "INNER JOIN sedes s ON e.id_sede = s.id_sede " +
                "LEFT JOIN empleado_roles er ON e.id_empleado = er.id_empleado " +
                "LEFT JOIN roles r ON er.id_rol = r.id_rol " +
                "WHERE LOWER(e.usuario) = ? AND e.clave_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.trim().toLowerCase());
            ps.setString(2, claveHash.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empleado emp = new Empleado();
                    emp.setIdEmpleado(rs.getInt("id_empleado"));
                    emp.setIdSede(rs.getInt("id_sede"));
                    emp.setDni(rs.getString("dni"));
                    emp.setNombre(rs.getString("nombre"));
                    emp.setApellido(rs.getString("apellido"));
                    emp.setUsuario(rs.getString("usuario"));
                    emp.setClaveHash(rs.getString("clave_hash"));
                    emp.setNombreSede(rs.getString("nombre_sede"));
                    emp.setNombreRol(rs.getString("nombre_rol"));
                    return Optional.of(emp);
                }
            }
        } catch (SQLException e) {
            System.err.println("[EmpleadoDAOImpl] Error en autenticar: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Empleado> buscarPorId(int idEmpleado) {
        String sql =
                "SELECT e.id_empleado, e.id_sede, e.dni, e.nombre, e.apellido, e.usuario, e.clave_hash, " +
                "s.nombre_sede, r.nombre_rol " +
                "FROM empleados e " +
                "INNER JOIN sedes s ON e.id_sede = s.id_sede " +
                "LEFT JOIN empleado_roles er ON e.id_empleado = er.id_empleado " +
                "LEFT JOIN roles r ON er.id_rol = r.id_rol " +
                "WHERE e.id_empleado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empleado emp = new Empleado();
                    emp.setIdEmpleado(rs.getInt("id_empleado"));
                    emp.setIdSede(rs.getInt("id_sede"));
                    emp.setDni(rs.getString("dni"));
                    emp.setNombre(rs.getString("nombre"));
                    emp.setApellido(rs.getString("apellido"));
                    emp.setUsuario(rs.getString("usuario"));
                    emp.setClaveHash(rs.getString("clave_hash"));
                    emp.setNombreSede(rs.getString("nombre_sede"));
                    emp.setNombreRol(rs.getString("nombre_rol"));
                    return Optional.of(emp);
                }
            }
        } catch (SQLException e) {
            System.err.println("[EmpleadoDAOImpl] Error en buscarPorId: " + e.getMessage());
        }
        return Optional.empty();
    }
}
