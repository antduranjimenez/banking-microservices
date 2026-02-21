package com.bank.cuenta.application.service;

import com.bank.cuenta.application.port.in.ReporteUseCase;
import com.bank.cuenta.application.port.out.ClienteCacheRepositoryPort;
import com.bank.cuenta.application.port.out.CuentaRepositoryPort;
import com.bank.cuenta.application.port.out.MovimientoRepositoryPort;
import com.bank.cuenta.domain.entity.ClienteCache;
import com.bank.cuenta.domain.entity.Cuenta;
import com.bank.cuenta.domain.entity.EstadoCuenta;
import com.bank.cuenta.domain.entity.Movimiento;
import com.bank.cuenta.domain.exception.CuentaNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService implements ReporteUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;
    private final ClienteCacheRepositoryPort clienteCacheRepository;

    @Override
    public EstadoCuenta generarEstadoCuenta(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        ClienteCache cliente = clienteCacheRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new CuentaNotFoundException("Cliente no encontrado: " + clienteId));

        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<EstadoCuenta.CuentaDetalle> cuentaDetalles = cuentas.stream()
                .map(cuenta -> {
                    List<Movimiento> movimientos = movimientoRepository
                            .findByCuentaIdAndFechaBetween(cuenta.getId(), inicio, fin);

                    List<EstadoCuenta.MovimientoDetalle> movimientosDetalle = movimientos.stream()
                            .map(m -> EstadoCuenta.MovimientoDetalle.builder()
                                    .id(m.getId())
                                    .fecha(m.getFecha())
                                    .tipoMovimiento(m.getTipoMovimiento())
                                    .valor(m.getValor())
                                    .saldo(m.getSaldo())
                                    .build())
                            .collect(Collectors.toList());

                    return EstadoCuenta.CuentaDetalle.builder()
                            .numeroCuenta(cuenta.getNumeroCuenta())
                            .tipoCuenta(cuenta.getTipoCuenta())
                            .saldoInicial(cuenta.getSaldoInicial())
                            .saldoDisponible(cuenta.getSaldoDisponible())
                            .estado(cuenta.getEstado())
                            .movimientos(movimientosDetalle)
                            .build();
                })
                .collect(Collectors.toList());

        return EstadoCuenta.builder()
                .clienteId(clienteId)
                .nombreCliente(cliente.getNombre())
                .cuentas(cuentaDetalles)
                .build();
    }
}
