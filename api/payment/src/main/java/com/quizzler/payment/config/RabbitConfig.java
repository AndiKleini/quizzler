package com.quizzler.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Messaging topology for the payment API. As the first step toward decoupling payment from the
 * quizzler API, payment-confirmed events are published to a topic exchange in addition to the
 * existing success-webhook HTTP call (see {@code PaymentConfirmationService}). The exchange name
 * and routing key are shared contract with the quizzler consumer.
 */
@Configuration
public class RabbitConfig {

    public static final String PAYMENT_EVENTS_EXCHANGE = "payment.events";
    public static final String PAYMENT_CONFIRMED_ROUTING_KEY = "payment.confirmed";

    @Bean
    public TopicExchange paymentEventsExchange() {
        return new TopicExchange(PAYMENT_EVENTS_EXCHANGE);
    }

    /**
     * Drives both the auto-configured {@code RabbitTemplate} (outbound) serialization. Built from the
     * Spring-managed {@link ObjectMapper} so JSR-310 types such as {@code Instant} serialize correctly.
     */
    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
