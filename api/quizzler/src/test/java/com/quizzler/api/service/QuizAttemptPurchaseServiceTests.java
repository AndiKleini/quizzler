package com.quizzler.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.quizzler.api.domain.QuizAttemptPurchase;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.dto.QuizAttemptPurchaseDto;
import com.quizzler.api.repository.QuizAttemptPurchaseRepository;
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
class QuizAttemptPurchaseServiceTests {

    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private QuizAttemptPurchaseRepository quizAttemptPurchaseRepository;

    @InjectMocks
    private QuizAttemptPurchaseService quizAttemptPurchaseService;

    @Test
    void createPurchase_persists_new_purchase_for_session() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.of(session));
        when(quizAttemptPurchaseRepository.save(any(QuizAttemptPurchase.class)))
                .thenAnswer(call -> call.getArgument(0));

        QuizAttemptPurchaseDto dto = quizAttemptPurchaseService.createPurchase(SESSION_PUBLIC_ID);

        assertThat(dto.getSessionId()).isEqualTo(SESSION_PUBLIC_ID);
        assertThat(dto.getPurchaseId()).isNotBlank();
        assertThat(dto.getPrice()).isEqualTo(200);

        ArgumentCaptor<QuizAttemptPurchase> saved = ArgumentCaptor.forClass(QuizAttemptPurchase.class);
        verify(quizAttemptPurchaseRepository).save(saved.capture());
        assertThat(saved.getValue().getSession()).isSameAs(session);
        assertThat(saved.getValue().getPublicId()).isEqualTo(dto.getPurchaseId());
    }

    @Test
    void createPurchase_when_session_not_found_throws() {
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptPurchaseService.createPurchase(SESSION_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
