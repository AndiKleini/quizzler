package com.quizzler.api.repository;

import com.quizzler.api.domain.QuizSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSpecificationRepository extends JpaRepository<QuizSpecification, Long> {
}
