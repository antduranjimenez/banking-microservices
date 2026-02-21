package com.bank.cuenta.infrastructure.web.controller;

import com.bank.cuenta.application.port.in.CuentaUseCase;
import com.bank.cuenta.domain.entity.Cuenta;
import com.bank.cuenta.infrastructure.web.dto.ApiResponse;
import com.bank.cuenta.infrastructure.web.dto.CuentaDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaUseCase cuentaUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaDto.Response>> crear(@Valid @RequestBody CuentaDto.Request request) {
        Cuenta cuenta = cuentaUseCase.crearCuenta(toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cuenta creada exitosamente", toResponse(cuenta)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaDto.Response>>> listar() {
        var cuentas = cuentaUseCase.listarCuentas().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(cuentas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDto.Response>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toResponse(cuentaUseCase.obtenerCuentaPorId(id))));
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<ApiResponse<CuentaDto.Response>> obtenerPorNumero(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(ApiResponse.success(toResponse(cuentaUseCase.obtenerCuentaPorNumero(numeroCuenta))));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<CuentaDto.Response>>> listarPorCliente(@PathVariable String clienteId) {
        var cuentas = cuentaUseCase.listarCuentasPorCliente(clienteId).stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(cuentas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDto.Response>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CuentaDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Cuenta actualizada", toResponse(cuentaUseCase.actualizarCuenta(id, toDomain(request)))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDto.Response>> actualizarParcial(
            @PathVariable Long id, @RequestBody CuentaDto.PatchRequest request) {
        Cuenta patch = Cuenta.builder().tipoCuenta(request.getTipoCuenta()).estado(request.getEstado()).build();
        return ResponseEntity.ok(ApiResponse.success("Cuenta actualizada", toResponse(cuentaUseCase.actualizarCuentaParcial(id, patch))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        cuentaUseCase.eliminarCuenta(id);
        return ResponseEntity.ok(ApiResponse.success("Cuenta eliminada", null));
    }

    private Cuenta toDomain(CuentaDto.Request r) {
        return Cuenta.builder()
                .numeroCuenta(r.getNumeroCuenta()).tipoCuenta(r.getTipoCuenta())
                .saldoInicial(r.getSaldoInicial()).saldoDisponible(r.getSaldoInicial())
                .estado(r.getEstado()).clienteId(r.getClienteId()).build();
    }

    private CuentaDto.Response toResponse(Cuenta c) {
        return CuentaDto.Response.builder()
                .id(c.getId()).numeroCuenta(c.getNumeroCuenta()).tipoCuenta(c.getTipoCuenta())
                .saldoInicial(c.getSaldoInicial()).saldoDisponible(c.getSaldoDisponible())
                .estado(c.getEstado()).clienteId(c.getClienteId()).build();
    }
}
