package com.bank.cliente.infrastructure.persistence.adapter;

import com.bank.cliente.infrastructure.persistence.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClientePersistenceMapper {

    public com.bank.cliente.domain.entity.Cliente toDomain(Cliente entity) {
        if (entity == null) return null;
        return com.bank.cliente.domain.entity.Cliente.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .genero(entity.getGenero())
                .edad(entity.getEdad())
                .identificacion(entity.getIdentificacion())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .clienteId(entity.getClienteId())
                .contrasena(entity.getContrasena())
                .estado(entity.getEstado())
                .build();
    }

    public Cliente toEntity(com.bank.cliente.domain.entity.Cliente domain) {
        if (domain == null) return null;
        return Cliente.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .genero(domain.getGenero())
                .edad(domain.getEdad())
                .identificacion(domain.getIdentificacion())
                .direccion(domain.getDireccion())
                .telefono(domain.getTelefono())
                .clienteId(domain.getClienteId())
                .contrasena(domain.getContrasena())
                .estado(domain.getEstado())
                .build();
    }
}
