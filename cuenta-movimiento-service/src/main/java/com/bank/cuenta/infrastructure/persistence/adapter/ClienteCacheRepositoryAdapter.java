package com.bank.cuenta.infrastructure.persistence.adapter;

import com.bank.cuenta.application.port.out.ClienteCacheRepositoryPort;
import com.bank.cuenta.infrastructure.persistence.entity.ClienteCache;
import com.bank.cuenta.infrastructure.persistence.repository.ClienteCacheJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteCacheRepositoryAdapter implements ClienteCacheRepositoryPort {

    private final ClienteCacheJpaRepository jpaRepository;

    @Override
    public com.bank.cuenta.domain.entity.ClienteCache save(com.bank.cuenta.domain.entity.ClienteCache clienteCache) {
        ClienteCache entity = ClienteCache.builder()
                .id(clienteCache.getId())
                .clienteId(clienteCache.getClienteId())
                .nombre(clienteCache.getNombre())
                .identificacion(clienteCache.getIdentificacion())
                .estado(clienteCache.getEstado())
                .build();
        ClienteCache saved = jpaRepository.save(entity);
        return com.bank.cuenta.domain.entity.ClienteCache.builder()
                .id(saved.getId())
                .clienteId(saved.getClienteId())
                .nombre(saved.getNombre())
                .identificacion(saved.getIdentificacion())
                .estado(saved.getEstado())
                .build();
    }

    @Override
    public Optional<com.bank.cuenta.domain.entity.ClienteCache> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId).map(e -> com.bank.cuenta.domain.entity.ClienteCache.builder()
                .id(e.getId())
                .clienteId(e.getClienteId())
                .nombre(e.getNombre())
                .identificacion(e.getIdentificacion())
                .estado(e.getEstado())
                .build());
    }

    @Override
    public void deleteByClienteId(String clienteId) {
        jpaRepository.deleteByClienteId(clienteId);
    }
}
