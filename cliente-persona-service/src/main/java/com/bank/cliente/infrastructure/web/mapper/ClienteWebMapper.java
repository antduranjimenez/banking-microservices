package com.bank.cliente.infrastructure.web.mapper;

import com.bank.cliente.domain.entity.Cliente;
import com.bank.cliente.infrastructure.web.dto.ClienteDto;
import org.springframework.stereotype.Component;

@Component
public class ClienteWebMapper {

    public Cliente toDomain(ClienteDto.Request request) {
        return Cliente.builder()
                .nombre(request.getNombre())
                .genero(request.getGenero())
                .edad(request.getEdad())
                .identificacion(request.getIdentificacion())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .clienteId(request.getClienteId())
                .contrasena(request.getContrasena())
                .estado(request.getEstado())
                .build();
    }

    public Cliente toDomain(ClienteDto.PatchRequest request) {
        return Cliente.builder()
                .nombre(request.getNombre())
                .genero(request.getGenero())
                .edad(request.getEdad())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .contrasena(request.getContrasena())
                .estado(request.getEstado())
                .build();
    }

    public ClienteDto.Response toResponse(Cliente cliente) {
        return ClienteDto.Response.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(cliente.getClienteId())
                .estado(cliente.getEstado())
                .build();
    }
}
