package com.quizzler.payment.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RabbitPublication(exchange = "payment.events", routingKey = "payment.confirmed")
public class PaymentConfirmationPublisher extends RabbitEventPublisher<PaymentConfirmationEvent> {

    protected PaymentConfirmationPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    @Override
    public void publish(PaymentConfirmationEvent event) {
        super.publish(event);
    }
}