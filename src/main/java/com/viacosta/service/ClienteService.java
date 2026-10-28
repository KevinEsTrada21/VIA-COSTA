package com.viacosta.service;

import com.viacosta.dao.ClienteDAO;
import com.viacosta.dao.impl.ClienteDAOImpl;
import com.viacosta.model.Cliente;
import com.viacosta.util.Validators;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteDAO clienteDAO;
    private final ReniecServiceMock reniecService;

    // Inyección de dependencias por constructor
    public ClienteService(ClienteDAO clienteDAO, ReniecServiceMock reniecService) {
        this.clienteDAO = clienteDAO;
        this.reniecService = reniecService;
    }

    public ClienteService() {
        this(new ClienteDAOImpl(), new ReniecServiceMock());
    }

    public Optional<Cliente> buscarPorDni(String dni) {
        if (!Validators.esDniValido(dni)) return Optional.empty();
        return clienteDAO.buscarPorDni(dni);
    }

    public Optional<Cliente> consultarEnReniec(String dni) {
        return reniecService.consultarDni(dni);
    }

    public List<Cliente> listarClientes(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return clienteDAO.listarTodos();
        }
        return clienteDAO.buscarPorCriterio(filtro);
    }

    public Cliente guardarCliente(Cliente cliente) {
        if (!Validators.esDniValido(cliente.getDni())) {
            throw new IllegalArgumentException("El DNI ingresado no es válido (debe tener 8 dígitos numéricos).");
        }
        if (!Validators.noVacio(cliente.getNombre()) || !Validators.noVacio(cliente.getApellido())) {
            throw new IllegalArgumentException("Los nombres y apellidos son obligatorios.");
        }

        Optional<Cliente> existente = clienteDAO.buscarPorDni(cliente.getDni());
        if (existente.isPresent()) {
            cliente.setIdCliente(existente.get().getIdCliente());
            clienteDAO.actualizar(cliente);
            return cliente;
        } else {
            return clienteDAO.registrar(cliente);
        }
    }

    public boolean eliminarCliente(int idCliente) {
        return clienteDAO.eliminar(idCliente);
    }
}
