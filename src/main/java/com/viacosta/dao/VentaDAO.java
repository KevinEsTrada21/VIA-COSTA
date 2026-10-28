package com.viacosta.dao;

import com.viacosta.model.Compra;
import com.viacosta.model.Comprobante;
import com.viacosta.model.DetalleBoleta;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface VentaDAO {
    int registrarCompra(Compra compra, Connection conn) throws SQLException;
    int registrarComprobante(Comprobante comprobante, Connection conn) throws SQLException;
    int registrarDetalleBoleta(DetalleBoleta detalle, Connection conn) throws SQLException;
    String generarSiguienteNumeroComprobante(String tipoComprobante, Connection conn) throws SQLException;
    List<Compra> listarComprasRecientes();
    List<DetalleBoleta> listarDetallesPorCompra(int idCompra);
}
