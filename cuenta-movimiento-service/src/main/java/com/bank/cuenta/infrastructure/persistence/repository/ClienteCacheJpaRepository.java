package com.bank.cuenta.infrastructure.persistence.repository;

import com.bank.cuenta.infrastructure.persistence.entity.ClienteCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteCacheJpaRepository extends JpaRepository<ClienteCache, Long> {
    Optional<ClienteCache> findByClienteId(String clienteId);

    void deleteByClienteId(String clienteId);
}
