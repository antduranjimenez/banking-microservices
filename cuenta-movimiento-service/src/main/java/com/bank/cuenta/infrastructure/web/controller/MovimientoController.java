package com.bank.cuenta.infrastructure.web.controller;

import com.bank.cuenta.application.port.in.MovimientoUseCase;
import com.bank.cuenta.domain.entity.Movimiento;
import com.bank.cuenta.infrastructure.web.dto.ApiResponse;
import com.bank.cuenta.infrastructure.web.dto.MovimientoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoUseCase movimientoUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoDto.Response>> registrar(
            @Valid @RequestBody MovimientoDto.Request request) {
        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(request.getTipoMovimiento())
                .valor(request.getValor())
                .build();
        Movimiento saved = movimientoUseCase.registrarMovimiento(request.getCuentaId(), movimiento);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movimiento registrado exitosamente", toResponse(saved)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoDto.Response>>> listar() {
        var movimientos = movimientoUseCase.listarMovimientos().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(movimientos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoDto.Response>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toResponse(movimientoUseCase.obtenerMovimientoPorId(id))));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<ApiResponse<List<MovimientoDto.Response>>> listarPorCuenta(@PathVariable Long cuentaId) {
        var movimientos = movimientoUseCase.listarMovimientosPorCuenta(cuentaId).stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(movimientos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovimientoDto.Response>> actualizar(
            @PathVariable Long id, @Valid @RequestBody MovimientoDto.Request request) {
        Movimiento m = Movimiento.builder().tipoMovimiento(request.getTipoMovimiento()).valor(request.getValor()).build();
        return ResponseEntity.ok(ApiResponse.success(toResponse(movimientoUseCase.actualizarMovimiento(id, m))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        movimientoUseCase.eliminarMovimiento(id);
        return ResponseEntity.ok(ApiResponse.success("Movimiento eliminado", null));
    }

    private MovimientoDto.Response toResponse(Movimiento m) {
        return MovimientoDto.Response.builder()
                .id(m.getId()).fecha(m.getFecha()).tipoMovimiento(m.getTipoMovimiento())
                .valor(m.getValor()).saldo(m.getSaldo()).cuentaId(m.getCuentaId()).build();
    }
}
