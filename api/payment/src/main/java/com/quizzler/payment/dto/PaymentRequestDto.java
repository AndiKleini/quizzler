package com.quizzler.payment.dto;

public class PaymentRequestDto {

    private String transactionId;
    private int price;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(String transactionId, int price) {
        this.transactionId = transactionId;
        this.price = price;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /** Price in cents. */
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
