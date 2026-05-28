package com.quizzler.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.quizzler.api.domain.Answer;
import com.quizzler.api.domain.QuizAttempt;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.AnswerDto;
import com.quizzler.api.dto.AnswerSubmissionDto;
import com.quizzler.api.repository.AnswerRepository;
import com.quizzler.api.repository.QuestionRepository;
import com.quizzler.api.repository.QuizAttemptRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTests {

    private static final String ATTEMPT_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    private static final String SESSION_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final long ANSWER_ID = 7L;
    private static final long QUESTION_ID = 42L;
    private static final long SELECTED_OPTION_ID = 3L;
    private static final long CORRECT_OPTION_ID = 2L;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AnswerService answerService;

    @Test
    void submitAnswer_persists_new_answer_and_returns_correct_option() {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
        AnswerDto expected = new AnswerDto(
                ANSWER_ID, ATTEMPT_PUBLIC_ID, QUESTION_ID, SELECTED_OPTION_ID, CORRECT_OPTION_ID, before);
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, QUESTION_ID, 0L, 0L);
        QuizAttempt attempt = new QuizAttempt(ATTEMPT_PUBLIC_ID, session, QUESTION_ID);
        SinglePickQuestion question = new SinglePickQuestion("Title", "Text");
        question.setCorrectOptionId(CORRECT_OPTION_ID);
        when(quizAttemptRepository.findByPublicId(ATTEMPT_PUBLIC_ID)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenAnswer(call -> {
            Answer saved = call.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", ANSWER_ID);
            return saved;
        });

        AnswerDto dto = answerService.submitAnswer(
                ATTEMPT_PUBLIC_ID,
                new AnswerSubmissionDto(QUESTION_ID, SELECTED_OPTION_ID));

        assertThat(dto).
            usingRecursiveComparison().
            ignoringFields("submittedAt").
            isEqualTo(expected);
        assertThat(dto.getSubmittedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void submitAnswer_when_attempt_not_found_throws() {
        when(quizAttemptRepository.findByPublicId(ATTEMPT_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.submitAnswer(
                ATTEMPT_PUBLIC_ID,
                new AnswerSubmissionDto(QUESTION_ID, SELECTED_OPTION_ID)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void submitAnswer_when_question_not_found_throws() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, QUESTION_ID, 0L, 0L);
        QuizAttempt attempt = new QuizAttempt(ATTEMPT_PUBLIC_ID, session, QUESTION_ID);
        when(quizAttemptRepository.findByPublicId(ATTEMPT_PUBLIC_ID)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.submitAnswer(
                ATTEMPT_PUBLIC_ID,
                new AnswerSubmissionDto(QUESTION_ID, SELECTED_OPTION_ID)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
