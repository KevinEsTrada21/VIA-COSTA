package com.viacosta;

/**
 * Clase lanzadora (Bootstrap Launcher) para JavaFX.
 * Permite ejecutar la aplicación directamente desde el botón "Run" (Play) de IntelliJ IDEA,
 * Eclipse o cualquier IDE moderno sin necesidad de configurar VM Options (--module-path)
 * evitando el error: "JavaFX runtime components are missing".
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
