package com.bank.cliente.infrastructure.web.dto;

import com.bank.cliente.domain.entity.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ClienteDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        private Genero genero;

        @Positive(message = "La edad debe ser positiva")
        private Integer edad;

        @NotBlank(message = "La identificacion es obligatoria")
        private String identificacion;

        private String direccion;
        private String telefono;
        private String clienteId;

        @NotBlank(message = "La contrasena es obligatoria")
        private String contrasena;

        @NotNull(message = "El estado es obligatorio")
        private Boolean estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchRequest {
        private String nombre;
        private Genero genero;
        private Integer edad;
        private String direccion;
        private String telefono;
        private String contrasena;
        private Boolean estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String nombre;
        private Genero genero;
        private Integer edad;
        private String identificacion;
        private String direccion;
        private String telefono;
        private String clienteId;
        private Boolean estado;
    }
}
