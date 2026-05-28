package com.quizzler.api.repository;

import java.util.Optional;

import com.quizzler.api.domain.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByPublicId(String publicId);
}
