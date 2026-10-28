package com.viacosta.service;

import com.viacosta.model.Cliente;
import com.viacosta.util.Validators;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio simulado de consulta a la API de RENIEC.
 * Cumple con el modelado BPMN y la arquitectura descrita en el Avance 3
 * (Participante externo para verificación de identidad de pasajeros).
 */
public class ReniecServiceMock {

    private static final Map<String, Cliente> PADRON_RENIEC = new HashMap<>();

    static {
        // Integrantes del proyecto (portada oficial) y ciudadanos de prueba
        registrarEnPadron("72422674", "Kevin Delmis", "Estrada León", "Chimbote");
        registrarEnPadron("21309423", "José Manuel", "Ortiz Jauregui", "Nuevo Chimbote");
        registrarEnPadron("24226740", "Adrián Rodrigo", "Rodríguez Ramírez", "Huaraz");
        registrarEnPadron("23265061", "Kenyi Jhonatan", "Quiroz Capa", "Chimbote");

        // Pasajeros frecuentes
        registrarEnPadron("45678901", "Juan Carlos", "Pérez Rodríguez", "Nuevo Chimbote");
        registrarEnPadron("78901234", "María Elena", "Gómez Alva", "Huaraz");
        registrarEnPadron("40506070", "Luis Alberto", "Ramírez Mendoza", "Chimbote");
        registrarEnPadron("10203040", "Carlos Javier", "Valdiviezo Campos", "Chimbote");
        registrarEnPadron("87654321", "Rosa Isabel", "Torres Morales", "Huaraz");
    }

    private static void registrarEnPadron(String dni, String nombre, String apellido, String ciudad) {
        Cliente c = new Cliente();
        c.setDni(dni);
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setDireccion("Jr. Comercio 123, " + ciudad);
        c.setTelefono("9" + (int)(10000000 + Math.random() * 89999999));
        c.setCorreo((nombre.toLowerCase().replace(" ", "") + "@gmail.com"));
        PADRON_RENIEC.put(dni, c);
    }

    /**
     * Consulta el padrón de identidad por DNI (simulación de servicio web REST/SOAP).
     * @param dni 8 dígitos numéricos
     * @return Optional con los datos de identidad si es válido
     */
    public Optional<Cliente> consultarDni(String dni) {
        if (!Validators.esDniValido(dni)) {
            return Optional.empty();
        }

        // Si ya está en el padrón precargado
        if (PADRON_RENIEC.containsKey(dni)) {
            Cliente original = PADRON_RENIEC.get(dni);
            Cliente copia = new Cliente(0, original.getDni(), original.getNombre(), original.getApellido(),
                    original.getTelefono(), original.getDireccion(), original.getCorreo());
            return Optional.of(copia);
        }

        // Generador determinista de nombres para cualquier DNI válido de 8 dígitos de demostración
        String[] nombresMasc = {"Jorge", "Miguel", "David", "Julio", "Andrés", "Fernando", "Pedro", "Víctor"};
        String[] nombresFem = {"Ana", "Carmen", "Lucía", "Patricia", "Diana", "Valeria", "Sofía", "Camila"};
        String[] apellidos = {"Flores", "Huamán", "Quispe", "Vásquez", "Castillo", "Rojas", "Gutiérrez", "Chávez", "Salazar", "Mendoza"};

        long hash = Math.abs(dni.hashCode());
        boolean esMasc = (hash % 2 == 0);
        String nom = esMasc ? nombresMasc[(int)(hash % nombresMasc.length)] : nombresFem[(int)(hash % nombresFem.length)];
        String ape1 = apellidos[(int)((hash / 7) % apellidos.length)];
        String ape2 = apellidos[(int)((hash / 13) % apellidos.length)];

        Cliente generado = new Cliente(0, dni, nom, ape1 + " " + ape2, "9" + (int)(10000000 + (hash % 89999999)),
                "Av. Principal " + (hash % 900 + 100) + ", Áncash",
                nom.toLowerCase() + "." + ape1.toLowerCase() + "@gmail.com");

        return Optional.of(generado);
    }
}
