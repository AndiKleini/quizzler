package com.quizzler.payment.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Generic, best-effort publisher for events of type {@code T}. The destination is taken from the
 * {@link RabbitPublication} annotation on the concrete subclass, so a subclass only declares the
 * event type and the exchange/routing key — no per-call destination is needed.
 *
 * <p>Publishing is best-effort: during the transition away from the success-webhook HTTP call the
 * webhook remains the authoritative path, so a broker outage is logged and swallowed rather than
 * propagated to the caller.
 *
 * @param <T> the event payload type this publisher emits
 */
public abstract class RabbitEventPublisher<T> {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    protected RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        RabbitPublication publication = AnnotationUtils.findAnnotation(getClass(), RabbitPublication.class);
        if (publication == null) {
            throw new IllegalStateException(
                    getClass().getName() + " must be annotated with @" + RabbitPublication.class.getSimpleName());
        }
        this.exchange = publication.exchange();
        this.routingKey = publication.routingKey();
    }

    public void publish(T event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (AmqpException ex) {
            log.warn("Failed to publish {} to exchange '{}' with routing key '{}': {}",
                    event.getClass().getSimpleName(), exchange, routingKey, ex.getMessage());
        }
    }
}
