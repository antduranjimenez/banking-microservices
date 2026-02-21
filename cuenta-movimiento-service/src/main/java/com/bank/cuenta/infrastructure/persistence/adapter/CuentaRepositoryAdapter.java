package com.bank.cuenta.infrastructure.persistence.adapter;

import com.bank.cuenta.application.port.out.CuentaRepositoryPort;
import com.bank.cuenta.infrastructure.persistence.entity.Cuenta;
import com.bank.cuenta.infrastructure.persistence.repository.CuentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;

    @Override
    public com.bank.cuenta.domain.entity.Cuenta save(com.bank.cuenta.domain.entity.Cuenta cuenta) {
        Cuenta entity = toEntity(cuenta);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<com.bank.cuenta.domain.entity.Cuenta> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<com.bank.cuenta.domain.entity.Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(this::toDomain);
    }

    @Override
    public List<com.bank.cuenta.domain.entity.Cuenta> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<com.bank.cuenta.domain.entity.Cuenta> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }

    private com.bank.cuenta.domain.entity.Cuenta toDomain(Cuenta entity) {
        return com.bank.cuenta.domain.entity.Cuenta.builder()
                .id(entity.getId())
                .numeroCuenta(entity.getNumeroCuenta())
                .tipoCuenta(entity.getTipoCuenta())
                .saldoInicial(entity.getSaldoInicial())
                .saldoDisponible(entity.getSaldoDisponible())
                .estado(entity.getEstado())
                .clienteId(entity.getClienteId())
                .build();
    }

    private Cuenta toEntity(com.bank.cuenta.domain.entity.Cuenta domain) {
        return Cuenta.builder()
                .id(domain.getId())
                .numeroCuenta(domain.getNumeroCuenta())
                .tipoCuenta(domain.getTipoCuenta())
                .saldoInicial(domain.getSaldoInicial())
                .saldoDisponible(domain.getSaldoDisponible())
                .estado(domain.getEstado())
                .clienteId(domain.getClienteId())
                .build();
    }
}
