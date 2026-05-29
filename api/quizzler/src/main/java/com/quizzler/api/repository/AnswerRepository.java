package com.quizzler.api.repository;

import java.util.List;

import com.quizzler.api.domain.Answer;
import com.quizzler.api.domain.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByAttempt(QuizAttempt attempt);
}
