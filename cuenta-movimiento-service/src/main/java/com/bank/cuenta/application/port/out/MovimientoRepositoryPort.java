package com.bank.cuenta.application.port.out;

import com.bank.cuenta.domain.entity.Movimiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {
    Movimiento save(Movimiento movimiento);

    Optional<Movimiento> findById(Long id);

    List<Movimiento> findAll();

    List<Movimiento> findByCuentaId(Long cuentaId);

    List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime inicio, LocalDateTime fin);

    void deleteById(Long id);
}
