package com.quizzler.payment.dto;

public class PaymentRequestDto {

    private String transactionId;
    private int price;
    private String redirectUrl;
    private String webhookSuccessUrl;
    private String webhookCancelUrl;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(
        String transactionId, 
        int price, 
        String redirectUrl, 
        String webhookSuccessUrl, 
        String webhookCancelUrl) {
            this.transactionId = transactionId;
            this.price = price;
            this.redirectUrl = redirectUrl;
            this.webhookSuccessUrl = webhookSuccessUrl;
            this.webhookCancelUrl = webhookCancelUrl;    
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

    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    public String getWebhookSuccessUrl() {
        return this.webhookSuccessUrl;
    }

    public String getWebhookCancelUrl() {
        return this.webhookCancelUrl;
    }
}
