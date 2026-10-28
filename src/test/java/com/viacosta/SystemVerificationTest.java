package com.viacosta;

import com.viacosta.config.DatabaseConnection;
import com.viacosta.model.*;
import com.viacosta.service.AuthService;
import com.viacosta.service.ClienteService;
import com.viacosta.service.ReniecServiceMock;
import com.viacosta.service.VentaService;
import com.viacosta.service.ViajeService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SystemVerificationTest {

    private static AuthService authService;
    private static ClienteService clienteService;
    private static ViajeService viajeService;
    private static VentaService ventaService;
    private static ReniecServiceMock reniecMock;

    @BeforeAll
    public static void setUp() {
        authService = new AuthService();
        clienteService = new ClienteService();
        viajeService = new ViajeService();
        ventaService = new VentaService();
        reniecMock = new ReniecServiceMock();
    }

    @Test
    @Order(1)
    @DisplayName("CP-01: Verificación de Conectividad con el Motor de Base de Datos")
    public void testConexionBaseDatos() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull(conn, "La conexión a la base de datos no debe ser nula");
            assertFalse(conn.isClosed(), "La conexión debe encontrarse activa");
            System.out.println("✅ [CP-01 OK] Conexión establecida. Motor activo: " +
                    (DatabaseConnection.isH2Fallback() ? "H2 Embebido (Memoria)" : "MySQL 8.0 (viacosta)"));
        }
    }

    @Test
    @Order(2)
    @DisplayName("CP-02: Autenticación de Colaboradores con Hash SHA-256 (Admin y Vendedor)")
    public void testAutenticacionUsuarios() {
        Optional<Empleado> admin = authService.autenticar("admin", "admin123");
        assertTrue(admin.isPresent(), "El usuario 'admin' debe autenticar exitosamente");
        assertEquals("ADMINISTRADOR", admin.get().getNombreRol(), "El rol debe ser ADMINISTRADOR");

        Optional<Empleado> vendedor = authService.autenticar("vendedor", "vendedor123");
        assertTrue(vendedor.isPresent(), "El usuario 'vendedor' debe autenticar exitosamente");
        assertEquals("VENDEDOR", vendedor.get().getNombreRol(), "El rol debe ser VENDEDOR");

        Optional<Empleado> fail = authService.autenticar("admin", "clave_invalida_999");
        assertTrue(fail.isEmpty(), "No debe permitir el acceso con contraseñas erróneas");

        System.out.println("✅ [CP-02 OK] Autenticación y control de roles verificado.");
    }

    @Test
    @Order(3)
    @DisplayName("CP-03: Consulta Externa a la API RENIEC (BPMN) y Padrón Electoral")
    public void testConsultaReniec() {
        Optional<Cliente> opt = reniecMock.consultarDni("72422674");
        assertTrue(opt.isPresent(), "El DNI 72422674 debe existir en el padrón");
        assertEquals("Kevin Delmis", opt.get().getNombre());
        assertEquals("Estrada León", opt.get().getApellido());

        // Consulta DNI de pasajero
        Optional<Cliente> opt2 = reniecMock.consultarDni("45678901");
        assertTrue(opt2.isPresent());
        assertEquals("Juan Carlos", opt2.get().getNombre());

        System.out.println("✅ [CP-03 OK] Servicio RENIEC participante externo responde con éxito.");
    }

    @Test
    @Order(4)
    @DisplayName("CP-04: Consulta de Salidas Programadas y Catálogos de Transporte")
    public void testConsultaSalidas() {
        List<AsignacionBusRuta> salidas = viajeService.listarSalidasVigentes();
        assertNotNull(salidas, "La lista de salidas no debe ser nula");
        assertFalse(salidas.isEmpty(), "Debe existir al menos 1 salida programada vigente");

        AsignacionBusRuta primera = salidas.get(0);
        assertTrue(primera.getCapacidadTotal() > 0, "La capacidad total debe ser mayor a 0");
        assertNotNull(primera.getPlacaBus(), "La placa del bus debe estar informada");

        System.out.println("✅ [CP-04 OK] Salidas consultadas. Primera: " + primera.getRutaCompleta() +
                " en Bus " + primera.getPlacaBus() + " (Cupos libres: " + primera.getAsientosDisponibles() + ")");
    }

    @Test
    @Order(5)
    @DisplayName("CP-05: Transacción Atómica de Venta de Pasaje y Emisión de Comprobante")
    public void testTransaccionVentaPasaje() throws Exception {
        List<AsignacionBusRuta> salidas = viajeService.listarSalidasVigentes();
        AsignacionBusRuta salida = salidas.get(0);

        List<AsientoEstadoFecha> asientos = viajeService.obtenerAsientosParaViaje(salida.getIdAsignacion(), salida.getIdBus());
        
        // Buscar un asiento disponible (por ejemplo el asiento 10 o 15)
        AsientoEstadoFecha asientoLibre = asientos.stream()
                .filter(AsientoEstadoFecha::isDisponible)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay asientos libres para la prueba"));

        Cliente pasajero = new Cliente(0, "40506070", "Luis", "Ramirez", "987654321", "Chimbote", "lramirez@gmail.com");
        Empleado vendedor = authService.autenticar("vendedor", "vendedor123").orElseThrow();

        // Ejecutar transacción atómica de venta
        VentaService.ResultadoVenta res = ventaService.procesarVenta(
                pasajero,
                vendedor,
                salida,
                Collections.singletonList(asientoLibre),
                "Boleta",
                "Efectivo"
        );

        assertNotNull(res, "El resultado de la venta no debe ser nulo");
        assertNotNull(res.getComprobante().getNumeroComprobante(), "Debe tener número de comprobante generado");
        assertTrue(res.getCompra().getIdCompra() > 0, "Debe tener ID de compra persistido");
        assertEquals(1, res.getDetalles().size(), "Debe tener 1 detalle de boleta");

        System.out.println("✅ [CP-05 OK] Venta exitosa. Comprobante emitido: " +
                res.getComprobante().getNumeroComprobante() + " | Importe: S/ " + res.getCompra().getTotal());

        // CP-06: Verificación de Anti-sobreventa (Intento de comprar el MISMO asiento en la MISMA salida)
        assertThrows(IllegalStateException.class, () -> {
            ventaService.procesarVenta(
                    pasajero,
                    vendedor,
                    salida,
                    Collections.singletonList(asientoLibre),
                    "Boleta",
                    "Efectivo"
            );
        }, "El sistema debe rechazar la compra duplicada del mismo asiento para la misma salida");

        System.out.println("✅ [CP-06 OK] Anti-sobreventa validado: El intento de doble venta fue bloqueado correctamente.");
    }
}
