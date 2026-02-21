package com.bank.cuenta.infrastructure.web.dto;

import com.bank.cuenta.domain.entity.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class CuentaDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "El numero de cuenta es obligatorio")
        private String numeroCuenta;
        @NotNull(message = "El tipo de cuenta es obligatorio")
        private TipoCuenta tipoCuenta;
        @NotNull(message = "El saldo inicial es obligatorio")
        private BigDecimal saldoInicial;
        @NotNull(message = "El estado es obligatorio")
        private Boolean estado;
        @NotBlank(message = "El clienteId es obligatorio")
        private String clienteId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchRequest {
        private TipoCuenta tipoCuenta;
        private Boolean estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String numeroCuenta;
        private TipoCuenta tipoCuenta;
        private BigDecimal saldoInicial;
        private BigDecimal saldoDisponible;
        private Boolean estado;
        private String clienteId;
    }
}
