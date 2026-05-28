package com.quizzler.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.dto.QuizSessionDto;
import com.quizzler.api.repository.QuizSessionRepository;
import com.quizzler.api.repository.QuizSpecificationRepository;

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
    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    private static final long SEEDED_QUESTION_ID = 99L;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    @Autowired
    private QuizSpecificationRepository quizSpecificationRepository;

    private QuizSpecification seededSpecification;

    @BeforeEach
    void seedTestData() {
        quizSessionRepository.deleteAll();
        quizSpecificationRepository.deleteAll();
        seededSpecification = quizSpecificationRepository.save(
                new QuizSpecification(List.of(SEEDED_QUESTION_ID)));
    }

    @Test
    public void createSession_returns_dto_with_generated_public_id(@Autowired WebTestClient webTestClient) {
        webTestClient.post().uri(SESSION).exchange()
                .expectStatus().isCreated()
                .expectBody(QuizSessionDto.class)
                .value(dto -> assertThat(dto.getPublicId()).isNotBlank());
    }

    @Test
    public void createSession_when_no_specification_exists_returns_409(@Autowired WebTestClient webTestClient) {
        quizSpecificationRepository.deleteAll();

        webTestClient.post().uri(SESSION).exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void getSession_when_exists_returns_dto_without_specification(@Autowired WebTestClient webTestClient) {
        quizSessionRepository.save(new QuizSession(SESSION_PUBLIC_ID, seededSpecification));
        QuizSessionDto expected = new QuizSessionDto(SESSION_PUBLIC_ID);

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
