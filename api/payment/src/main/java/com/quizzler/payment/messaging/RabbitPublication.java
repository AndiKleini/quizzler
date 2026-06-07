package com.quizzler.payment.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the destination a {@link RabbitEventPublisher} sends to. Placed on a concrete publisher
 * subclass, it binds that publisher to a fixed exchange and routing key so the base class needs no
 * per-call destination arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RabbitPublication {

    String exchange();

    String routingKey();
}
