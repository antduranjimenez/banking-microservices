package com.bank.cuenta.application.port.in;

import com.bank.cuenta.domain.entity.Cuenta;

import java.util.List;

public interface CuentaUseCase {
    Cuenta crearCuenta(Cuenta cuenta);

    Cuenta obtenerCuentaPorId(Long id);

    Cuenta obtenerCuentaPorNumero(String numeroCuenta);

    List<Cuenta> listarCuentas();

    List<Cuenta> listarCuentasPorCliente(String clienteId);

    Cuenta actualizarCuenta(Long id, Cuenta cuenta);

    Cuenta actualizarCuentaParcial(Long id, Cuenta cuenta);

    void eliminarCuenta(Long id);
}
