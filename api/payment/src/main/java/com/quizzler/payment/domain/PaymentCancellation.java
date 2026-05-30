package com.quizzler.payment.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Immutable record that a payment was cancelled. Insert-only; its existence is what makes the
 * referenced {@link Payment} "cancelled".
 */
@Entity
@Table(name = "payment_cancellation")
public class PaymentCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private String publicId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false, unique = true, updatable = false)
    private Payment payment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PaymentCancellation() {
    }

    public PaymentCancellation(String publicId, Payment payment, Instant createdAt) {
        this.publicId = publicId;
        this.payment = payment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Payment getPayment() {
        return payment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
