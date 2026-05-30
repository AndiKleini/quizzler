package com.quizzler.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.quizzler.api.domain.Answer;
import com.quizzler.api.domain.QuizAttempt;
import com.quizzler.api.domain.QuizAttemptPurchase;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.dto.QuizAttemptDto;
import com.quizzler.api.repository.AnswerRepository;
import com.quizzler.api.repository.QuizAttemptPurchaseRepository;
import com.quizzler.api.repository.QuizAttemptRepository;
import com.quizzler.api.repository.QuizSessionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceTests {

    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    private static final String ATTEMPT_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final String PURCHASE_PUBLIC_ID = "99999999-8888-7777-6666-555555555555";
    private static final long FIRST_QUESTION_ID = 42L;
    private static final long SECOND_QUESTION_ID = 43L;
    private static final long THIRD_QUESTION_ID = 44L;

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuizAttemptPurchaseRepository quizAttemptPurchaseRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QuizAttemptService quizAttemptService;

    @Test
    void createAttempt_persists_new_attempt_with_first_question_of_specification() {
        QuizSpecification specification = new QuizSpecification(List.of(FIRST_QUESTION_ID, SECOND_QUESTION_ID));
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, specification);
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.of(session));
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID))
                .thenReturn(Optional.of(new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session)));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(call -> call.getArgument(0));

        QuizAttemptDto dto = quizAttemptService.createAttempt(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID);

        QuizAttemptDto expected = new QuizAttemptDto(null, SESSION_PUBLIC_ID, FIRST_QUESTION_ID, false);
        assertThat(dto)
                .usingRecursiveComparison()
                .ignoringFields("attemptId")
                .isEqualTo(expected);
        assertThat(dto.getAttemptId()).isNotBlank();

        ArgumentCaptor<QuizAttempt> saved = ArgumentCaptor.forClass(QuizAttempt.class);
        verify(quizAttemptRepository).save(saved.capture());
        assertThat(saved.getValue().getSession()).isSameAs(session);
    }

    @Test
    void createAttempt_when_session_not_found_throws() {
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptService.createAttempt(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createAttempt_when_purchase_not_found_throws() {
        QuizSpecification specification = new QuizSpecification(List.of(FIRST_QUESTION_ID));
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, specification);
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.of(session));
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptService.createAttempt(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createAttempt_when_purchase_belongs_to_other_session_throws() {
        QuizSpecification specification = new QuizSpecification(List.of(FIRST_QUESTION_ID));
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, specification);
        QuizSession otherSession = new QuizSession("other-session-id", specification);
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.of(session));
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID))
                .thenReturn(Optional.of(new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, otherSession)));

        assertThatThrownBy(() -> quizAttemptService.createAttempt(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void createAttempt_when_specification_has_no_questions_throws() {
        QuizSpecification empty = new QuizSpecification(List.of());
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, empty);
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.of(session));
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID))
                .thenReturn(Optional.of(new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session)));

        assertThatThrownBy(() -> quizAttemptService.createAttempt(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void getAttempt_returns_first_unanswered_question_in_order_from_specification() {
        List<Long> unorderedQuestionList = List.of(FIRST_QUESTION_ID, THIRD_QUESTION_ID, SECOND_QUESTION_ID);
        QuizSpecification specification = new QuizSpecification(unorderedQuestionList);
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, specification);
        QuizAttempt attempt = new QuizAttempt(ATTEMPT_PUBLIC_ID, session, FIRST_QUESTION_ID);
        when(quizAttemptRepository.findByPublicId(ATTEMPT_PUBLIC_ID)).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttempt(attempt))
                .thenReturn(List.of(new Answer(attempt, FIRST_QUESTION_ID, 1L, Instant.now())));

        QuizAttemptDto dto = quizAttemptService.getAttempt(SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID);

        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(new QuizAttemptDto(ATTEMPT_PUBLIC_ID, SESSION_PUBLIC_ID, SECOND_QUESTION_ID, false));
    }

    @Test
    void getAttempt_when_attempt_not_found_throws() {
        when(quizAttemptRepository.findByPublicId(ATTEMPT_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptService.getAttempt(SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getAttempt_when_attempt_does_not_belong_to_session_throws() {
        QuizSpecification specification = new QuizSpecification(List.of(FIRST_QUESTION_ID));
        QuizSession otherSession = new QuizSession("other-session-id", specification);
        QuizAttempt attempt = new QuizAttempt(ATTEMPT_PUBLIC_ID, otherSession, FIRST_QUESTION_ID);
        when(quizAttemptRepository.findByPublicId(ATTEMPT_PUBLIC_ID)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> quizAttemptService.getAttempt(SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getAttempt_when_all_questions_answered_returns_completed() {
        QuizSpecification specification = new QuizSpecification(List.of(FIRST_QUESTION_ID, SECOND_QUESTION_ID));
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, specification);
        QuizAttempt attempt = new QuizAttempt(ATTEMPT_PUBLIC_ID, session, FIRST_QUESTION_ID);
        QuizAttemptDto expected = new QuizAttemptDto(ATTEMPT_PUBLIC_ID, SESSION_PUBLIC_ID, 0L, true);
        when(quizAttemptRepository.findByPublicId(ATTEMPT_PUBLIC_ID)).thenReturn(Optional.of(attempt));
        when(answerRepository.findByAttempt(attempt)).thenReturn(List.of(
                new Answer(attempt, FIRST_QUESTION_ID, 1L, Instant.now()),
                new Answer(attempt, SECOND_QUESTION_ID, 1L, Instant.now())));

        QuizAttemptDto dto = quizAttemptService.getAttempt(SESSION_PUBLIC_ID, ATTEMPT_PUBLIC_ID);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }
}
