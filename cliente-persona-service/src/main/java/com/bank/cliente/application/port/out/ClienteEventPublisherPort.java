package com.bank.cliente.application.port.out;

import com.bank.cliente.domain.entity.Cliente;

public interface ClienteEventPublisherPort {
    void publishClienteCreated(Cliente cliente);

    void publishClienteUpdated(Cliente cliente);

    void publishClienteDeleted(Long clienteId);
}
