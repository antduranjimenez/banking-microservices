package com.bank.cuenta.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoCuenta {
    private String clienteId;
    private String nombreCliente;
    private List<CuentaDetalle> cuentas;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CuentaDetalle {
        private String numeroCuenta;
        private TipoCuenta tipoCuenta;
        private BigDecimal saldoInicial;
        private BigDecimal saldoDisponible;
        private Boolean estado;
        private List<MovimientoDetalle> movimientos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MovimientoDetalle {
        private Long id;
        private LocalDateTime fecha;
        private TipoMovimiento tipoMovimiento;
        private BigDecimal valor;
        private BigDecimal saldo;
    }
}
