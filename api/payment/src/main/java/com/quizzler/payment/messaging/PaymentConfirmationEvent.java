package com.quizzler.payment.messaging;

public class PaymentConfirmationEvent {

    private String transactionId;
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentConfirmationEvent(String transactionId, String productId) {
        this.transactionId = transactionId;
        this.productId = productId;
    }
}
