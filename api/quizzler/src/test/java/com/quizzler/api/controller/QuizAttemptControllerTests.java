package com.quizzler.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.QuizAttemptDto;
import com.quizzler.api.repository.QuizAttemptRepository;
import com.quizzler.api.repository.QuestionRepository;
import com.quizzler.api.repository.QuizSessionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class QuizAttemptControllerTests {

    private static final String ATTEMPT_URI = "/session/{publicId}/attempt";
    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    private Long seededQuestionId;

    @BeforeEach
    void seedTestData() {
        quizAttemptRepository.deleteAll();
        quizSessionRepository.deleteAll();
        questionRepository.deleteAll();

        SinglePickQuestion question = new SinglePickQuestion("Title", "Text");
        seededQuestionId = questionRepository.save(question).getId();
    }

    @Test
    public void createAttempt_returns_attempt_with_current_question(@Autowired WebTestClient webTestClient) {
        quizSessionRepository.save(new QuizSession(SESSION_PUBLIC_ID, seededQuestionId, 0L, 0L));

        webTestClient.post().uri(ATTEMPT_URI, SESSION_PUBLIC_ID).exchange()
                .expectStatus().isCreated()
                .expectBody(QuizAttemptDto.class)
                .value(dto -> {
                    assertThat(dto.getAttemptId()).isNotBlank();
                    assertThat(dto.getSessionId()).isEqualTo(SESSION_PUBLIC_ID);
                    assertThat(dto.getQuestionId()).isEqualTo(seededQuestionId);
                });
    }

    @Test
    public void createAttempt_when_session_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        webTestClient.post().uri(ATTEMPT_URI, SESSION_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }
}
