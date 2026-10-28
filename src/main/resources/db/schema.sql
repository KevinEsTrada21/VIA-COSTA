-- ==============================================================================
-- SISTEMA DE ESCRITORIO "VIACOSTA" - VENTA DE PASAJES Y TRANSPORTE
-- Curso Integrador I: Sistemas Software (Avance 3 - UTP)
-- Base de Datos: viacosta | Motor: MySQL 8.0+ (Cumple 3FN y Modelo Documentado)
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS viacosta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE viacosta;

-- Desactivar verificaciones foráneas temporalmente para reconstrucción limpia
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS detalle_encomienda;
DROP TABLE IF EXISTS detalle_boleta;
DROP TABLE IF EXISTS compras;
DROP TABLE IF EXISTS comprobantes;
DROP TABLE IF EXISTS asiento_estado_fecha;
DROP TABLE IF EXISTS asignacion_buses_rutas;
DROP TABLE IF EXISTS rutas;
DROP TABLE IF EXISTS asientos;
DROP TABLE IF EXISTS tipo_asiento;
DROP TABLE IF EXISTS buses;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS empleado_roles;
DROP TABLE IF EXISTS empleados;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS sedes;
DROP TABLE IF EXISTS empresa;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------------------------
-- 1. TABLA: empresa
-- ------------------------------------------------------------------------------
CREATE TABLE empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    ruc CHAR(11) NOT NULL UNIQUE,
    razon_social VARCHAR(150) NOT NULL,
    direccion VARCHAR(200) NOT NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 2. TABLA: sedes
