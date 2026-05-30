package com.quizzler.payment.dto;

import java.time.Instant;

public class PaymentCancellationDto {

    private final String cancellationId;
    private final String paymentId;
    private final Instant createdAt;

    public PaymentCancellationDto(String cancellationId, String paymentId, Instant createdAt) {
        this.cancellationId = cancellationId;
        this.paymentId = paymentId;
        this.createdAt = createdAt;
    }

    public String getCancellationId() {
        return cancellationId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
