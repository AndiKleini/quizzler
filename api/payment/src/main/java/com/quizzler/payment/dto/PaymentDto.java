package com.quizzler.payment.dto;

import java.time.Instant;

public class PaymentDto {

    private final String paymentId;
    private final String transactionId;
    private final int price;
    private final Instant createdAt;

    public PaymentDto(String paymentId, String transactionId, int price, Instant createdAt) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.price = price;
        this.createdAt = createdAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    /** Price in cents. */
    public int getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
