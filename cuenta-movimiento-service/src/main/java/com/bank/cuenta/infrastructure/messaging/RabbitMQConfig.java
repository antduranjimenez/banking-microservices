package com.bank.cuenta.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.cliente}")
    private String exchangeName;

    @Value("${rabbitmq.queue.cliente-events}")
    private String queueName;

    @Bean
    public TopicExchange clienteExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue clienteEventsQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding clienteEventsBinding() {
        return BindingBuilder.bind(clienteEventsQueue())
                .to(clienteExchange())
                .with("cliente.*");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
