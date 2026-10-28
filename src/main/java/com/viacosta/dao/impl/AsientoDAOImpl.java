package com.viacosta.dao.impl;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.dao.AsientoDAO;
import com.viacosta.model.AsientoEstadoFecha;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsientoDAOImpl implements AsientoDAO {

    @Override
    public List<AsientoEstadoFecha> listarAsientosPorAsignacion(int idAsignacion) {
        List<AsientoEstadoFecha> lista = new ArrayList<>();
        String sql =
                "SELECT aef.id_estado, aef.id_asiento, aef.id_asignacion, aef.estado, aef.fecha_actualizacion, " +
                "a.numero_asiento, a.precio_base, " +
                "ta.nombre AS nombre_tipo, ta.cargo_adicional " +
                "FROM asiento_estado_fecha aef " +
                "INNER JOIN asientos a ON aef.id_asiento = a.id_asiento " +
                "INNER JOIN tipo_asiento ta ON a.id_tipo_asiento = ta.id_tipo_asiento " +
                "WHERE aef.id_asignacion = ? " +
                "ORDER BY a.numero_asiento ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsientoEstado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AsientoDAOImpl] Error en listarAsientosPorAsignacion: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizarEstado(int idAsiento, int idAsignacion, String nuevoEstado, Connection conn) throws SQLException {
        String sql = "UPDATE asiento_estado_fecha SET estado = ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id_asiento = ? AND id_asignacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idAsiento);
            ps.setInt(3, idAsignacion);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean verificarDisponibilidad(int idAsiento, int idAsignacion, Connection conn) throws SQLException {
        String sql = "SELECT estado FROM asiento_estado_fecha WHERE id_asiento = ? AND id_asignacion = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAsiento);
            ps.setInt(2, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "Disponible".equalsIgnoreCase(rs.getString("estado"));
                }
            }
        }
        return false;
    }

    @Override
    public void asegurarAsientosParaAsignacion(int idAsignacion, int idBus) {
        String sqlCheck = "SELECT COUNT(*) FROM asiento_estado_fecha WHERE id_asignacion = ?";
        String sqlInsert = "INSERT INTO asiento_estado_fecha (id_asiento, id_asignacion, estado) " +
                           "SELECT a.id_asiento, ?, 'Disponible' FROM asientos a " +
                           "WHERE a.id_bus = ? AND NOT EXISTS (" +
                           "    SELECT 1 FROM asiento_estado_fecha aef " +
                           "    WHERE aef.id_asiento = a.id_asiento AND aef.id_asignacion = ?" +
                           ")";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            psCheck.setInt(1, idAsignacion);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                        psIns.setInt(1, idAsignacion);
                        psIns.setInt(2, idBus);
                        psIns.setInt(3, idAsignacion);
                        psIns.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[AsientoDAOImpl] Error en asegurarAsientosParaAsignacion: " + e.getMessage());
        }
    }

    private AsientoEstadoFecha mapearAsientoEstado(ResultSet rs) throws SQLException {
        AsientoEstadoFecha aef = new AsientoEstadoFecha();
        aef.setIdEstado(rs.getInt("id_estado"));
        aef.setIdAsiento(rs.getInt("id_asiento"));
        aef.setIdAsignacion(rs.getInt("id_asignacion"));
        aef.setEstado(rs.getString("estado"));

        Timestamp ts = rs.getTimestamp("fecha_actualizacion");
        if (ts != null) aef.setFechaActualizacion(ts.toLocalDateTime());

        aef.setNumeroAsiento(rs.getInt("numero_asiento"));
        aef.setNombreTipo(rs.getString("nombre_tipo"));
        aef.setPrecioBase(rs.getBigDecimal("precio_base"));
        aef.setCargoAdicional(rs.getBigDecimal("cargo_adicional"));

        return aef;
    }
}
