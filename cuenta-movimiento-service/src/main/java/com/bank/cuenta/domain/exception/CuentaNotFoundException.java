package com.bank.cuenta.domain.exception;

public class CuentaNotFoundException extends RuntimeException {
    public CuentaNotFoundException(Long id) {
        super("Cuenta no encontrada con id: " + id);
    }

    public CuentaNotFoundException(String numeroCuenta) {
        super("Cuenta no encontrada con numero: " + numeroCuenta);
    }
}
