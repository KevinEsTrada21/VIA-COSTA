package com.viacosta.config;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Gestor de conexion a base de datos MySQL con respaldo persistente en disco.
 */
public class DatabaseConnection {

    private static String mysqlServerUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima&characterEncoding=UTF-8";
    private static String mysqlUrl = "jdbc:mysql://localhost:3306/viacosta?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima&characterEncoding=UTF-8";
    private static String mysqlUser = "root";
    private static String mysqlPassword = "root";
    private static String mysqlDriver = "com.mysql.cj.jdbc.Driver";

    private static String h2Url = "jdbc:h2:./data/viacosta_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;AUTO_SERVER=TRUE";
    private static String h2User = "sa";
    private static String h2Password = "";

    private static boolean useFallbackH2 = false;
    private static boolean mysqlInitialized = false;
    private static boolean h2Initialized = false;
    private static boolean passwordPromptShown = false;

    static {
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                if (props.getProperty("db.url") != null) mysqlUrl = props.getProperty("db.url").trim();
                if (props.getProperty("db.user") != null) mysqlUser = props.getProperty("db.user").trim();
                if (props.getProperty("db.password") != null) mysqlPassword = props.getProperty("db.password").trim();
                if (props.getProperty("db.driver") != null) mysqlDriver = props.getProperty("db.driver").trim();
                if (props.getProperty("db.h2.url") != null) h2Url = props.getProperty("db.h2.url").trim();
                if (props.getProperty("db.h2.user") != null) h2User = props.getProperty("db.h2.user").trim();
            }
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] No se pudo leer config.properties: " + e.getMessage());
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (useFallbackH2) {
            return getH2Connection();
        }

        try {
            Class.forName(mysqlDriver);
            if (!mysqlInitialized) {
                if (inicializarMySQL()) {
                    mysqlInitialized = true;
                } else {
                    useFallbackH2 = true;
                    return getH2Connection();
                }
            }
            return DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Usando base de datos local en disco: " + e.getMessage());
            useFallbackH2 = true;
            return getH2Connection();
        }
    }

    private static boolean inicializarMySQL() {
        DriverManager.setLoginTimeout(2);
        Set<String> candidatos = new LinkedHashSet<>();
        candidatos.add(mysqlPassword);
        candidatos.add("");
        candidatos.add("root");
        candidatos.add("1234");
        candidatos.add("123456");
        candidatos.add("admin");
        candidatos.add("12345");
        candidatos.add("mysql");

        for (String pass : candidatos) {
            if (probarYPrepararMySQL(pass)) {
                if (!pass.equals(mysqlPassword)) {
                    mysqlPassword = pass;
                    guardarPasswordEnConfig(pass);
                }
                return true;
            }
        }

        if (!passwordPromptShown && !GraphicsEnvironment.isHeadless() && !esEjecucionDeTest()) {
            passwordPromptShown = true;
            while (true) {
                JPasswordField pf = new JPasswordField();
                int ok = JOptionPane.showConfirmDialog(
                        null,
                        new Object[]{
                                "Para conectar con tu MySQL Workbench, ingresa la contraseña de tu usuario '" + mysqlUser + "':\n" +
                                "(Si presionas Cancelar, los datos igual se guardarán en la carpeta local 'data')",
                                pf
                        },
                        "Conectar a MySQL",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (ok != JOptionPane.OK_OPTION) {
                    break;
                }
                String ingresada = new String(pf.getPassword()).trim();
                if (probarYPrepararMySQL(ingresada)) {
                    mysqlPassword = ingresada;
                    guardarPasswordEnConfig(ingresada);
                    JOptionPane.showMessageDialog(
                            null,
                            "Conectado a MySQL correctamente. La base de datos 'viacosta' está lista.",
                            "MySQL Conectado",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return true;
                } else {
                    int retry = JOptionPane.showConfirmDialog(
                            null,
                            "Contraseña de MySQL incorrecta para el usuario '" + mysqlUser + "'. ¿Deseas intentar de nuevo?",
                            "Error de contraseña MySQL",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (retry != JOptionPane.YES_OPTION) {
                        break;
                    }
                }
            }
        }
        return false;
    }

    private static boolean probarYPrepararMySQL(String password) {
        try (Connection serverConn = DriverManager.getConnection(mysqlServerUrl, mysqlUser, password);
             Statement st = serverConn.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS viacosta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        } catch (SQLException e) {
            return false;
        }

        try (Connection dbConn = DriverManager.getConnection(mysqlUrl, mysqlUser, password)) {
            inicializarEsquemaYDatosSiVacio(dbConn);
            System.out.println("[DatabaseConnection] Conectado exitosamente a MySQL (base de datos: viacosta).");
            return true;
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error preparando esquema en MySQL: " + e.getMessage());
            return false;
        }
    }

    private static void guardarPasswordEnConfig(String nuevaPassword) {
        String[] rutas = {
                "src/main/resources/config.properties",
                "target/classes/config.properties"
        };
        for (String ruta : rutas) {
            try {
                File f = new File(ruta);
                if (f.exists()) {
                    String contenido = Files.readString(f.toPath(), StandardCharsets.UTF_8);
                    contenido = contenido.replaceAll("(?m)^db\\.password=.*$", "db.password=" + nuevaPassword);
                    try (FileOutputStream fos = new FileOutputStream(f, false)) {
                        fos.write(contenido.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static boolean esEjecucionDeTest() {
        for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
            if (el.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }

    private static Connection getH2Connection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(h2Url, h2User, h2Password);
            if (!h2Initialized) {
                inicializarEsquemaYDatosSiVacio(conn);
                h2Initialized = true;
            }
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver H2 no disponible: " + e.getMessage(), e);
        }
    }

    private static void inicializarEsquemaYDatosSiVacio(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS empresa (" +
                    "id_empresa INT AUTO_INCREMENT PRIMARY KEY, " +
                    "ruc CHAR(11) NOT NULL UNIQUE, " +
                    "razon_social VARCHAR(150) NOT NULL, " +
                    "direccion VARCHAR(200) NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS sedes (" +
                    "id_sede INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre_sede VARCHAR(100) NOT NULL, " +
                    "ciudad VARCHAR(100) NOT NULL, " +
                    "id_empresa INT NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS roles (" +
                    "id_rol INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre_rol VARCHAR(50) NOT NULL UNIQUE);");

            st.execute("CREATE TABLE IF NOT EXISTS empleados (" +
                    "id_empleado INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_sede INT NOT NULL, " +
                    "dni CHAR(8) NOT NULL UNIQUE, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "apellido VARCHAR(100) NOT NULL, " +
                    "usuario VARCHAR(50) NOT NULL UNIQUE, " +
                    "clave_hash VARCHAR(255) NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS empleado_roles (" +
                    "id_empleado INT NOT NULL, " +
                    "id_rol INT NOT NULL, " +
                    "PRIMARY KEY (id_empleado, id_rol));");

            st.execute("CREATE TABLE IF NOT EXISTS clientes (" +
                    "id_cliente INT AUTO_INCREMENT PRIMARY KEY, " +
                    "dni CHAR(8) NOT NULL UNIQUE, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "apellido VARCHAR(100) NOT NULL, " +
                    "telefono VARCHAR(15) NULL, " +
                    "direccion VARCHAR(200) NULL, " +
                    "correo VARCHAR(100) NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS buses (" +
                    "id_bus INT AUTO_INCREMENT PRIMARY KEY, " +
                    "placa VARCHAR(10) NOT NULL UNIQUE, " +
                    "modelo VARCHAR(100) NOT NULL, " +
                    "capacidad_asientos INT NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS tipo_asiento (" +
                    "id_tipo_asiento INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(50) NOT NULL UNIQUE, " +
                    "descripcion VARCHAR(200) NULL, " +
                    "cargo_adicional DECIMAL(10,2) NOT NULL DEFAULT 0.00);");

            st.execute("CREATE TABLE IF NOT EXISTS asientos (" +
                    "id_asiento INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_bus INT NOT NULL, " +
                    "id_tipo_asiento INT NOT NULL, " +
                    "numero_asiento INT NOT NULL, " +
                    "precio_base DECIMAL(10,2) NOT NULL DEFAULT 35.00);");

            st.execute("CREATE TABLE IF NOT EXISTS rutas (" +
                    "id_ruta INT AUTO_INCREMENT PRIMARY KEY, " +
                    "origen VARCHAR(100) NOT NULL, " +
                    "destino VARCHAR(100) NOT NULL, " +
                    "duracion_horas DECIMAL(4,1) NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS asignacion_buses_rutas (" +
                    "id_asignacion INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_bus INT NOT NULL, " +
                    "id_ruta INT NOT NULL, " +
                    "fecha_salida DATE NOT NULL, " +
                    "hora_salida TIME NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS asiento_estado_fecha (" +
                    "id_estado INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_asiento INT NOT NULL, " +
                    "id_asignacion INT NOT NULL, " +
                    "estado VARCHAR(20) NOT NULL DEFAULT 'Disponible', " +
                    "fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            st.execute("CREATE TABLE IF NOT EXISTS comprobantes (" +
                    "id_comprobante INT AUTO_INCREMENT PRIMARY KEY, " +
                    "tipo_comprobante VARCHAR(20) NOT NULL, " +
                    "numero_comprobante VARCHAR(20) NOT NULL UNIQUE, " +
                    "fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

            st.execute("CREATE TABLE IF NOT EXISTS compras (" +
                    "id_compra INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_cliente INT NOT NULL, " +
                    "id_empleado INT NOT NULL, " +
                    "fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "total DECIMAL(10,2) NOT NULL, " +
                    "metodo_pago VARCHAR(50) NOT NULL DEFAULT 'Efectivo');");

            st.execute("CREATE TABLE IF NOT EXISTS detalle_boleta (" +
                    "id_detalle INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_compra INT NOT NULL, " +
                    "id_asiento INT NOT NULL, " +
                    "id_asignacion INT NOT NULL, " +
                    "id_comprobante INT NOT NULL, " +
                    "fecha_viaje DATE NOT NULL, " +
                    "hora_viaje TIME NOT NULL, " +
                    "precio_unitario DECIMAL(10,2) NOT NULL);");

            st.execute("CREATE TABLE IF NOT EXISTS detalle_encomienda (" +
                    "id_detalle_encomienda INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_compra INT NOT NULL, " +
                    "id_asignacion INT NOT NULL, " +
                    "id_comprobante INT NOT NULL, " +
                    "destinatario_nombre VARCHAR(100) NOT NULL, " +
                    "destinatario_dni CHAR(8) NOT NULL, " +
                    "descripcion VARCHAR(255) NOT NULL, " +
                    "peso_kg DECIMAL(6,2) NOT NULL, " +
                    "precio DECIMAL(10,2) NOT NULL);");

            // Verificar si ya existen datos para NO sobrescribir las ventas realizadas
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM empresa")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return;
                }
            }

            System.out.println("[DatabaseConnection] Insertando datos iniciales por primera vez...");

            st.execute("INSERT INTO empresa (id_empresa, ruc, razon_social, direccion) VALUES " +
                    "(1, '20601234567', 'Empresa de Transportes Vía Costa Express S.R.L.', 'Av. Víctor Raúl Haya de la Torre 342, Chimbote');");

            st.execute("INSERT INTO sedes (id_sede, nombre_sede, ciudad, id_empresa) VALUES " +
                    "(1, 'Agencia Chimbote - Terminal Terrestre El Chimbador', 'Chimbote', 1), " +
                    "(2, 'Agencia Huaraz - Terminal Terrestre de Huaraz', 'Huaraz', 1);");

            st.execute("INSERT INTO roles (id_rol, nombre_rol) VALUES (1, 'ADMINISTRADOR'), (2, 'VENDEDOR');");

            st.execute("INSERT INTO empleados (id_empleado, id_sede, dni, nombre, apellido, usuario, clave_hash) VALUES " +
                    "(1, 1, '10203040', 'Carlos', 'Valdiviezo', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'), " +
                    "(2, 1, '72422674', 'Kevin', 'Estrada', 'vendedor', '56976bf24998ca63e35fe4f1e2469b5751d1856003e8d16fef0aafef496ed044');");

            st.execute("INSERT INTO empleado_roles (id_empleado, id_rol) VALUES (1, 1), (2, 2);");

            st.execute("INSERT INTO clientes (id_cliente, dni, nombre, apellido, telefono, direccion, correo) VALUES " +
                    "(1, '45678901', 'Juan', 'Perez Rodriguez', '943123456', 'Urb. Buenos Aires Mz D Lte 12, Nuevo Chimbote', 'jperez@gmail.com'), " +
                    "(2, '78901234', 'Maria', 'Gomez Alva', '943987654', 'Jr. San Martin 450, Huaraz', 'mgomez@outlook.com'), " +
                    "(3, '40506070', 'Luis', 'Ramirez Mendoza', '987654321', 'Av. Bolognesi 780, Chimbote', 'lramirez@gmail.com');");

            st.execute("INSERT INTO buses (id_bus, placa, modelo, capacidad_asientos) VALUES " +
                    "(1, 'T4A-950', 'Marcopolo Paradiso G7 1200 Scania', 30);");

            st.execute("INSERT INTO tipo_asiento (id_tipo_asiento, nombre, descripcion, cargo_adicional) VALUES " +
                    "(1, 'Estandar', 'Asientos semi-cama 140°', 0.00), " +
                    "(2, 'VIP', 'Asientos sofá-cama 160°', 15.00), " +
                    "(3, 'Panoramico', 'Asientos frontales panorámicos', 10.00);");

            for (int i = 1; i <= 30; i++) {
                int idTipo = (i <= 10) ? 2 : (i <= 14 ? 3 : 1);
                st.execute(String.format("INSERT INTO asientos (id_asiento, id_bus, id_tipo_asiento, numero_asiento, precio_base) VALUES (%d, 1, %d, %d, 35.00);", i, idTipo, i));
            }

            st.execute("INSERT INTO rutas (id_ruta, origen, destino, duracion_horas) VALUES " +
                    "(1, 'Chimbote', 'Huaraz', 4.5), " +
                    "(2, 'Huaraz', 'Chimbote', 4.5);");

            String hoy = LocalDate.now().toString();
            String manana = LocalDate.now().plusDays(1).toString();
            st.execute(String.format("INSERT INTO asignacion_buses_rutas (id_asignacion, id_bus, id_ruta, fecha_salida, hora_salida) VALUES " +
                    "(1, 1, 1, '%s', '08:30:00'), " +
                    "(2, 1, 1, '%s', '14:00:00'), " +
                    "(3, 1, 2, '%s', '09:00:00'), " +
                    "(4, 1, 1, '%s', '20:30:00');", hoy, hoy, manana, manana));

            for (int asig = 1; asig <= 4; asig++) {
                for (int asiento = 1; asiento <= 30; asiento++) {
                    int idEstado = (asig - 1) * 30 + asiento;
                    String estado = (asig == 1 && (asiento == 3 || asiento == 4)) ? "Ocupado" : "Disponible";
                    st.execute(String.format("INSERT INTO asiento_estado_fecha (id_estado, id_asiento, id_asignacion, estado) VALUES (%d, %d, %d, '%s');",
                            idEstado, asiento, asig, estado));
                }
            }

            System.out.println("[DatabaseConnection] Datos iniciales cargados correctamente.");
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error inicializando esquema: " + e.getMessage());
        }
    }

    public static boolean isH2Fallback() {
        return useFallbackH2;
    }

    public static void setForceH2(boolean force) {
        useFallbackH2 = force;
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables == null) return;
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
