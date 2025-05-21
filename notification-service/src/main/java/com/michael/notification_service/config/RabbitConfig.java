package com.michael.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "notifications.exchange";
    public static final String QUEUE    = "notifications.queue";
    public static final String ROUTING_KEY = "notifications.routingkey";

    @Bean
    public Exchange notificationExchange() {
        return ExchangeBuilder
                .directExchange(EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
                .durable(QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE + ".dlx")
                .build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, Exchange notificationExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY)
                .noargs();
    }

    // --- JSON converter ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        return template;
    }
}
