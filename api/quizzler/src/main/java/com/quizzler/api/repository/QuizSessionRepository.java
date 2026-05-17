package com.quizzler.api.repository;

import java.util.Optional;

import com.quizzler.api.domain.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {

    Optional<QuizSession> findByPublicId(String publicId);
}
