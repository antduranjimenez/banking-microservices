package com.bank.cliente.infrastructure.messaging;

import com.bank.cliente.application.port.out.ClienteEventPublisherPort;
import com.bank.cliente.domain.entity.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventPublisherAdapter implements ClienteEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.cliente}")
    private String exchange;

    @Value("${rabbitmq.routing-key.cliente-created}")
    private String createdKey;

    @Value("${rabbitmq.routing-key.cliente-updated}")
    private String updatedKey;

    @Value("${rabbitmq.routing-key.cliente-deleted}")
    private String deletedKey;

    @Override
    public void publishClienteCreated(Cliente cliente) {
        ClienteEvent event = buildEvent("CREATED", cliente);
        publish(createdKey, event);
    }

    @Override
    public void publishClienteUpdated(Cliente cliente) {
        ClienteEvent event = buildEvent("UPDATED", cliente);
        publish(updatedKey, event);
    }

    @Override
    public void publishClienteDeleted(Long clienteId) {
        ClienteEvent event = ClienteEvent.builder()
                .eventType("DELETED")
                .id(clienteId)
                .timestamp(LocalDateTime.now())
                .build();
        publish(deletedKey, event);
    }

    private ClienteEvent buildEvent(String eventType, Cliente cliente) {
        return ClienteEvent.builder()
                .eventType(eventType)
                .id(cliente.getId())
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .identificacion(cliente.getIdentificacion())
                .estado(cliente.getEstado())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void publish(String routingKey, ClienteEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Evento publicado: {} para cliente id: {}", event.getEventType(), event.getId());
        } catch (Exception e) {
            log.error("Error al publicar evento de cliente: {}", e.getMessage(), e);
        }
    }
}
