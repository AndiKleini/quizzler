package com.quizzler.payment.dto;

import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;

public class PaymentRequestDto {

    private String transactionId;
    private int price;
    private String redirectUrl;
    private String webhookSuccessUrl;
    private String webhookCancelUrl;
    private String productId;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(
        String transactionId, 
        int price, 
        String productId,
        String redirectUrl, 
        String webhookSuccessUrl, 
        String webhookCancelUrl) {
            this.transactionId = transactionId;
            this.price = price;
            this.productId = productId;
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
    
    public String getProductId() {
        return this.productId;
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
