package com.bank.cliente.application.port.out;

import com.bank.cliente.domain.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente);

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByClienteId(String clienteId);

    Optional<Cliente> findByIdentificacion(String identificacion);

    List<Cliente> findAll();

    void deleteById(Long id);

    boolean existsByClienteId(String clienteId);

    boolean existsByIdentificacion(String identificacion);
}
