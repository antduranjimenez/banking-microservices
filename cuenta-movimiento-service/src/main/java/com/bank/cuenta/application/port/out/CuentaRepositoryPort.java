package com.bank.cuenta.application.port.out;

import com.bank.cuenta.domain.entity.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {
    Cuenta save(Cuenta cuenta);

    Optional<Cuenta> findById(Long id);

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    List<Cuenta> findAll();

    List<Cuenta> findByClienteId(String clienteId);

    void deleteById(Long id);

    boolean existsByNumeroCuenta(String numeroCuenta);
}
