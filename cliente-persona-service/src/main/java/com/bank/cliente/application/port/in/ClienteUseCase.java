package com.bank.cliente.application.port.in;

import com.bank.cliente.domain.entity.Cliente;

import java.util.List;

public interface ClienteUseCase {
    Cliente crearCliente(Cliente cliente);

    Cliente obtenerClientePorId(Long id);

    Cliente obtenerClientePorClienteId(String clienteId);

    List<Cliente> listarClientes();

    Cliente actualizarCliente(Long id, Cliente cliente);

    Cliente actualizarClienteParcial(Long id, Cliente cliente);

    void eliminarCliente(Long id);
}
