package com.viacosta.util;

import com.viacosta.model.Empleado;

public class SessionManager {
    private static Empleado usuarioActual;

    public static void setUsuarioActual(Empleado empleado) {
        usuarioActual = empleado;
    }

    public static Empleado getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public static boolean esAdministrador() {
        return usuarioActual != null && "ADMINISTRADOR".equalsIgnoreCase(usuarioActual.getNombreRol());
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
