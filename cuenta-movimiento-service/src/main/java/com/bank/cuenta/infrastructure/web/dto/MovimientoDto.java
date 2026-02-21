package com.bank.cuenta.infrastructure.web.dto;

import com.bank.cuenta.domain.entity.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private TipoMovimiento tipoMovimiento;
        @NotNull(message = "El valor es obligatorio")
        private BigDecimal valor;
        @NotNull(message = "El cuentaId es obligatorio")
        private Long cuentaId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private LocalDateTime fecha;
        private TipoMovimiento tipoMovimiento;
        private BigDecimal valor;
        private BigDecimal saldo;
        private Long cuentaId;
    }
}
