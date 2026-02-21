package com.bank.cliente.infrastructure.persistence.adapter;

import com.bank.cliente.application.port.out.ClienteRepositoryPort;
import com.bank.cliente.domain.entity.Cliente;
import com.bank.cliente.infrastructure.persistence.repository.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;
    private final ClientePersistenceMapper mapper;

    @Override
    public Cliente save(Cliente cliente) {
        var entity = mapper.toEntity(cliente);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> findByIdentificacion(String identificacion) {
        return jpaRepository.findByIdentificacion(identificacion).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByClienteId(String clienteId) {
        return jpaRepository.existsByClienteId(clienteId);
    }

    @Override
    public boolean existsByIdentificacion(String identificacion) {
        return jpaRepository.existsByIdentificacion(identificacion);
    }
}
