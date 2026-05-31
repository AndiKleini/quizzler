package com.quizzler.api.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Subset of the payment API's {@code POST /payment} response that the quizzler API consumes.
 * Unknown fields (transactionId, price, createdAt) are ignored on deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCreationResponse {

    private String paymentId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
