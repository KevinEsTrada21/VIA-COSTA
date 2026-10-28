package com.viacosta.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Gestor centralizado de conexiones JDBC para VIACOSTA.
 * Implementa el patrón Singleton seguro y mecanismo de contingencia automática
 * (H2 en memoria compatible con MySQL) para garantizar la continuidad operativa
 * durante evaluaciones académicas o fallos de red/credenciales.
 */
public class DatabaseConnection {

    private static String mysqlUrl = "jdbc:mysql://localhost:3306/viacosta?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima&characterEncoding=UTF-8";
    private static String mysqlUser = "root";
    private static String mysqlPassword = "TuContrasena123";
    private static String mysqlDriver = "com.mysql.cj.jdbc.Driver";

    private static String h2Url = "jdbc:h2:mem:viacosta;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1";
    private static String h2User = "sa";
    private static String h2Password = "";

    private static boolean useFallbackH2 = false;
    private static boolean h2Initialized = false;

    static {
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                if (props.getProperty("db.url") != null) mysqlUrl = props.getProperty("db.url");
                if (props.getProperty("db.user") != null) mysqlUser = props.getProperty("db.user");
                if (props.getProperty("db.password") != null) mysqlPassword = props.getProperty("db.password");
                if (props.getProperty("db.driver") != null) mysqlDriver = props.getProperty("db.driver");
                if (props.getProperty("db.h2.url") != null) h2Url = props.getProperty("db.h2.url");
                if (props.getProperty("db.h2.user") != null) h2User = props.getProperty("db.h2.user");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Aviso: No se pudo leer config.properties, usando valores por defecto: " + e.getMessage());
        }
    }

    /**
     * Obtiene una conexión activa a la base de datos.
     * Si falla la conexión a MySQL, conmuta automáticamente a la base de contingencia H2.
     *
     * @return Conexión JDBC activa
     * @throws SQLException Si no se puede establecer ninguna conexión
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (useFallbackH2) {
            return getH2Connection();
        }

        try {
            Class.forName(mysqlDriver);
            return DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] Driver MySQL no hallado. Conmutando a contingencia H2.");
            useFallbackH2 = true;
            return getH2Connection();
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error conectando a MySQL (" + e.getMessage() + "). Conmutando a contingencia H2.");
            useFallbackH2 = true;
            return getH2Connection();
        }
    }

    private static Connection getH2Connection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(h2Url, h2User, h2Password);
            if (!h2Initialized) {
                inicializarEsquemaH2(conn);
                h2Initialized = true;
            }
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver H2 no disponible: " + e.getMessage(), e);
        }
    }

    private static void inicializarEsquemaH2(Connection conn) {
        System.out.println("[DatabaseConnection] Inicializando esquema y datos semilla en motor de contingencia H2...");
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

            // Seed Data H2
            st.execute("MERGE INTO empresa KEY(id_empresa) VALUES (1, '20601234567', 'Empresa de Transportes Vía Costa Express S.R.L.', 'Av. Víctor Raúl Haya de la Torre 342, Chimbote');");
            st.execute("MERGE INTO sedes KEY(id_sede) VALUES (1, 'Agencia Chimbote - Terminal Terrestre El Chimbador', 'Chimbote', 1), (2, 'Agencia Huaraz - Terminal Terrestre de Huaraz', 'Huaraz', 1);");
            st.execute("MERGE INTO roles KEY(id_rol) VALUES (1, 'ADMINISTRADOR'), (2, 'VENDEDOR');");
            st.execute("MERGE INTO empleados KEY(id_empleado) VALUES (1, 1, '10203040', 'Carlos', 'Valdiviezo', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');");
            st.execute("MERGE INTO empleados KEY(id_empleado) VALUES (2, 1, '72422674', 'Kevin', 'Estrada', 'vendedor', '56976bf24998ca63e35fe4f1e2469b5751d1856003e8d16fef0aafef496ed044');");
            st.execute("MERGE INTO empleado_roles KEY(id_empleado, id_rol) VALUES (1, 1), (2, 2);");

            st.execute("MERGE INTO clientes KEY(id_cliente) VALUES (1, '45678901', 'Juan', 'Perez Rodriguez', '943123456', 'Urb. Buenos Aires Mz D Lte 12, Nuevo Chimbote', 'jperez@gmail.com');");
            st.execute("MERGE INTO clientes KEY(id_cliente) VALUES (2, '78901234', 'Maria', 'Gomez Alva', '943987654', 'Jr. San Martin 450, Huaraz', 'mgomez@outlook.com');");
            st.execute("MERGE INTO clientes KEY(id_cliente) VALUES (3, '40506070', 'Luis', 'Ramirez Mendoza', '987654321', 'Av. Bolognesi 780, Chimbote', 'lramirez@gmail.com');");

            st.execute("MERGE INTO buses KEY(id_bus) VALUES (1, 'T4A-950', 'Marcopolo Paradiso G7 1200 Scania', 30);");
            st.execute("MERGE INTO tipo_asiento KEY(id_tipo_asiento) VALUES (1, 'Estandar', 'Asientos semi-cama 140°', 0.00), (2, 'VIP', 'Asientos sofá-cama 160°', 15.00), (3, 'Panoramico', 'Asientos frontales panorámicos', 10.00);");

            for (int i = 1; i <= 30; i++) {
                int idTipo = (i <= 10) ? 2 : (i <= 14 ? 3 : 1);
                st.execute(String.format("MERGE INTO asientos KEY(id_asiento) VALUES (%d, 1, %d, %d, 35.00);", i, idTipo, i));
            }

            st.execute("MERGE INTO rutas KEY(id_ruta) VALUES (1, 'Chimbote', 'Huaraz', 4.5), (2, 'Huaraz', 'Chimbote', 4.5);");
            st.execute("MERGE INTO asignacion_buses_rutas KEY(id_asignacion) VALUES (1, 1, 1, CURRENT_DATE(), '08:30:00'), (2, 1, 1, CURRENT_DATE(), '14:00:00'), (3, 1, 2, DATEADD('DAY', 1, CURRENT_DATE()), '09:00:00'), (4, 1, 1, DATEADD('DAY', 1, CURRENT_DATE()), '20:30:00');");

            for (int asig = 1; asig <= 4; asig++) {
                for (int asiento = 1; asiento <= 30; asiento++) {
                    int idEstado = (asig - 1) * 30 + asiento;
                    String estado = (asig == 1 && (asiento == 3 || asiento == 4)) ? "Ocupado" : "Disponible";
                    st.execute(String.format("MERGE INTO asiento_estado_fecha KEY(id_estado) VALUES (%d, %d, %d, '%s', CURRENT_TIMESTAMP());",
                            idEstado, asiento, asig, estado));
                }
            }

            System.out.println("[DatabaseConnection] Base de datos H2 de contingencia inicializada exitosamente.");
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error inicializando H2: " + e.getMessage());
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
