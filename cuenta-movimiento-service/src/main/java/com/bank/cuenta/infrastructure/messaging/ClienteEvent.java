package com.bank.cuenta.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEvent {
    private String eventType;
    private Long id;
    private String clienteId;
    private String nombre;
    private String identificacion;
    private Boolean estado;
    private LocalDateTime timestamp;
}
