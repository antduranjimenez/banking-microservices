package com.bank.cuenta;

import com.bank.cuenta.application.port.out.CuentaRepositoryPort;
import com.bank.cuenta.application.service.MovimientoService;
import com.bank.cuenta.domain.entity.Cuenta;
import com.bank.cuenta.domain.entity.Movimiento;
import com.bank.cuenta.domain.entity.TipoCuenta;
import com.bank.cuenta.domain.entity.TipoMovimiento;
import com.bank.cuenta.domain.exception.SaldoInsuficienteException;
import com.bank.cuenta.infrastructure.persistence.adapter.CuentaRepositoryAdapter;
import com.bank.cuenta.infrastructure.persistence.adapter.MovimientoRepositoryAdapter;
import com.bank.cuenta.infrastructure.persistence.repository.MovimientoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import({MovimientoService.class, CuentaRepositoryAdapter.class, MovimientoRepositoryAdapter.class})
@DisplayName("MovimientoService Integration Tests")
class MovimientoServiceIntegrationTest {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private CuentaRepositoryPort cuentaRepository;

    @Autowired
    private MovimientoJpaRepository movimientoJpaRepository;

    private Cuenta cuentaTest;

    @BeforeEach
    void setUp() {
        cuentaTest = cuentaRepository.save(Cuenta.builder()
                .numeroCuenta("TEST-001")
                .tipoCuenta(TipoCuenta.AHORROS)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoDisponible(new BigDecimal("1000.00"))
                .estado(true)
                .clienteId("CLI-TEST")
                .build());
    }

    @Test
    @DisplayName("Debe registrar deposito y actualizar saldo")
    void debeRegistrarDepositoYActualizarSaldo() {
        Movimiento deposito = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("500.00"))
                .build();

        Movimiento resultado = movimientoService.registrarMovimiento(cuentaTest.getId(), deposito);

        assertThat(resultado.getSaldo()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(resultado.getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);

        Cuenta cuentaActualizada = cuentaRepository.findById(cuentaTest.getId()).orElseThrow();
        assertThat(cuentaActualizada.getSaldoDisponible()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Debe registrar retiro y actualizar saldo")
    void debeRegistrarRetiroYActualizarSaldo() {
        Movimiento retiro = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("-300.00"))
                .build();

        Movimiento resultado = movimientoService.registrarMovimiento(cuentaTest.getId(), retiro);

        assertThat(resultado.getSaldo()).isEqualByComparingTo(new BigDecimal("700.00"));

        Cuenta cuentaActualizada = cuentaRepository.findById(cuentaTest.getId()).orElseThrow();
        assertThat(cuentaActualizada.getSaldoDisponible()).isEqualByComparingTo(new BigDecimal("700.00"));
    }

    @Test
    @DisplayName("F3: Debe lanzar SaldoInsuficienteException cuando saldo es insuficiente")
    void debeLanzarSaldoInsuficienteException() {
        Movimiento retiroExcesivo = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("-2000.00"))
                .build();

        assertThatThrownBy(() -> movimientoService.registrarMovimiento(cuentaTest.getId(), retiroExcesivo))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessage("Saldo no disponible");

        // Verificar que el saldo no cambio
        Cuenta cuentaSinCambios = cuentaRepository.findById(cuentaTest.getId()).orElseThrow();
        assertThat(cuentaSinCambios.getSaldoDisponible()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Debe registrar multiples movimientos secuencialmente")
    void debeRegistrarMultiplesMovimientos() {
        movimientoService.registrarMovimiento(cuentaTest.getId(),
                Movimiento.builder().valor(new BigDecimal("500.00")).build());
        movimientoService.registrarMovimiento(cuentaTest.getId(),
                Movimiento.builder().valor(new BigDecimal("-200.00")).build());
        movimientoService.registrarMovimiento(cuentaTest.getId(),
                Movimiento.builder().valor(new BigDecimal("100.00")).build());

        Cuenta cuentaFinal = cuentaRepository.findById(cuentaTest.getId()).orElseThrow();
        assertThat(cuentaFinal.getSaldoDisponible()).isEqualByComparingTo(new BigDecimal("1400.00"));
        assertThat(movimientoJpaRepository.findByCuentaId(cuentaTest.getId())).hasSize(3);
    }
}
