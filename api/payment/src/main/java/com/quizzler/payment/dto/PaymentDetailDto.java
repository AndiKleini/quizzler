package com.quizzler.payment.dto;

/**
 * Read model for {@code GET /payment/{paymentId}}. Deliberately exposes only the data the payment
 * UI needs — the amount to show the user and the URL to return to after a settled payment — and
 * omits everything else stored on the payment (transaction id, webhook URLs, timestamps).
 */
public class PaymentDetailDto {

    private final int price;
    private final String redirectUrl;

    public PaymentDetailDto(int price, String redirectUrl) {
        this.price = price;
        this.redirectUrl = redirectUrl;
    }

    /** Price in cents. */
    public int getPrice() {
        return price;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
