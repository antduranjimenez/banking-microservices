package com.bank.cuenta.infrastructure.web.controller;

import com.bank.cuenta.application.port.in.ReporteUseCase;
import com.bank.cuenta.domain.entity.EstadoCuenta;
import com.bank.cuenta.infrastructure.web.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteUseCase reporteUseCase;

    /**
     * F4: Reporte de Estado de Cuenta
     * GET /api/reportes?fecha=2022-01-01,2022-12-31&cliente=CLI-1712345678
     */
    @GetMapping
    public ResponseEntity<ApiResponse<EstadoCuenta>> generarReporte(
            @RequestParam("fecha") String fechaRango,
            @RequestParam("cliente") String clienteId) {

        String[] fechas = fechaRango.split(",");
        LocalDate fechaInicio = LocalDate.parse(fechas[0].trim());
        LocalDate fechaFin = LocalDate.parse(fechas[1].trim());

        EstadoCuenta estadoCuenta = reporteUseCase.generarEstadoCuenta(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(estadoCuenta));
    }
}
