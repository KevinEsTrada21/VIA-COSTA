package com.viacosta.dao.impl;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.dao.ClienteDAO;
import com.viacosta.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public Optional<Cliente> buscarPorDni(String dni) {
        String sql = "SELECT id_cliente, dni, nombre, apellido, telefono, direccion, correo FROM clientes WHERE dni = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAOImpl] Error en buscarPorDni: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> buscarPorId(int idCliente) {
        String sql = "SELECT id_cliente, dni, nombre, apellido, telefono, direccion, correo FROM clientes WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAOImpl] Error en buscarPorId: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, dni, nombre, apellido, telefono, direccion, correo FROM clientes ORDER BY id_cliente DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAOImpl] Error en listarTodos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Cliente> buscarPorCriterio(String texto) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, dni, nombre, apellido, telefono, direccion, correo FROM clientes " +
                     "WHERE dni LIKE ? OR LOWER(nombre) LIKE ? OR LOWER(apellido) LIKE ? ORDER BY nombre ASC";
        String pattern = "%" + texto.toLowerCase().trim() + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto.trim() + "%");
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClienteDAOImpl] Error en buscarPorCriterio: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Cliente registrar(Cliente cliente) {
        String sql = "INSERT INTO clientes (dni, nombre, apellido, telefono, direccion, correo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getDni().trim());
            ps.setString(2, cliente.getNombre().trim());
            ps.setString(3, cliente.getApellido().trim());
            ps.setString(4, cliente.getTelefono() != null ? cliente.getTelefono().trim() : null);
            ps.setString(5, cliente.getDireccion() != null ? cliente.getDireccion().trim() : null);
            ps.setString(6, cliente.getCorreo() != null ? cliente.getCorreo().trim() : null);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setIdCliente(rs.getInt(1));
                    }
                }
            }
            return cliente;
        } catch (SQLException e) {
            System.err.println("[ClienteDAOImpl] Error en registrar: " + e.getMessage());
            throw new RuntimeException("Error al registrar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, direccion = ?, correo = ? WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre().trim());
            ps.setString(2, cliente.getApellido().trim());
            ps.setString(3, cliente.getTelefono() != null ? cliente.getTelefono().trim() : null);
            ps.setString(4, cliente.getDireccion() != null ? cliente.getDireccion().trim() : null);
            ps.setString(5, cliente.getCorreo() != null ? cliente.getCorreo().trim() : null);
            ps.setInt(6, cliente.getIdCliente());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ClienteDAOImpl] Error en actualizar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ClienteDAOImpl] Error en eliminar: " + e.getMessage());
            return false;
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                rs.getString("correo")
        );
    }
}