-- ------------------------------------------------------------------------------
CREATE TABLE sedes (
    id_sede INT AUTO_INCREMENT PRIMARY KEY,
    nombre_sede VARCHAR(100) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    id_empresa INT NOT NULL,
    CONSTRAINT fk_sedes_empresa FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 3. TABLA: roles
-- ------------------------------------------------------------------------------
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 4. TABLA: empleados
-- ------------------------------------------------------------------------------
CREATE TABLE empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    id_sede INT NOT NULL,
    dni CHAR(8) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave_hash VARCHAR(255) NOT NULL,
    CONSTRAINT fk_empleados_sede FOREIGN KEY (id_sede)
        REFERENCES sedes(id_sede)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 5. TABLA: empleado_roles
-- ------------------------------------------------------------------------------
CREATE TABLE empleado_roles (
    id_empleado INT NOT NULL,
    id_rol INT NOT NULL,
    PRIMARY KEY (id_empleado, id_rol),
    CONSTRAINT fk_er_empleado FOREIGN KEY (id_empleado)
        REFERENCES empleados(id_empleado)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_er_rol FOREIGN KEY (id_rol)
        REFERENCES roles(id_rol)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 6. TABLA: clientes
-- ------------------------------------------------------------------------------
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    dni CHAR(8) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NULL,
    direccion VARCHAR(200) NULL,
    correo VARCHAR(100) NULL,
    INDEX idx_clientes_dni (dni)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 7. TABLA: buses
-- ------------------------------------------------------------------------------
CREATE TABLE buses (
    id_bus INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    modelo VARCHAR(100) NOT NULL,
    capacidad_asientos INT NOT NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 8. TABLA: tipo_asiento
-- ------------------------------------------------------------------------------
CREATE TABLE tipo_asiento (
    id_tipo_asiento INT AUTO_INCREMENT PRIMARY KEY,
    nombre ENUM('Estandar', 'VIP', 'Panoramico') NOT NULL UNIQUE,
    descripcion VARCHAR(200) NULL,
    cargo_adicional DECIMAL(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 9. TABLA: asientos
-- ------------------------------------------------------------------------------
CREATE TABLE asientos (
    id_asiento INT AUTO_INCREMENT PRIMARY KEY,
    id_bus INT NOT NULL,
    id_tipo_asiento INT NOT NULL,
    numero_asiento INT NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL DEFAULT 35.00,
    CONSTRAINT uk_bus_numero_asiento UNIQUE (id_bus, numero_asiento),
    CONSTRAINT fk_asientos_bus FOREIGN KEY (id_bus)
        REFERENCES buses(id_bus)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_asientos_tipo FOREIGN KEY (id_tipo_asiento)
        REFERENCES tipo_asiento(id_tipo_asiento)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 10. TABLA: rutas
-- ------------------------------------------------------------------------------
CREATE TABLE rutas (
    id_ruta INT AUTO_INCREMENT PRIMARY KEY,
    origen VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    duracion_horas DECIMAL(4,1) NOT NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 11. TABLA: asignacion_buses_rutas
-- ------------------------------------------------------------------------------
CREATE TABLE asignacion_buses_rutas (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_bus INT NOT NULL,
    id_ruta INT NOT NULL,
    fecha_salida DATE NOT NULL,
    hora_salida TIME NOT NULL,
    CONSTRAINT fk_abr_bus FOREIGN KEY (id_bus)
        REFERENCES buses(id_bus)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_abr_ruta FOREIGN KEY (id_ruta)
        REFERENCES rutas(id_ruta)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_asignacion_salida (fecha_salida, hora_salida)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 12. TABLA: asiento_estado_fecha (Disponibilidad puntual por salida)
-- ------------------------------------------------------------------------------
CREATE TABLE asiento_estado_fecha (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    id_asiento INT NOT NULL,
    id_asignacion INT NOT NULL,
    estado ENUM('Disponible', 'Reservado', 'Ocupado') NOT NULL DEFAULT 'Disponible',
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_asiento_asignacion UNIQUE (id_asiento, id_asignacion),
    CONSTRAINT fk_aef_asiento FOREIGN KEY (id_asiento)
        REFERENCES asientos(id_asiento)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_aef_asignacion FOREIGN KEY (id_asignacion)
        REFERENCES asignacion_buses_rutas(id_asignacion)
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_aef_estado (estado)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 13. TABLA: comprobantes
-- ------------------------------------------------------------------------------
CREATE TABLE comprobantes (
    id_comprobante INT AUTO_INCREMENT PRIMARY KEY,
    tipo_comprobante ENUM('Boleta', 'Factura') NOT NULL,
    numero_comprobante VARCHAR(20) NOT NULL UNIQUE,
    fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 14. TABLA: compras
-- ------------------------------------------------------------------------------
CREATE TABLE compras (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_empleado INT NOT NULL,
    fecha_compra DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL DEFAULT 'Efectivo',
    CONSTRAINT fk_compras_cliente FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_compras_empleado FOREIGN KEY (id_empleado)
        REFERENCES empleados(id_empleado)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 15. TABLA: detalle_boleta
-- ------------------------------------------------------------------------------
CREATE TABLE detalle_boleta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_asiento INT NOT NULL,
    id_asignacion INT NOT NULL,
    id_comprobante INT NOT NULL,
    fecha_viaje DATE NOT NULL,
    hora_viaje TIME NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_db_compra FOREIGN KEY (id_compra)
        REFERENCES compras(id_compra)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_db_asiento FOREIGN KEY (id_asiento)
        REFERENCES asientos(id_asiento)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_db_asignacion FOREIGN KEY (id_asignacion)
        REFERENCES asignacion_buses_rutas(id_asignacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_db_comprobante FOREIGN KEY (id_comprobante)
        REFERENCES comprobantes(id_comprobante)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 16. TABLA: detalle_encomienda (Módulo complementario contemplado en diseño)
-- ------------------------------------------------------------------------------
CREATE TABLE detalle_encomienda (
    id_detalle_encomienda INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_asignacion INT NOT NULL,
    id_comprobante INT NOT NULL,
    destinatario_nombre VARCHAR(100) NOT NULL,
    destinatario_dni CHAR(8) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    peso_kg DECIMAL(6,2) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_de_compra FOREIGN KEY (id_compra)
        REFERENCES compras(id_compra)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_de_asignacion FOREIGN KEY (id_asignacion)
        REFERENCES asignacion_buses_rutas(id_asignacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_de_comprobante FOREIGN KEY (id_comprobante)
        REFERENCES comprobantes(id_comprobante)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ==============================================================================
-- INSERCIÓN DE DATOS SEMILLA (SEED DATA)
-- ==============================================================================

-- 1. Empresa
INSERT INTO empresa (id_empresa, ruc, razon_social, direccion) VALUES
(1, '20601234567', 'Empresa de Transportes Vía Costa Express S.R.L.', 'Av. Víctor Raúl Haya de la Torre 342, Chimbote');

-- 2. Sedes
INSERT INTO sedes (id_sede, nombre_sede, ciudad, id_empresa) VALUES
(1, 'Agencia Chimbote - Terminal Terrestre El Chimbador', 'Chimbote', 1),
(2, 'Agencia Huaraz - Terminal Terrestre de Huaraz', 'Huaraz', 1);

-- 3. Roles
INSERT INTO roles (id_rol, nombre_rol) VALUES
(1, 'ADMINISTRADOR'),
(2, 'VENDEDOR');

-- 4. Empleados (Claves hasheadas con SHA-256)
-- admin123  -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- vendedor123 -> 56976bf24998ca63e35fe4f1e2469b5751d1856003e8d16fef0aafef496ed044
INSERT INTO empleados (id_empleado, id_sede, dni, nombre, apellido, usuario, clave_hash) VALUES
(1, 1, '10203040', 'Carlos', 'Valdiviezo', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'),
(2, 1, '72422674', 'Kevin', 'Estrada', 'vendedor', '56976bf24998ca63e35fe4f1e2469b5751d1856003e8d16fef0aafef496ed044');

-- 5. Asignación de Roles
INSERT INTO empleado_roles (id_empleado, id_rol) VALUES
(1, 1),
(2, 2);

-- 6. Clientes iniciales
INSERT INTO clientes (id_cliente, dni, nombre, apellido, telefono, direccion, correo) VALUES
(1, '45678901', 'Juan', 'Perez Rodriguez', '943123456', 'Urb. Buenos Aires Mz D Lte 12, Nuevo Chimbote', 'jperez@gmail.com'),
(2, '78901234', 'Maria', 'Gomez Alva', '943987654', 'Jr. San Martin 450, Huaraz', 'mgomez@outlook.com'),
(3, '40506070', 'Luis', 'Ramirez Mendoza', '987654321', 'Av. Bolognesi 780, Chimbote', 'lramirez@gmail.com');

-- 7. Bus
INSERT INTO buses (id_bus, placa, modelo, capacidad_asientos) VALUES
(1, 'T4A-950', 'Marcopolo Paradiso G7 1200 Scania', 30);

-- 8. Tipos de Asiento
INSERT INTO tipo_asiento (id_tipo_asiento, nombre, descripcion, cargo_adicional) VALUES
(1, 'Estandar', 'Asientos semi-cama 140° en segundo nivel con puerto USB', 0.00),
(2, 'VIP', 'Asientos sofá-cama 160° primer nivel con pantallas individuales', 15.00),
(3, 'Panoramico', 'Asientos frontales panorámicos con vista privilegiada', 10.00);

-- 9. 30 Asientos para Bus 1 (Asientos 1-10 VIP, 11-14 Panorámico, 15-30 Estándar)
INSERT INTO asientos (id_bus, id_tipo_asiento, numero_asiento, precio_base) VALUES
(1, 2, 1, 35.00), (1, 2, 2, 35.00), (1, 2, 3, 35.00), (1, 2, 4, 35.00),
(1, 2, 5, 35.00), (1, 2, 6, 35.00), (1, 2, 7, 35.00), (1, 2, 8, 35.00),
(1, 2, 9, 35.00), (1, 2, 10, 35.00),
(1, 3, 11, 35.00), (1, 3, 12, 35.00), (1, 3, 13, 35.00), (1, 3, 14, 35.00),
(1, 1, 15, 35.00), (1, 1, 16, 35.00), (1, 1, 17, 35.00), (1, 1, 18, 35.00),
(1, 1, 19, 35.00), (1, 1, 20, 35.00), (1, 1, 21, 35.00), (1, 1, 22, 35.00),
(1, 1, 23, 35.00), (1, 1, 24, 35.00), (1, 1, 25, 35.00), (1, 1, 26, 35.00),
(1, 1, 27, 35.00), (1, 1, 28, 35.00), (1, 1, 29, 35.00), (1, 1, 30, 35.00);

-- 10. Rutas
INSERT INTO rutas (id_ruta, origen, destino, duracion_horas) VALUES
(1, 'Chimbote', 'Huaraz', 4.5),
(2, 'Huaraz', 'Chimbote', 4.5);

-- 11. Asignaciones de Salidas Vigentes (Usa fechas relativas CURDATE() para vigencia permanente)
INSERT INTO asignacion_buses_rutas (id_asignacion, id_bus, id_ruta, fecha_salida, hora_salida) VALUES
(1, 1, 1, CURDATE(), '08:30:00'),
(2, 1, 1, CURDATE(), '14:00:00'),
(3, 1, 2, CURDATE() + INTERVAL 1 DAY, '09:00:00'),
(4, 1, 1, CURDATE() + INTERVAL 1 DAY, '20:30:00');

-- 12. Asiento Estado Fecha: Inicializar los 30 asientos para la Asignación 1 (Salida hoy 08:30)
INSERT INTO asiento_estado_fecha (id_asiento, id_asignacion, estado)
SELECT id_asiento, 1, 'Disponible' FROM asientos WHERE id_bus = 1;

-- Inicializar los 30 asientos para la Asignación 2 (Salida hoy 14:00)
INSERT INTO asiento_estado_fecha (id_asiento, id_asignacion, estado)
SELECT id_asiento, 2, 'Disponible' FROM asientos WHERE id_bus = 1;

-- Inicializar los 30 asientos para la Asignación 3 (Salida mañana 09:00)
INSERT INTO asiento_estado_fecha (id_asiento, id_asignacion, estado)
SELECT id_asiento, 3, 'Disponible' FROM asientos WHERE id_bus = 1;

-- Inicializar los 30 asientos para la Asignación 4 (Salida mañana 20:30)
INSERT INTO asiento_estado_fecha (id_asiento, id_asignacion, estado)
SELECT id_asiento, 4, 'Disponible' FROM asientos WHERE id_bus = 1;

-- Simulación de 2 asientos ya vendidos/ocupados en Asignación 1 para demostración de estados
UPDATE asiento_estado_fecha 
SET estado = 'Ocupado' 
WHERE id_asignacion = 1 AND id_asiento IN (3, 4);

-- 13. Comprobante histórico de prueba
INSERT INTO comprobantes (id_comprobante, tipo_comprobante, numero_comprobante, fecha_emision) VALUES
(1, 'Boleta', 'B001-000001', NOW());

-- 14. Compra histórica de prueba
INSERT INTO compras (id_compra, id_cliente, id_empleado, fecha_compra, total, metodo_pago) VALUES
(1, 1, 2, NOW(), 100.00, 'Efectivo');

-- 15. Detalle de boleta histórico correspondiente a los asientos 3 y 4 (VIP: 35 + 15 = 50 c/u)
INSERT INTO detalle_boleta (id_detalle, id_compra, id_asiento, id_asignacion, id_comprobante, fecha_viaje, hora_viaje, precio_unitario) VALUES
(1, 1, 3, 1, 1, CURDATE(), '08:30:00', 50.00),
(2, 1, 4, 1, 1, CURDATE(), '08:30:00', 50.00);
