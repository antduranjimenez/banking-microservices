package com.bank.cuenta.domain.exception;

public class MovimientoNotFoundException extends RuntimeException {
    public MovimientoNotFoundException(Long id) {
        super("Movimiento no encontrado con id: " + id);
    }
}
