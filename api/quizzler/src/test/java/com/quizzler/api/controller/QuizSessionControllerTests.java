package com.quizzler.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.QuizSessionDto;
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
public class QuizSessionControllerTests {

    private static final String SESSION = "/session";
    private static final String QUESTION_TITLE = "Layered architecture question";
    private static final String QUESTION_TEXT = "Which quality is best improved by a layered architecture?";
    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    private Long seededQuestionId;

    @BeforeEach
    void seedTestData() {
        quizSessionRepository.deleteAll();
        questionRepository.deleteAll();

        SinglePickQuestion question = new SinglePickQuestion(QUESTION_TITLE, QUESTION_TEXT);
        seededQuestionId = questionRepository.save(question).getId();
    }

    @Test
    public void createSession_assigns_the_only_question_as_current(@Autowired WebTestClient webTestClient) {
        QuizSessionDto expected = new QuizSessionDto(null, seededQuestionId, 0L, 0L);

        webTestClient.post().uri(SESSION).exchange()
                .expectStatus().isCreated()
                .expectBody(QuizSessionDto.class)
                .value(dto -> {
                    assertThat(dto.getPublicId()).isNotBlank();
                    assertThat(dto).usingRecursiveComparison()
                            .ignoringFields("publicId")
                            .isEqualTo(expected);
                });
    }

    @Test
    public void createSession_when_no_question_exists_returns_409(@Autowired WebTestClient webTestClient) {
        questionRepository.deleteAll();

        webTestClient.post().uri(SESSION).exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void getSession_when_exists_returns_expected_dto(@Autowired WebTestClient webTestClient) {
        quizSessionRepository.save(new QuizSession(SESSION_PUBLIC_ID, seededQuestionId, 0L, 0L));
        QuizSessionDto expected = new QuizSessionDto(SESSION_PUBLIC_ID, seededQuestionId, 0L, 0L);

        webTestClient.get().uri(SESSION + "/{publicId}", SESSION_PUBLIC_ID).exchange()
                .expectStatus().isOk()
                .expectBody(QuizSessionDto.class)
                .value(dto -> assertThat(dto).usingRecursiveComparison().isEqualTo(expected));
    }

    @Test
    public void getSession_when_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        webTestClient.get().uri(SESSION + "/{publicId}", SESSION_PUBLIC_ID).exchange()
                .expectStatus().isNotFound();
    }
}
