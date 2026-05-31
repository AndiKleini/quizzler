package com.quizzler.api.domain;

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
 * Immutable record that a {@link QuizAttemptPurchase} was confirmed by the payment provider.
 * Insert-only; its existence is what makes the referenced purchase "confirmed". The unique
 * {@code quiz_attempt_purchase_id} constraint guarantees at most one confirmation per purchase.
 */
@Entity
@Table(name = "quiz_attempt_purchase_confirmation")
public class QuizAttemptPurchaseConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private String publicId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_attempt_purchase_id", nullable = false, unique = true, updatable = false)
    private QuizAttemptPurchase purchase;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected QuizAttemptPurchaseConfirmation() {
    }

    public QuizAttemptPurchaseConfirmation(String publicId, QuizAttemptPurchase purchase, Instant createdAt) {
        this.publicId = publicId;
        this.purchase = purchase;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public QuizAttemptPurchase getPurchase() {
        return purchase;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
