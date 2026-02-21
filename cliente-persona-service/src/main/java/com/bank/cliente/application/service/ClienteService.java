package com.bank.cliente.application.service;

import com.bank.cliente.application.port.in.ClienteUseCase;
import com.bank.cliente.application.port.out.ClienteEventPublisherPort;
import com.bank.cliente.application.port.out.ClienteRepositoryPort;
import com.bank.cliente.domain.entity.Cliente;
import com.bank.cliente.domain.exception.ClienteAlreadyExistsException;
import com.bank.cliente.domain.exception.ClienteNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final ClienteEventPublisherPort eventPublisher;

    @Override
    public Cliente crearCliente(Cliente cliente) {
        log.info("Creando cliente con identificacion: {}", cliente.getIdentificacion());

        if (clienteRepository.existsByIdentificacion(cliente.getIdentificacion())) {
            throw new ClienteAlreadyExistsException(
                    "Ya existe un cliente con identificacion: " + cliente.getIdentificacion()
            );
        }

        if (cliente.getClienteId() == null || cliente.getClienteId().isBlank()) {
            cliente.setClienteId("CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        if (clienteRepository.existsByClienteId(cliente.getClienteId())) {
            throw new ClienteAlreadyExistsException(
                    "Ya existe un cliente con clienteId: " + cliente.getClienteId()
            );
        }

        Cliente saved = clienteRepository.save(cliente);
        eventPublisher.publishClienteCreated(saved);
        log.info("Cliente creado exitosamente con id: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerClientePorClienteId(String clienteId) {
        return clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("clienteId", clienteId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente actualizarCliente(Long id, Cliente clienteData) {
        Cliente existing = obtenerClientePorId(id);

        existing.setNombre(clienteData.getNombre());
        existing.setGenero(clienteData.getGenero());
        existing.setEdad(clienteData.getEdad());
        existing.setDireccion(clienteData.getDireccion());
        existing.setTelefono(clienteData.getTelefono());
        existing.setContrasena(clienteData.getContrasena());
        existing.setEstado(clienteData.getEstado());

        Cliente updated = clienteRepository.save(existing);
        eventPublisher.publishClienteUpdated(updated);
        return updated;
    }

    @Override
    public Cliente actualizarClienteParcial(Long id, Cliente clienteData) {
        Cliente existing = obtenerClientePorId(id);

        if (clienteData.getNombre() != null) existing.setNombre(clienteData.getNombre());
        if (clienteData.getGenero() != null) existing.setGenero(clienteData.getGenero());
        if (clienteData.getEdad() != null) existing.setEdad(clienteData.getEdad());
        if (clienteData.getDireccion() != null) existing.setDireccion(clienteData.getDireccion());
        if (clienteData.getTelefono() != null) existing.setTelefono(clienteData.getTelefono());
        if (clienteData.getContrasena() != null) existing.setContrasena(clienteData.getContrasena());
        if (clienteData.getEstado() != null) existing.setEstado(clienteData.getEstado());

        Cliente updated = clienteRepository.save(existing);
        eventPublisher.publishClienteUpdated(updated);
        return updated;
    }

    @Override
    public void eliminarCliente(Long id) {
        if (clienteRepository.findById(id).isEmpty()) {
            throw new ClienteNotFoundException(id);
        }
        clienteRepository.deleteById(id);
        eventPublisher.publishClienteDeleted(id);
        log.info("Cliente eliminado con id: {}", id);
    }
}
