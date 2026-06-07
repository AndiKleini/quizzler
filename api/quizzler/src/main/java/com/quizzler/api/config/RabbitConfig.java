package com.quizzler.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Messaging topology for the quizzler API consumer. As the first step toward decoupling the two
 * APIs, payment-confirmed events are received from the payment API's topic exchange and used to
 * confirm the corresponding quiz-attempt purchase. The exchange name and routing key are shared
 * contract with the payment publisher; the queue and binding are owned here.
 */
@Configuration
public class RabbitConfig {

    public static final String PAYMENT_EVENTS_EXCHANGE = "payment.events";
    public static final String PAYMENT_CONFIRMED_ROUTING_KEY = "payment.confirmed";
    public static final String PAYMENT_CONFIRMED_QUEUE = "quizzler.payment-confirmed";

    @Bean
    public TopicExchange paymentEventsExchange() {
        return new TopicExchange(PAYMENT_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue paymentConfirmedQueue() {
        return QueueBuilder.durable(PAYMENT_CONFIRMED_QUEUE).build();
    }

    @Bean
    public Binding paymentConfirmedBinding(Queue paymentConfirmedQueue, TopicExchange paymentEventsExchange) {
        return BindingBuilder.bind(paymentConfirmedQueue)
                .to(paymentEventsExchange)
                .with(PAYMENT_CONFIRMED_ROUTING_KEY);
    }

    /**
     * Drives the auto-configured listener container's deserialization. Built from the Spring-managed
     * {@link ObjectMapper} so JSR-310 types such as {@code Instant} deserialize correctly.
     */
    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
