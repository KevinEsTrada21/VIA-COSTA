# VIACOSTA - Sistema de Venta de Pasajes y Gestión de Transporte Interprovincial

**Curso Integrador I: Sistemas Software — Avance 3**  
**Universidad Tecnológica del Perú (UTP)**

---

## 1. Descripción del Proyecto
Sistema de escritorio desarrollado en **Java 17 (LTS)** y **JavaFX** con una arquitectura por capas desacopladas (**MVC + DAO + Service Layer + Inyección de Dependencias por constructor y principios SOLID**). Diseñado con interfaz moderna en **Modo Oscuro (Dark Theme)** para agilizar la atención de venta de pasajes en taquilla, consulta de disponibilidad de asientos en tiempo real, asignación de itinerarios, integración simulada con el servicio externo de identidad **RENIEC (BPMN)** y emisión de comprobantes oficiales con persistencia transaccional en **MySQL 8.0**.

---

## 2. Stack Tecnológico
- **Lenguaje:** Java 17+ (LTS).
- **Framework UI:** JavaFX 17 (FXML y CSS desacoplado).
- **Gestor de Dependencias y Construcción:** Maven con Maven Wrapper (`mvnw` / `mvnw.cmd`) incluido para ejecución autónoma.
- **Motor de Base de Datos Principal:** MySQL 8.0+ (Base de datos: `viacosta`).
- **Motor de Contingencia Automática:** H2 Database Engine en memoria con compatibilidad MySQL (se activa automáticamente si MySQL no está disponible, asegurando sustentaciones 100% exitosas sin fallos).
- **Persistencia:** JDBC nativo con `PreparedStatement`, prevención de inyección SQL y manejo transaccional ACID (`setAutoCommit(false)`, `commit()`, `rollback()`).

---

## 3. Estructura de Paquetes y Arquitectura

```text
com.viacosta/
├── config/
│   └── DatabaseConnection.java      # Singleton y Factory JDBC con auto-contingencia H2
├── model/                           # Entidades POJO de dominio
│   ├── Empresa.java
│   ├── Sede.java
│   ├── Rol.java
│   ├── Empleado.java
│   ├── Cliente.java
│   ├── Bus.java
│   ├── TipoAsiento.java
│   ├── Asiento.java
│   ├── Ruta.java
│   ├── AsignacionBusRuta.java
│   ├── AsientoEstadoFecha.java
│   ├── Comprobante.java
│   ├── Compra.java
│   └── DetalleBoleta.java
├── dao/                             # Interfaces de acceso a datos (Patrón DAO)
│   ├── ClienteDAO.java
│   ├── ViajeDAO.java
│   ├── AsientoDAO.java
│   ├── VentaDAO.java
│   └── EmpleadoDAO.java
├── dao/impl/                        # Implementaciones JDBC con PreparedStatement
│   ├── ClienteDAOImpl.java
│   ├── ViajeDAOImpl.java
│   ├── AsientoDAOImpl.java
│   ├── VentaDAOImpl.java
│   └── EmpleadoDAOImpl.java
├── service/                         # Capa de Lógica de Negocio y Transacciones
│   ├── AuthService.java             # Autenticación y hash SHA-256
│   ├── ClienteService.java          # Validaciones y orquestación de clientes
│   ├── ReniecServiceMock.java       # Mock del participante externo RENIEC (BPMN)
│   ├── ViajeService.java            # Consulta de salidas, rutas y asientos
│   └── VentaService.java            # Transacción ACID de venta y anti-sobreventa
├── controller/                      # Controladores JavaFX (Capa Presentación / MVC)
│   ├── LoginController.java         # Inicio de sesión con indicador de BD
│   ├── MainLayoutController.java    # Dashboard con navegación modular y reloj
│   ├── VentasController.java        # Taquilla interactiva con mapa de cabina de bus
│   ├── ClientesController.java      # CRUD de pasajeros y consulta RENIEC
│   ├── ViajesController.java        # Consulta operativa y niveles de ocupación
│   ├── HistorialVentasController.java # Auditoría de compras y boletas emitidas
│   └── TicketModalController.java   # Visualización e impresión térmica de ticket
├── util/                            # Componentes de soporte
│   ├── AlertHelper.java             # Diálogos con estilo Dark Mode
│   ├── CurrencyFormatter.java       # Formato en soles (S/ #,##0.00)
│   ├── PasswordHasher.java          # Hasheo SHA-256
│   ├── SessionManager.java          # Manejo del colaborador en sesión
│   └── Validators.java              # Validadores de DNI (8 dígitos) y campos
└── MainApp.java                     # Clase principal ejecutable JavaFX

src/main/resources/
├── css/
│   └── styles.css                   # Hoja de estilos Dark Mode premium
├── db/
│   └── schema.sql                   # Script DDL (16 tablas) + Seed Data
├── fxml/
│   ├── Login.fxml                   # Pantalla de acceso
│   ├── MainLayout.fxml              # Shell general con menú lateral
│   ├── VentasView.fxml              # Taquilla con mapa interactivo de bus
│   ├── ClientesView.fxml            # Módulo de clientes y pasajeros
│   ├── ViajesView.fxml              # Programación de salidas y flota
│   ├── HistorialVentasView.fxml     # Registro de operaciones y boletas
│   └── TicketModal.fxml             # Comprobante impreso oficial
└── config.properties                # Configuración de base de datos
```

