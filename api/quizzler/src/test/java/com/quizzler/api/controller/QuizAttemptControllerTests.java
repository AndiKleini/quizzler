package com.quizzler.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.quizzler.api.domain.Answer;
import com.quizzler.api.domain.QuizAttempt;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.AnswerDto;
import com.quizzler.api.dto.AnswerSubmissionDto;
import com.quizzler.api.dto.QuizAttemptDto;
import com.quizzler.api.repository.AnswerRepository;
import com.quizzler.api.repository.QuizAttemptRepository;
import com.quizzler.api.repository.QuestionRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import com.quizzler.api.service.QuizAttemptService;

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
    private static final String ANSWER_URI = "/session/{publicId}/attempt/{attemptPublicId}/answer";
    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    private static final String ATTEMPT_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final long SELECTED_OPTION_ID = 3L;
    private static final long CORRECT_OPTION_ID = 2L;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private Long seededQuestionId;

    @BeforeEach
    void seedTestData() {
        answerRepository.deleteAll();
        quizAttemptRepository.deleteAll();
        quizSessionRepository.deleteAll();
        questionRepository.deleteAll();

        SinglePickQuestion question = new SinglePickQuestion("Title", "Text");
        question.setCorrectOptionId(CORRECT_OPTION_ID);
        seededQuestionId = questionRepository.save(question).getId();
    }

    @Test
    public void createAttempt_returns_attempt_with_hardcoded_question(@Autowired WebTestClient webTestClient) {
        quizSessionRepository.save(new QuizSession(SESSION_PUBLIC_ID));

        webTestClient.post().uri(ATTEMPT_URI, SESSION_PUBLIC_ID).exchange()
                .expectStatus().isCreated()
                .expectBody(QuizAttemptDto.class)
                .value(dto -> {
                    assertThat(dto.getAttemptId()).isNotBlank();
                    assertThat(dto.getSessionId()).isEqualTo(SESSION_PUBLIC_ID);
                    assertThat(dto.getQuestionId()).isEqualTo(QuizAttemptService.HARDCODED_QUESTION_ID);
                });
    }

    @Test
    public void createAttempt_when_session_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        webTestClient.post().uri(ATTEMPT_URI, SESSION_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void submitAnswer_inserts_new_answer_for_attempt(@Autowired WebTestClient webTestClient) {
        QuizSession session = quizSessionRepository.save(
                new QuizSession(SESSION_PUBLIC_ID));
        quizAttemptRepository.save(new QuizAttempt(ATTEMPT_PUBLIC_ID, session, seededQuestionId));
        AnswerSubmissionDto submission = new AnswerSubmissionDto(seededQuestionId, SELECTED_OPTION_ID);
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);

        webTestClient.post().uri(ANSWER_URI, SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID)
                .bodyValue(submission)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AnswerDto.class)
                .value(dto -> {
                    AnswerDto expected = new AnswerDto(
                            dto.getId(), ATTEMPT_PUBLIC_ID, seededQuestionId, SELECTED_OPTION_ID, CORRECT_OPTION_ID, dto.getSubmittedAt());
                    assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
                    assertThat(dto.getId()).isNotNull();
                    assertThat(dto.getSubmittedAt()).isAfterOrEqualTo(before);
                });
    }

    @Test
    public void submitAnswer_when_called_twice_inserts_two_answers(@Autowired WebTestClient webTestClient) {
        QuizSession session = quizSessionRepository.save(
                new QuizSession(SESSION_PUBLIC_ID));
        quizAttemptRepository.save(new QuizAttempt(ATTEMPT_PUBLIC_ID, session, seededQuestionId));
        AnswerSubmissionDto first = new AnswerSubmissionDto(seededQuestionId, 1L);
        AnswerSubmissionDto second = new AnswerSubmissionDto(seededQuestionId, 2L);

        webTestClient.post().uri(ANSWER_URI, SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID)
                .bodyValue(first).exchange().expectStatus().isCreated();
        webTestClient.post().uri(ANSWER_URI, SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID)
                .bodyValue(second).exchange().expectStatus().isCreated();

        List<Answer> stored = answerRepository.findAll();
        assertThat(stored).hasSize(2);
        assertThat(stored).extracting(Answer::getSelectedOptionId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    public void submitAnswer_when_attempt_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        webTestClient.post().uri(ANSWER_URI, SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID)
                .bodyValue(new AnswerSubmissionDto(seededQuestionId, SELECTED_OPTION_ID))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }
}
