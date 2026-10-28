package com.viacosta.dao;

import com.viacosta.model.AsientoEstadoFecha;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AsientoDAO {
    List<AsientoEstadoFecha> listarAsientosPorAsignacion(int idAsignacion);
    boolean actualizarEstado(int idAsiento, int idAsignacion, String nuevoEstado, Connection conn) throws SQLException;
    boolean verificarDisponibilidad(int idAsiento, int idAsignacion, Connection conn) throws SQLException;
    void asegurarAsientosParaAsignacion(int idAsignacion, int idBus);
}
