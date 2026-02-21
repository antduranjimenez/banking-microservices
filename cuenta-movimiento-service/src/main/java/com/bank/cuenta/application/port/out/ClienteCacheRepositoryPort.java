package com.bank.cuenta.application.port.out;

import com.bank.cuenta.domain.entity.ClienteCache;

import java.util.Optional;

public interface ClienteCacheRepositoryPort {
    ClienteCache save(ClienteCache clienteCache);

    Optional<ClienteCache> findByClienteId(String clienteId);

    void deleteByClienteId(String clienteId);
}
