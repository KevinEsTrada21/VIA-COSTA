package com.viacosta.service;

import com.viacosta.dao.AsientoDAO;
import com.viacosta.dao.ViajeDAO;
import com.viacosta.dao.impl.AsientoDAOImpl;
import com.viacosta.dao.impl.ViajeDAOImpl;
import com.viacosta.model.AsignacionBusRuta;
import com.viacosta.model.AsientoEstadoFecha;
import com.viacosta.model.Bus;
import com.viacosta.model.Ruta;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ViajeService {

    private final ViajeDAO viajeDAO;
    private final AsientoDAO asientoDAO;

    public ViajeService(ViajeDAO viajeDAO, AsientoDAO asientoDAO) {
        this.viajeDAO = viajeDAO;
        this.asientoDAO = asientoDAO;
    }

    public ViajeService() {
        this(new ViajeDAOImpl(), new AsientoDAOImpl());
    }

    public List<AsignacionBusRuta> listarSalidasVigentes() {
        return viajeDAO.listarSalidasVigentes();
    }

    public List<AsignacionBusRuta> buscarSalidas(String origen, String destino, LocalDate fecha) {
        return viajeDAO.buscarSalidas(origen, destino, fecha);
    }

    public Optional<AsignacionBusRuta> obtenerAsignacion(int idAsignacion) {
        return viajeDAO.buscarPorId(idAsignacion);
    }

    public List<AsientoEstadoFecha> obtenerAsientosParaViaje(int idAsignacion, int idBus) {
        // Asegura que todos los asientos del bus estén mapeados para esta asignación en BD
        asientoDAO.asegurarAsientosParaAsignacion(idAsignacion, idBus);
        return asientoDAO.listarAsientosPorAsignacion(idAsignacion);
    }

    public List<Ruta> listarRutas() {
        return viajeDAO.listarRutas();
    }

    public List<Bus> listarBuses() {
        return viajeDAO.listarBuses();
    }
}
