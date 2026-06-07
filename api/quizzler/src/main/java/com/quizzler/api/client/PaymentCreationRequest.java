package com.quizzler.api.client;

/**
 * Request body sent to the payment API's {@code POST /payment} operation.
 * {@code price} is in cents (see the "monetary amounts as integer cents" architecture decision).
 * The URLs tell the payment provider where to send the user back to ({@code redirectUrl}) and
 * which webhooks to call on a settled ({@code webhookSuccessUrl}) or cancelled
 * ({@code webhookCancelUrl}) payment.
 */
public class PaymentCreationRequest {

    private final String transactionId;
    private final int price;
    private final String productId;
    private final String redirectUrl;
    private final String webhookSuccessUrl;
    private final String webhookCancelUrl;

    public PaymentCreationRequest(String transactionId,
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
 
    public String getProductId() {
        return productId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }

    public int getPrice() {
        return price;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getWebhookSuccessUrl() {
        return webhookSuccessUrl;
    }

    public String getWebhookCancelUrl() {
        return webhookCancelUrl;
    }
}
