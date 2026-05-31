package com.quizzler.api.dto;

public class PaymentInitiationDto {

    private final String paymentId;

    public PaymentInitiationDto(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
