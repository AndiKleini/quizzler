package com.quizzler.payment.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Creation record of a payment. Rows are insert-only — see the "mutationless payment schema"
 * architecture decision. A payment's status is derived from the presence of a
 * {@link PaymentConfirmation} or {@link PaymentCancellation}, never from a mutable field.
 */
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private String publicId;

    @Column(name = "transaction_id", nullable = false, updatable = false)
    private String transactionId;

    @Column(name = "price", nullable = false, updatable = false)
    private int price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "redirect_url", nullable = false, updatable = false)
    private String redirectUrl;

    @Column(name = "webhook_success_url", nullable = false, updatable = false)
    private String webhookSuccessUrl;

    @Column(name = "webhook_cancel_url", nullable = false, updatable = false)
    private String webhookCancelUrl;

    protected Payment() {
    }

    public Payment(
        String publicId, 
        String transactionId, 
        int price, 
        Instant createdAt, 
        String httpExampleComRedirect, 
        String httpExampleComWebhookSuccess, 
        String httpExampleComWebhookCancel) {
        this.publicId = publicId;
        this.transactionId = transactionId;
        this.price = price;
        this.createdAt = createdAt;
        this.redirectUrl = httpExampleComRedirect;
        this.webhookSuccessUrl = httpExampleComWebhookSuccess;
        this.webhookCancelUrl = httpExampleComWebhookCancel;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    /** Price in cents. */
    public int getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
