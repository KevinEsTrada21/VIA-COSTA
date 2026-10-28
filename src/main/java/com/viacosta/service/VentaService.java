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

    public ResultadoVenta procesarVenta(Cliente cliente, Empleado empleado, AsignacionBusRuta viaje,
                                        List<AsientoEstadoFecha> asientos, String tipoComprobante,
                                        String metodoPago) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("Debe ingresar los datos del pasajero.");
        }
        if (empleado == null) {
            empleado = new Empleado();
            empleado.setIdEmpleado(1);
            empleado.setNombre("Carlos");
            empleado.setApellido("Valdiviezo");
        }
        if (viaje == null) {
            throw new IllegalArgumentException("Debe seleccionar un horario de salida.");
        }
        if (asientos == null || asientos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un asiento.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (cliente.getIdCliente() <= 0) {
                var existente = clienteDAO.buscarPorDni(cliente.getDni());
                if (existente.isPresent()) {
                    cliente.setIdCliente(existente.get().getIdCliente());
                } else {
                    Cliente reg = clienteDAO.registrar(cliente);
                    cliente.setIdCliente(reg.getIdCliente());
                }
            }

            for (AsientoEstadoFecha aef : asientos) {
                boolean disponible = asientoDAO.verificarDisponibilidad(aef.getIdAsiento(), viaje.getIdAsignacion(), conn);
                if (!disponible) {
                    throw new IllegalStateException("El asiento #" + aef.getNumeroAsiento() +
                            " ya fue ocupado. Por favor elija otro asiento.");
                }
            }

            BigDecimal total = BigDecimal.ZERO;
            for (AsientoEstadoFecha aef : asientos) {
                total = total.add(aef.getPrecioFinal());
            }

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

            String numComp = ventaDAO.generarSiguienteNumeroComprobante(tipoComprobante, conn);
            Comprobante comprobante = new Comprobante();
            comprobante.setTipoComprobante(tipoComprobante);
            comprobante.setNumeroComprobante(numComp);
            comprobante.setFechaEmision(LocalDateTime.now());

            int idComprobante = ventaDAO.registrarComprobante(comprobante, conn);
            compra.setNumeroComprobante(numComp);
            compra.setTipoComprobante(tipoComprobante);

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

                asientoDAO.actualizarEstado(aef.getIdAsiento(), viaje.getIdAsignacion(), "Ocupado", conn);
            }

            conn.commit();
            return new ResultadoVenta(compra, comprobante, detalles, cliente, viaje);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
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
