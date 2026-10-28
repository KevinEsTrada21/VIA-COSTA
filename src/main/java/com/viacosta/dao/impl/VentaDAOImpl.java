package com.viacosta.dao.impl;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.dao.VentaDAO;
import com.viacosta.model.Compra;
import com.viacosta.model.Comprobante;
import com.viacosta.model.DetalleBoleta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public int registrarCompra(Compra compra, Connection conn) throws SQLException {
        String sql = "INSERT INTO compras (id_cliente, id_empleado, fecha_compra, total, metodo_pago) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, compra.getIdCliente());
            ps.setInt(2, compra.getIdEmpleado());
            ps.setBigDecimal(3, compra.getTotal());
            ps.setString(4, compra.getMetodoPago());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    compra.setIdCompra(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID autogenerado para la compra.");
    }

    @Override
    public int registrarComprobante(Comprobante comprobante, Connection conn) throws SQLException {
        String sql = "INSERT INTO comprobantes (tipo_comprobante, numero_comprobante, fecha_emision) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, comprobante.getTipoComprobante());
            ps.setString(2, comprobante.getNumeroComprobante());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    comprobante.setIdComprobante(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID autogenerado para el comprobante.");
    }

    @Override
    public int registrarDetalleBoleta(DetalleBoleta detalle, Connection conn) throws SQLException {
        String sql = "INSERT INTO detalle_boleta (id_compra, id_asiento, id_asignacion, id_comprobante, fecha_viaje, hora_viaje, precio_unitario) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detalle.getIdCompra());
            ps.setInt(2, detalle.getIdAsiento());
            ps.setInt(3, detalle.getIdAsignacion());
            ps.setInt(4, detalle.getIdComprobante());
            ps.setDate(5, Date.valueOf(detalle.getFechaViaje()));
            ps.setTime(6, Time.valueOf(detalle.getHoraViaje()));
            ps.setBigDecimal(7, detalle.getPrecioUnitario());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    detalle.setIdDetalle(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID autogenerado para el detalle de boleta.");
    }

    @Override
    public String generarSiguienteNumeroComprobante(String tipoComprobante, Connection conn) throws SQLException {
        String prefijo = "Boleta".equalsIgnoreCase(tipoComprobante) ? "B001-" : "F001-";
        String sql = "SELECT numero_comprobante FROM comprobantes WHERE numero_comprobante LIKE ? ORDER BY id_comprobante DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefijo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ultimo = rs.getString("numero_comprobante");
                    try {
                        String correlativoStr = ultimo.substring(prefijo.length());
                        long correlativo = Long.parseLong(correlativoStr);
                        return prefijo + String.format("%06d", correlativo + 1);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return prefijo + "000001";
    }

    @Override
    public List<Compra> listarComprasRecientes() {
        List<Compra> lista = new ArrayList<>();
        String sql =
                "SELECT c.id_compra, c.id_cliente, c.id_empleado, c.fecha_compra, c.total, c.metodo_pago, " +
                "CONCAT(cl.nombre, ' ', cl.apellido) AS cliente_nombre, cl.dni AS cliente_dni, " +
                "CONCAT(e.nombre, ' ', e.apellido) AS empleado_nombre, " +
                "cmp.numero_comprobante, cmp.tipo_comprobante " +
                "FROM compras c " +
                "INNER JOIN clientes cl ON c.id_cliente = cl.id_cliente " +
                "INNER JOIN empleados e ON c.id_empleado = e.id_empleado " +
                "LEFT JOIN detalle_boleta db ON c.id_compra = db.id_compra " +
                "LEFT JOIN comprobantes cmp ON db.id_comprobante = cmp.id_comprobante " +
                "GROUP BY c.id_compra, c.id_cliente, c.id_empleado, c.fecha_compra, c.total, c.metodo_pago, " +
                "cl.nombre, cl.apellido, cl.dni, e.nombre, e.apellido, cmp.numero_comprobante, cmp.tipo_comprobante " +
                "ORDER BY c.id_compra DESC LIMIT 50";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Compra c = new Compra();
                c.setIdCompra(rs.getInt("id_compra"));
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setIdEmpleado(rs.getInt("id_empleado"));

                Timestamp ts = rs.getTimestamp("fecha_compra");
                if (ts != null) c.setFechaCompra(ts.toLocalDateTime());

                c.setTotal(rs.getBigDecimal("total"));
                c.setMetodoPago(rs.getString("metodo_pago"));
                c.setClienteNombre(rs.getString("cliente_nombre"));
                c.setClienteDni(rs.getString("cliente_dni"));
                c.setEmpleadoNombre(rs.getString("empleado_nombre"));
                c.setNumeroComprobante(rs.getString("numero_comprobante"));
                c.setTipoComprobante(rs.getString("tipo_comprobante"));

                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("[VentaDAOImpl] Error en listarComprasRecientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<DetalleBoleta> listarDetallesPorCompra(int idCompra) {
        List<DetalleBoleta> lista = new ArrayList<>();
        String sql =
                "SELECT db.id_detalle, db.id_compra, db.id_asiento, db.id_asignacion, db.id_comprobante, " +
                "db.fecha_viaje, db.hora_viaje, db.precio_unitario, " +
                "a.numero_asiento, ta.nombre AS tipo_asiento, " +
                "r.origen, r.destino, b.placa, cmp.numero_comprobante " +
                "FROM detalle_boleta db " +
                "INNER JOIN asientos a ON db.id_asiento = a.id_asiento " +
                "INNER JOIN tipo_asiento ta ON a.id_tipo_asiento = ta.id_tipo_asiento " +
                "INNER JOIN asignacion_buses_rutas abr ON db.id_asignacion = abr.id_asignacion " +
                "INNER JOIN rutas r ON abr.id_ruta = r.id_ruta " +
                "INNER JOIN buses b ON abr.id_bus = b.id_bus " +
                "INNER JOIN comprobantes cmp ON db.id_comprobante = cmp.id_comprobante " +
                "WHERE db.id_compra = ? " +
                "ORDER BY db.id_detalle ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleBoleta d = new DetalleBoleta();
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setIdCompra(rs.getInt("id_compra"));
                    d.setIdAsiento(rs.getInt("id_asiento"));
                    d.setIdAsignacion(rs.getInt("id_asignacion"));
                    d.setIdComprobante(rs.getInt("id_comprobante"));

                    Date dt = rs.getDate("fecha_viaje");
                    if (dt != null) d.setFechaViaje(dt.toLocalDate());

                    Time tm = rs.getTime("hora_viaje");
                    if (tm != null) d.setHoraViaje(tm.toLocalTime());

                    d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    d.setNumeroAsiento(rs.getInt("numero_asiento"));
                    d.setTipoAsiento(rs.getString("tipo_asiento"));
                    d.setOrigen(rs.getString("origen"));
                    d.setDestino(rs.getString("destino"));
                    d.setPlacaBus(rs.getString("placa"));
                    d.setNumeroComprobante(rs.getString("numero_comprobante"));

                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("[VentaDAOImpl] Error en listarDetallesPorCompra: " + e.getMessage());
        }
        return lista;
    }
}
