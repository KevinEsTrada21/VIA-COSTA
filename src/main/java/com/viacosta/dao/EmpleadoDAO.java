package com.viacosta.dao;

import com.viacosta.model.Empleado;
import java.util.Optional;

public interface EmpleadoDAO {
    Optional<Empleado> autenticar(String usuario, String claveHash);
    Optional<Empleado> buscarPorId(int idEmpleado);
}
