package com.quizzler.api.messaging;

/**
 * PaymentConfirmationEvent
 */
public class PaymentConfirmationEvent {

    private String transactionId;
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public PaymentConfirmationEvent(String transactionId, String productId) {
        this.transactionId = transactionId;
        this.productId = productId;
    }
}
