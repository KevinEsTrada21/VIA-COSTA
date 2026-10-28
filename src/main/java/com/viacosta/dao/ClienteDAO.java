package com.viacosta.dao;

import com.viacosta.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteDAO {
    Optional<Cliente> buscarPorDni(String dni);
    Optional<Cliente> buscarPorId(int idCliente);
    List<Cliente> listarTodos();
    List<Cliente> buscarPorCriterio(String texto);
    Cliente registrar(Cliente cliente);
    boolean actualizar(Cliente cliente);
    boolean eliminar(int idCliente);
}
