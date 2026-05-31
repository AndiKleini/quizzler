package com.quizzler.api.repository;

import java.util.Optional;

import com.quizzler.api.domain.QuizAttemptPurchaseConfirmation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptPurchaseConfirmationRepository
        extends JpaRepository<QuizAttemptPurchaseConfirmation, Long> {

        boolean existsByPurchasePublicId(String uniquePurchaseIdForTheScopeOfTheTest);

        Optional<QuizAttemptPurchaseConfirmation> findByPurchasePublicId(String purchasePublicId);
}
