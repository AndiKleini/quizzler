package com.quizzler.api.messaging;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.quizzler.api.service.QuizAttemptPurchaseService;

@Component
public class PaymentConfirmationListener {

    private QuizAttemptPurchaseService purchaseService;

    public PaymentConfirmationListener(QuizAttemptPurchaseService quizAttemptPurchaseService) {
        this.purchaseService = quizAttemptPurchaseService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("payment.events"),
            exchange = @Exchange(value = "payment.events", type = ExchangeTypes.TOPIC),
            key = "payment.confirmed"))
    public void handleConfirmation(PaymentConfirmationEvent paymentConfirmationEvent) {
        this.purchaseService.confirmPurchase(
            paymentConfirmationEvent.getProductId(),
            paymentConfirmationEvent.getTransactionId());
    }
}
