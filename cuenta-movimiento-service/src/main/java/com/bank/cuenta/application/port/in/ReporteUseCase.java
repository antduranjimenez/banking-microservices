package com.bank.cuenta.application.port.in;

import com.bank.cuenta.domain.entity.EstadoCuenta;

import java.time.LocalDate;

public interface ReporteUseCase {
    EstadoCuenta generarEstadoCuenta(String clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
