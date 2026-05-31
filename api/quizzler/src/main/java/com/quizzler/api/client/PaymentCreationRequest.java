package com.quizzler.api.client;

/**
 * Request body sent to the payment API's {@code POST /payment} operation.
 * {@code price} is in cents (see the "monetary amounts as integer cents" architecture decision).
 */
public class PaymentCreationRequest {

    private final String transactionId;
    private final int price;

    public PaymentCreationRequest(String transactionId, int price) {
        this.transactionId = transactionId;
        this.price = price;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getPrice() {
        return price;
    }
}
