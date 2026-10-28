package com.viacosta.service;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.dao.AsientoDAO;
import com.viacosta.dao.ClienteDAO;
import com.viacosta.dao.VentaDAO;
import com.viacosta.dao.impl.AsientoDAOImpl;
import com.viacosta.dao.impl.ClienteDAOImpl;
import com.viacosta.dao.impl.VentaDAOImpl;
import com.viacosta.model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaService {

    private final VentaDAO ventaDAO;
    private final AsientoDAO asientoDAO;
    private final ClienteDAO clienteDAO;

    public VentaService(VentaDAO ventaDAO, AsientoDAO asientoDAO, ClienteDAO clienteDAO) {
        this.ventaDAO = ventaDAO;
        this.asientoDAO = asientoDAO;
        this.clienteDAO = clienteDAO;
    }

    public VentaService() {
        this(new VentaDAOImpl(), new AsientoDAOImpl(), new ClienteDAOImpl());
    }

    /**
     * DTO de resultado que encapsula las entidades creadas tras una venta exitosa.
     */
    public static class ResultadoVenta {
        private final Compra compra;
        private final Comprobante comprobante;
        private final List<DetalleBoleta> detalles;
        private final Cliente cliente;
        private final AsignacionBusRuta viaje;

        public ResultadoVenta(Compra compra, Comprobante comprobante, List<DetalleBoleta> detalles, Cliente cliente, AsignacionBusRuta viaje) {
            this.compra = compra;
            this.comprobante = comprobante;
            this.detalles = detalles;
            this.cliente = cliente;
            this.viaje = viaje;
        }

        public Compra getCompra() { return compra; }
        public Comprobante getComprobante() { return comprobante; }
        public List<DetalleBoleta> getDetalles() { return detalles; }
        public Cliente getCliente() { return cliente; }
        public AsignacionBusRuta getViaje() { return viaje; }
    }

    /**
     * Procesa la venta de pasajes de forma atómica (ACID) mediante transacciones JDBC.
     * Garantiza que ningún asiento sufra doble venta o inconsistencia relacional.
     */
    public ResultadoVenta procesarVenta(Cliente cliente, Empleado empleado, AsignacionBusRuta viaje,
                                        List<AsientoEstadoFecha> asientos, String tipoComprobante,
                                        String metodoPago) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("Debe seleccionar o registrar al pasajero.");
        }
        if (empleado == null) {
            throw new IllegalArgumentException("No hay una sesión activa de empleado autorizada.");
        }
        if (viaje == null) {
            throw new IllegalArgumentException("Debe seleccionar una salida programada.");
        }
        if (asientos == null || asientos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un asiento.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción atómica

            // 1. Asegurar que el cliente esté registrado en la BD bajo la misma transacción o conexión
            if (cliente.getIdCliente() <= 0) {
                var existente = clienteDAO.buscarPorDni(cliente.getDni());
                if (existente.isPresent()) {
                    cliente.setIdCliente(existente.get().getIdCliente());
                } else {
                    Cliente reg = clienteDAO.registrar(cliente);
                    cliente.setIdCliente(reg.getIdCliente());
                }
            }

            // 2. Verificación estricta de concurrencia: ningún asiento debe haber sido ocupado
            for (AsientoEstadoFecha aef : asientos) {
                boolean disponible = asientoDAO.verificarDisponibilidad(aef.getIdAsiento(), viaje.getIdAsignacion(), conn);
                if (!disponible) {
                    throw new IllegalStateException("El asiento #" + aef.getNumeroAsiento() +
                            " ya no se encuentra disponible (fue adquirido por otra ventanilla). Seleccione otro asiento.");
                }
            }

            // 3. Calcular importe total
            BigDecimal total = BigDecimal.ZERO;
            for (AsientoEstadoFecha aef : asientos) {
                total = total.add(aef.getPrecioFinal());
            }

            // 4. Registrar cabecera de Compra
            Compra compra = new Compra();
            compra.setIdCliente(cliente.getIdCliente());
            compra.setIdEmpleado(empleado.getIdEmpleado());
            compra.setFechaCompra(LocalDateTime.now());
            compra.setTotal(total);
            compra.setMetodoPago(metodoPago != null ? metodoPago : "Efectivo");
            compra.setClienteNombre(cliente.getNombreCompleto());
            compra.setClienteDni(cliente.getDni());
            compra.setEmpleadoNombre(empleado.getNombreCompleto());

            int idCompra = ventaDAO.registrarCompra(compra, conn);

            // 5. Generar y registrar Comprobante oficial
            String numComp = ventaDAO.generarSiguienteNumeroComprobante(tipoComprobante, conn);
            Comprobante comprobante = new Comprobante();
            comprobante.setTipoComprobante(tipoComprobante);
            comprobante.setNumeroComprobante(numComp);
            comprobante.setFechaEmision(LocalDateTime.now());

            int idComprobante = ventaDAO.registrarComprobante(comprobante, conn);
            compra.setNumeroComprobante(numComp);
            compra.setTipoComprobante(tipoComprobante);

            // 6. Registrar Detalle de Boleta y actualizar estado de asientos a 'Ocupado'
            List<DetalleBoleta> detalles = new ArrayList<>();
            for (AsientoEstadoFecha aef : asientos) {
                DetalleBoleta det = new DetalleBoleta();
                det.setIdCompra(idCompra);
                det.setIdAsiento(aef.getIdAsiento());
                det.setIdAsignacion(viaje.getIdAsignacion());
                det.setIdComprobante(idComprobante);
                det.setFechaViaje(viaje.getFechaSalida());
                det.setHoraViaje(viaje.getHoraSalida());
                det.setPrecioUnitario(aef.getPrecioFinal());
                det.setNumeroAsiento(aef.getNumeroAsiento());
                det.setTipoAsiento(aef.getNombreTipo());
                det.setOrigen(viaje.getOrigen());
                det.setDestino(viaje.getDestino());
                det.setPlacaBus(viaje.getPlacaBus());
                det.setNumeroComprobante(numComp);

                ventaDAO.registrarDetalleBoleta(det, conn);
                detalles.add(det);

                // Actualizar estado en asiento_estado_fecha a 'Ocupado'
                asientoDAO.actualizarEstado(aef.getIdAsiento(), viaje.getIdAsignacion(), "Ocupado", conn);
            }

            // 7. Commit atómico
            conn.commit();
            System.out.println("[VentaService] Venta registrada con éxito. Comprobante: " + numComp + ", Total: " + total);

            return new ResultadoVenta(compra, comprobante, detalles, cliente, viaje);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[VentaService] Transacción cancelada (Rollback ejecutado): " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("[VentaService] Error en rollback: " + rollbackEx.getMessage());
                }
            }
            if (e instanceof SQLException) throw (SQLException) e;
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public List<Compra> listarHistorialVentas() {
        return ventaDAO.listarComprasRecientes();
    }

    public List<DetalleBoleta> listarDetallesVenta(int idCompra) {
        return ventaDAO.listarDetallesPorCompra(idCompra);
    }
}
