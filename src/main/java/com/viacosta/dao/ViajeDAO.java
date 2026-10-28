package com.viacosta.dao;

import com.viacosta.model.AsignacionBusRuta;
import com.viacosta.model.Bus;
import com.viacosta.model.Ruta;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ViajeDAO {
    List<AsignacionBusRuta> listarSalidasVigentes();
    List<AsignacionBusRuta> buscarSalidas(String origen, String destino, LocalDate fecha);
    Optional<AsignacionBusRuta> buscarPorId(int idAsignacion);
    List<Ruta> listarRutas();
    List<Bus> listarBuses();
}
