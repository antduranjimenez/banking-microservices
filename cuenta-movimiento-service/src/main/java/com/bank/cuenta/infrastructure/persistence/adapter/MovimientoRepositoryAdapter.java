package com.bank.cuenta.infrastructure.persistence.adapter;

import com.bank.cuenta.application.port.out.MovimientoRepositoryPort;
import com.bank.cuenta.infrastructure.persistence.entity.Cuenta;
import com.bank.cuenta.infrastructure.persistence.entity.Movimiento;
import com.bank.cuenta.infrastructure.persistence.repository.CuentaJpaRepository;
import com.bank.cuenta.infrastructure.persistence.repository.MovimientoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository movimientoJpaRepository;
    private final CuentaJpaRepository cuentaJpaRepository;

    @Override
    public com.bank.cuenta.domain.entity.Movimiento save(com.bank.cuenta.domain.entity.Movimiento movimiento) {
        Cuenta cuentaEntity = cuentaJpaRepository.getReferenceById(movimiento.getCuentaId());
        Movimiento entity = Movimiento.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .cuenta(cuentaEntity)
                .build();
        return toDomain(movimientoJpaRepository.save(entity));
    }

    @Override
    public Optional<com.bank.cuenta.domain.entity.Movimiento> findById(Long id) {
        return movimientoJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<com.bank.cuenta.domain.entity.Movimiento> findAll() {
        return movimientoJpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<com.bank.cuenta.domain.entity.Movimiento> findByCuentaId(Long cuentaId) {
        return movimientoJpaRepository.findByCuentaId(cuentaId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<com.bank.cuenta.domain.entity.Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        return movimientoJpaRepository.findByCuentaIdAndFechaBetween(cuentaId, inicio, fin)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        movimientoJpaRepository.deleteById(id);
    }

    private com.bank.cuenta.domain.entity.Movimiento toDomain(Movimiento entity) {
        return com.bank.cuenta.domain.entity.Movimiento.builder()
                .id(entity.getId())
                .fecha(entity.getFecha())
                .tipoMovimiento(entity.getTipoMovimiento())
                .valor(entity.getValor())
                .saldo(entity.getSaldo())
                .cuentaId(entity.getCuenta().getId())
                .build();
    }
}
