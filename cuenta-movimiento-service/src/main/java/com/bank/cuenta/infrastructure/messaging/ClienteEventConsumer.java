package com.bank.cuenta.infrastructure.messaging;

import com.bank.cuenta.application.port.out.ClienteCacheRepositoryPort;
import com.bank.cuenta.domain.entity.ClienteCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventConsumer {

    private final ClienteCacheRepositoryPort clienteCacheRepository;

    @RabbitListener(queues = "${rabbitmq.queue.cliente-events}")
    public void handleClienteEvent(ClienteEvent event) {
        log.info("Evento recibido: {} para cliente: {}", event.getEventType(), event.getClienteId());

        try {
            switch (event.getEventType()) {
                case "CREATED", "UPDATED" -> {
                    ClienteCache cache = clienteCacheRepository.findByClienteId(event.getClienteId())
                            .orElseGet(ClienteCache::new);
                    cache.setClienteId(event.getClienteId());
                    cache.setNombre(event.getNombre());
                    cache.setIdentificacion(event.getIdentificacion());
                    cache.setEstado(event.getEstado());
                    clienteCacheRepository.save(cache);
                    log.info("ClienteCache sincronizado para clienteId: {}", event.getClienteId());
                }
                case "DELETED" -> {
                    if (event.getClienteId() != null) {
                        clienteCacheRepository.deleteByClienteId(event.getClienteId());
                        log.info("ClienteCache eliminado para clienteId: {}", event.getClienteId());
                    }
                }
                default -> log.warn("Tipo de evento desconocido: {}", event.getEventType());
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Conflicto de integridad al procesar evento {} para clienteId: {}. " +
                    "Posible dato duplicado proveniente del origen. Detalle: {}",
                    event.getEventType(), event.getClienteId(), e.getMostSpecificCause().getMessage());
        }
    }
}
