package com.quizzler.payment.dto;

import java.time.Instant;

public class PaymentConfirmationDto {

    private final String confirmationId;
    private final String paymentId;
    private final Instant createdAt;

    public PaymentConfirmationDto(String confirmationId, String paymentId, Instant createdAt) {
        this.confirmationId = confirmationId;
        this.paymentId = paymentId;
        this.createdAt = createdAt;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
