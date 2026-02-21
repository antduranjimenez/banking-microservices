package com.bank.cliente.infrastructure.web.controller;

import com.bank.cliente.application.port.in.ClienteUseCase;
import com.bank.cliente.infrastructure.web.dto.ApiResponse;
import com.bank.cliente.infrastructure.web.dto.ClienteDto;
import com.bank.cliente.infrastructure.web.mapper.ClienteWebMapper;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteUseCase clienteUseCase;
    private final ClienteWebMapper mapper;

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDto.Response>> crear(
            @Valid @RequestBody ClienteDto.Request request) {
        var cliente = clienteUseCase.crearCliente(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado exitosamente", mapper.toResponse(cliente)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDto.Response>>> listar() {
        var clientes = clienteUseCase.listarClientes().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(clientes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDto.Response>> obtenerPorId(@PathVariable Long id) {
        var cliente = clienteUseCase.obtenerClientePorId(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(cliente)));
    }

    @GetMapping("/clienteId/{clienteId}")
    public ResponseEntity<ApiResponse<ClienteDto.Response>> obtenerPorClienteId(
            @PathVariable String clienteId) {
        var cliente = clienteUseCase.obtenerClientePorClienteId(clienteId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(cliente)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDto.Response>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDto.Request request) {
        var cliente = clienteUseCase.actualizarCliente(id, mapper.toDomain(request));
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", mapper.toResponse(cliente)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDto.Response>> actualizarParcial(
            @PathVariable Long id,
            @RequestBody ClienteDto.PatchRequest request) {
        var cliente = clienteUseCase.actualizarClienteParcial(id, mapper.toDomain(request));
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", mapper.toResponse(cliente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        clienteUseCase.eliminarCliente(id);
        return ResponseEntity.ok(ApiResponse.success("Cliente eliminado exitosamente", null));
    }
}
