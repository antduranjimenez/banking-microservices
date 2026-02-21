package com.bank.cuenta.application.service;

import com.bank.cuenta.application.port.in.MovimientoUseCase;
import com.bank.cuenta.application.port.out.CuentaRepositoryPort;
import com.bank.cuenta.application.port.out.MovimientoRepositoryPort;
import com.bank.cuenta.domain.entity.Cuenta;
import com.bank.cuenta.domain.entity.Movimiento;
import com.bank.cuenta.domain.entity.TipoMovimiento;
import com.bank.cuenta.domain.exception.CuentaNotFoundException;
import com.bank.cuenta.domain.exception.MovimientoNotFoundException;
import com.bank.cuenta.domain.exception.SaldoInsuficienteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoService implements MovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepository;
    private final CuentaRepositoryPort cuentaRepository;

    @Override
    public Movimiento registrarMovimiento(Long cuentaId, Movimiento movimiento) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException(cuentaId));

        BigDecimal saldoActual = cuenta.getSaldoDisponible();
        BigDecimal nuevoSaldo = saldoActual.add(movimiento.getValor());

        // F3: Verificar saldo disponible para retiros (valores negativos)
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException();
        }

        // Actualizar saldo de la cuenta
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        if (movimiento.getTipoMovimiento() == null) {
            movimiento.setTipoMovimiento(movimiento.getValor().compareTo(BigDecimal.ZERO) >= 0
                    ? TipoMovimiento.DEPOSITO : TipoMovimiento.RETIRO);
        }

        movimiento.setCuentaId(cuentaId);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setFecha(LocalDateTime.now());

        Movimiento saved = movimientoRepository.save(movimiento);
        log.info("Movimiento registrado: {} en cuenta: {}, nuevo saldo: {}",
                saved.getId(), cuentaId, nuevoSaldo);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Movimiento obtenerMovimientoPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new MovimientoNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarMovimientosPorCuenta(Long cuentaId) {
        return movimientoRepository.findByCuentaId(cuentaId);
    }

    @Override
    public Movimiento actualizarMovimiento(Long id, Movimiento movimientoData) {
        Movimiento existing = obtenerMovimientoPorId(id);
        existing.setTipoMovimiento(movimientoData.getTipoMovimiento());
        existing.setValor(movimientoData.getValor());
        existing.setSaldo(movimientoData.getSaldo());
        return movimientoRepository.save(existing);
    }

    @Override
    public void eliminarMovimiento(Long id) {
        if (movimientoRepository.findById(id).isEmpty()) {
            throw new MovimientoNotFoundException(id);
        }
        movimientoRepository.deleteById(id);
    }
}
