package com.quizzler.api.repository;

import java.util.Optional;

import com.quizzler.api.domain.QuizAttemptPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptPurchaseRepository extends JpaRepository<QuizAttemptPurchase, Long> {

    Optional<QuizAttemptPurchase> findByPublicId(String publicId);
}
