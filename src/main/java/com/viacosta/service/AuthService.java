package com.viacosta.service;

import com.viacosta.dao.EmpleadoDAO;
import com.viacosta.dao.impl.EmpleadoDAOImpl;
import com.viacosta.model.Empleado;
import com.viacosta.util.PasswordHasher;
import com.viacosta.util.SessionManager;

import java.util.Optional;

public class AuthService {

    private final EmpleadoDAO empleadoDAO;

    public AuthService(EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }

    public AuthService() {
        this(new EmpleadoDAOImpl());
    }

    public Optional<Empleado> autenticar(String usuario, String contrasenaPlana) {
        if (usuario == null || contrasenaPlana == null || usuario.trim().isEmpty() || contrasenaPlana.isEmpty()) {
            return Optional.empty();
        }

        String hash = PasswordHasher.hash(contrasenaPlana);
        Optional<Empleado> optEmp = empleadoDAO.autenticar(usuario, hash);
        optEmp.ifPresent(SessionManager::setUsuarioActual);
        return optEmp;
    }

    public void cerrarSesion() {
        SessionManager.cerrarSesion();
    }
}
