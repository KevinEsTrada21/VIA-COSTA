package com.viacosta.dao.impl;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.dao.ViajeDAO;
import com.viacosta.model.AsignacionBusRuta;
import com.viacosta.model.Bus;
import com.viacosta.model.Ruta;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViajeDAOImpl implements ViajeDAO {

    private static final String BASE_QUERY =
            "SELECT abr.id_asignacion, abr.id_bus, abr.id_ruta, abr.fecha_salida, abr.hora_salida, " +
            "b.placa, b.modelo, b.capacidad_asientos, " +
            "r.origen, r.destino, r.duracion_horas, " +
            "COALESCE(SUM(CASE WHEN aef.estado = 'Disponible' THEN 1 ELSE 0 END), b.capacidad_asientos) AS disponibles " +
            "FROM asignacion_buses_rutas abr " +
            "INNER JOIN buses b ON abr.id_bus = b.id_bus " +
            "INNER JOIN rutas r ON abr.id_ruta = r.id_ruta " +
            "LEFT JOIN asiento_estado_fecha aef ON abr.id_asignacion = aef.id_asignacion ";

    @Override
    public List<AsignacionBusRuta> listarSalidasVigentes() {
        List<AsignacionBusRuta> lista = new ArrayList<>();
        String sql = BASE_QUERY +
                "WHERE abr.fecha_salida >= CURRENT_DATE() " +
                "GROUP BY abr.id_asignacion, abr.id_bus, abr.id_ruta, abr.fecha_salida, abr.hora_salida, " +
                "b.placa, b.modelo, b.capacidad_asientos, r.origen, r.destino, r.duracion_horas " +
                "ORDER BY abr.fecha_salida ASC, abr.hora_salida ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearAsignacion(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ViajeDAOImpl] Error en listarSalidasVigentes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<AsignacionBusRuta> buscarSalidas(String origen, String destino, LocalDate fecha) {
        List<AsignacionBusRuta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_QUERY);
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (origen != null && !origen.trim().isEmpty() && !origen.equalsIgnoreCase("TODOS")) {
            sql.append("AND LOWER(r.origen) = ? ");
            params.add(origen.toLowerCase().trim());
        }
        if (destino != null && !destino.trim().isEmpty() && !destino.equalsIgnoreCase("TODOS")) {
            sql.append("AND LOWER(r.destino) = ? ");
            params.add(destino.toLowerCase().trim());
        }
        if (fecha != null) {
            sql.append("AND abr.fecha_salida = ? ");
            params.add(Date.valueOf(fecha));
        } else {
            sql.append("AND abr.fecha_salida >= CURRENT_DATE() ");
        }

        sql.append("GROUP BY abr.id_asignacion, abr.id_bus, abr.id_ruta, abr.fecha_salida, abr.hora_salida, ")
           .append("b.placa, b.modelo, b.capacidad_asientos, r.origen, r.destino, r.duracion_horas ")
           .append("ORDER BY abr.fecha_salida ASC, abr.hora_salida ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsignacion(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ViajeDAOImpl] Error en buscarSalidas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Optional<AsignacionBusRuta> buscarPorId(int idAsignacion) {
        String sql = BASE_QUERY +
                "WHERE abr.id_asignacion = ? " +
                "GROUP BY abr.id_asignacion, abr.id_bus, abr.id_ruta, abr.fecha_salida, abr.hora_salida, " +
                "b.placa, b.modelo, b.capacidad_asientos, r.origen, r.destino, r.duracion_horas";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearAsignacion(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ViajeDAOImpl] Error en buscarPorId: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Ruta> listarRutas() {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT id_ruta, origen, destino, duracion_horas FROM rutas ORDER BY origen, destino";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Ruta(
                        rs.getInt("id_ruta"),
                        rs.getString("origen"),
                        rs.getString("destino"),
                        rs.getBigDecimal("duracion_horas")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ViajeDAOImpl] Error en listarRutas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Bus> listarBuses() {
        List<Bus> lista = new ArrayList<>();
        String sql = "SELECT id_bus, placa, modelo, capacidad_asientos FROM buses ORDER BY placa";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Bus(
                        rs.getInt("id_bus"),
                        rs.getString("placa"),
                        rs.getString("modelo"),
                        rs.getInt("capacidad_asientos")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ViajeDAOImpl] Error en listarBuses: " + e.getMessage());
        }
        return lista;
    }

    private AsignacionBusRuta mapearAsignacion(ResultSet rs) throws SQLException {
        AsignacionBusRuta a = new AsignacionBusRuta();
        a.setIdAsignacion(rs.getInt("id_asignacion"));
        a.setIdBus(rs.getInt("id_bus"));
        a.setIdRuta(rs.getInt("id_ruta"));

        Date d = rs.getDate("fecha_salida");
        if (d != null) a.setFechaSalida(d.toLocalDate());

        Time t = rs.getTime("hora_salida");
        if (t != null) a.setHoraSalida(t.toLocalTime());

        a.setPlacaBus(rs.getString("placa"));
        a.setModeloBus(rs.getString("modelo"));
        a.setCapacidadTotal(rs.getInt("capacidad_asientos"));
        a.setOrigen(rs.getString("origen"));
        a.setDestino(rs.getString("destino"));
        a.setDuracionHoras(rs.getBigDecimal("duracion_horas"));
        a.setAsientosDisponibles(rs.getInt("disponibles"));

        return a;
    }
}
