package com.bank.cuenta.application.port.in;

import com.bank.cuenta.domain.entity.Movimiento;

import java.util.List;

public interface MovimientoUseCase {
    Movimiento registrarMovimiento(Long cuentaId, Movimiento movimiento);

    Movimiento obtenerMovimientoPorId(Long id);

    List<Movimiento> listarMovimientos();

    List<Movimiento> listarMovimientosPorCuenta(Long cuentaId);

    Movimiento actualizarMovimiento(Long id, Movimiento movimiento);

    void eliminarMovimiento(Long id);
}
