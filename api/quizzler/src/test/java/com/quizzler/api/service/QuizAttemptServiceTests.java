package com.quizzler.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.quizzler.api.domain.QuizAttempt;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.QuizAttemptDto;
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

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @InjectMocks
    private QuizAttemptService quizAttemptService;

    @Test
    void createAttempt_persists_new_attempt_for_hardcoded_question() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID);
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.of(session));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(call -> call.getArgument(0));

        QuizAttemptDto dto = quizAttemptService.createAttempt(SESSION_PUBLIC_ID);

        QuizAttemptDto expected = new QuizAttemptDto(null, SESSION_PUBLIC_ID, QuizAttemptService.HARDCODED_QUESTION_ID);
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

        assertThatThrownBy(() -> quizAttemptService.createAttempt(SESSION_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