---

## 4. Base de Datos (`schema.sql`)

El script ubicado en `src/main/resources/db/schema.sql` crea la base de datos `viacosta` cumpliendo la **3ra Forma Normal (3FN)** con las 16 tablas documentadas en el informe:

1. `empresa`
2. `sedes`
3. `roles`
4. `empleados`
5. `empleado_roles`
6. `clientes`
7. `buses`
8. `tipo_asiento`
9. `asientos`
10. `rutas`
11. `asignacion_buses_rutas`
12. `asiento_estado_fecha` (Control puntual de disponibilidad por salida)
13. `comprobantes` (Boletas y Facturas)
14. `compras` (Cabecera de transacción)
15. `detalle_boleta` (Líneas de viaje por asiento)
16. `detalle_encomienda` (Diseño complementario para transporte)

### Credenciales configuradas en `config.properties`:
- **URL:** `jdbc:mysql://localhost:3306/viacosta`
- **Usuario:** `root`
- **Contraseña:** `TuContrasena123` (Modificable en `config.properties`)

---

## 5. Usuarios de Prueba para Sustentación

| Rol | Usuario | Contraseña | Nombres | Sede |
|---|---|---|---|---|
| **ADMINISTRADOR** | `admin` | `admin123` | Carlos Valdiviezo | Agencia Chimbote |
| **VENDEDOR / TAQUILLA** | `vendedor` | `vendedor123` | Kevin Estrada | Terminal El Chimbador |

*(En la pantalla de Login se incluyen botones de acceso rápido que autocompletan las credenciales con 1 solo clic).*

---

## 6. Instrucciones de Ejecución

### Opción 1: Mediante el ejecutable directo (Recomendado en Windows)
Haga doble clic en el archivo:
```text
run.bat
```

### Opción 2: Mediante consola con Maven Wrapper
Abra una terminal (PowerShell o CMD) en la carpeta del proyecto y ejecute:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw.cmd clean compile javafx:run
```

---

## 7. Casos de Prueba y Demostración Funcional (Avance 3 - 50%)

1. **RF-01 Autenticación:** Iniciar sesión con `vendedor` / `vendedor123`. Se observa el indicador de conexión `🟢 Motor: MySQL 8.0 (viacosta)`.
2. **RF-02 Consulta RENIEC y Registro:**
   - En el módulo de venta, ingrese el DNI `72422674` o `45678901` y presione **🔍 RENIEC**. Se autocompletan automáticamente los nombres, apellidos y dirección del pasajero.
3. **RF-03 Selección de Itinerario:**
   - Seleccione la salida de las 08:30 Chimbote -> Huaraz. Se carga la unidad `T4A-950` con 30 asientos.
4. **RF-04 Mapa Interactivo de Asientos:**
   - Observe los asientos 3 y 4 en color **Rojo (Ocupado)**.
   - Haga clic sobre los asientos 1 o 2 (Nivel VIP) o 15 (Estándar). Se iluminan en **Azul (Seleccionado)** y el total a pagar se calcula en tiempo real.
5. **RF-05 Transacción Atómica y Emisión:**
   - Presione **💳 Confirmar y Emitir Boleto**.
   - Se abre el modal con la **Boleta Electrónica** oficial con formato de ticket de viaje.
   - Al volver a la pantalla, los asientos adquiridos cambian a color rojo (`Ocupado`).
6. **Auditoría de Persistencia:**
   - Vaya a la sección **📊 Registro de Boletas** y verifique la nueva compra guardada con sus asientos asociados.
