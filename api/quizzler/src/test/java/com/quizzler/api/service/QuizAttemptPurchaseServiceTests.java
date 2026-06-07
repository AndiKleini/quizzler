package com.quizzler.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.quizzler.api.client.PaymentApiClient;
import com.quizzler.api.domain.QuizAttemptPurchase;
import com.quizzler.api.domain.QuizAttemptPurchaseConfirmation;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.dto.PaymentInitiationDto;
import com.quizzler.api.dto.QuizAttemptPurchaseConfirmationDto;
import com.quizzler.api.dto.QuizAttemptPurchaseDto;
import com.quizzler.api.repository.QuizAttemptPurchaseConfirmationRepository;
import com.quizzler.api.repository.QuizAttemptPurchaseRepository;
import com.quizzler.api.repository.QuizSessionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class QuizAttemptPurchaseServiceTests {

    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    private static final String OTHER_SESSION_PUBLIC_ID = "99999999-8888-7777-6666-555555555555";
    private static final String PURCHASE_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final String CONFIRMATION_PUBLIC_ID = "cccccccc-dddd-eeee-ffff-000000000000";
    private static final String PAYMENT_ID = "22222222-3333-4444-5555-666666666666";
    private static final String API_BASE_URL = "http://api.test";
    private static final String UI_BASE_URL = "http://ui.test";

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private QuizAttemptPurchaseRepository quizAttemptPurchaseRepository;

    @Mock
    private QuizAttemptPurchaseConfirmationRepository quizAttemptPurchaseConfirmationRepository;

    @Mock
    private PaymentApiClient paymentApiClient;

    private QuizAttemptPurchaseService quizAttemptPurchaseService;

    @BeforeEach
    void setUp() {
        quizAttemptPurchaseService = new QuizAttemptPurchaseService(
                quizSessionRepository, quizAttemptPurchaseRepository,
                quizAttemptPurchaseConfirmationRepository, paymentApiClient,
                API_BASE_URL, UI_BASE_URL);
    }

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

    @Test
    void initiatePayment_when_purchase_matches_session_creates_payment_with_callback_urls_and_returns_payment_id() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session);
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));
        String redirectUrl = UI_BASE_URL + "/quiz-session/" + SESSION_PUBLIC_ID
                + "/quiz-attempt-purchase-confirmed/?purchaseId=" + PURCHASE_PUBLIC_ID;
        String webhookSuccessUrl = API_BASE_URL + "/session/" + SESSION_PUBLIC_ID
                + "/quiz-attempt-purchase/" + PURCHASE_PUBLIC_ID + "/confirmation";
        String webhookCancelUrl = UI_BASE_URL + "/quiz-session/" + SESSION_PUBLIC_ID
                + "/quiz-attempt-purchase-failed/";
        when(paymentApiClient.createPayment(
                PURCHASE_PUBLIC_ID, 200, redirectUrl, webhookSuccessUrl, webhookCancelUrl)).thenReturn(PAYMENT_ID);

        PaymentInitiationDto dto = quizAttemptPurchaseService.initiatePayment(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID);

        assertThat(dto.getPaymentId()).isEqualTo(PAYMENT_ID);
    }

    @Test
    void initiatePayment_when_purchase_not_found_throws() {
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptPurchaseService.initiatePayment(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        verifyNoInteractions(paymentApiClient);
    }

    @Test
    void initiatePayment_when_purchase_belongs_to_other_session_throws() {
        QuizSession otherSession = new QuizSession(OTHER_SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, otherSession);
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> quizAttemptPurchaseService.initiatePayment(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
        verifyNoInteractions(paymentApiClient);
    }

    @Test
    void confirmPurchase_when_purchase_matches_session_persists_confirmation() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session);
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));
        when(quizAttemptPurchaseConfirmationRepository.saveAndFlush(any(QuizAttemptPurchaseConfirmation.class)))
                .thenAnswer(call -> call.getArgument(0));

        QuizAttemptPurchaseConfirmationDto dto =
                quizAttemptPurchaseService.confirmPurchase(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID);

        assertThat(dto.getPurchaseId()).isEqualTo(PURCHASE_PUBLIC_ID);
        assertThat(dto.getConfirmationId()).isNotBlank();
        assertThat(dto.getCreatedAt()).isNotNull();

        ArgumentCaptor<QuizAttemptPurchaseConfirmation> saved =
                ArgumentCaptor.forClass(QuizAttemptPurchaseConfirmation.class);
        verify(quizAttemptPurchaseConfirmationRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getPurchase()).isSameAs(purchase);
        assertThat(saved.getValue().getPublicId()).isEqualTo(dto.getConfirmationId());
    }

    @Test
    void confirmPurchase_when_purchase_not_found_throws() {
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptPurchaseService.confirmPurchase(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        verifyNoInteractions(quizAttemptPurchaseConfirmationRepository);
    }

    @Test
    void confirmPurchase_when_purchase_belongs_to_other_session_throws() {
        QuizSession otherSession = new QuizSession(OTHER_SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, otherSession);
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> quizAttemptPurchaseService.confirmPurchase(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
        verifyNoInteractions(quizAttemptPurchaseConfirmationRepository);
    }

    @Test
    void confirmPurchase_when_already_confirmed_throws_conflict() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session);
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));
        when(quizAttemptPurchaseConfirmationRepository.saveAndFlush(any(QuizAttemptPurchaseConfirmation.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThatThrownBy(() -> quizAttemptPurchaseService.confirmPurchase(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void getConfirmation_when_purchase_confirmed_returns_confirmation() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session);
        QuizAttemptPurchaseConfirmation confirmation =
                new QuizAttemptPurchaseConfirmation(CONFIRMATION_PUBLIC_ID, purchase, Instant.now());
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));
        when(quizAttemptPurchaseConfirmationRepository.findByPurchasePublicId(PURCHASE_PUBLIC_ID))
                .thenReturn(Optional.of(confirmation));

        QuizAttemptPurchaseConfirmationDto dto =
                quizAttemptPurchaseService.getConfirmation(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID);

        assertThat(dto).usingRecursiveComparison().isEqualTo(new QuizAttemptPurchaseConfirmationDto(
                CONFIRMATION_PUBLIC_ID, PURCHASE_PUBLIC_ID, confirmation.getCreatedAt()));
    }

    @Test
    void getConfirmation_when_not_confirmed_yet_throws_not_found() {
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session);
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));
        when(quizAttemptPurchaseConfirmationRepository.findByPurchasePublicId(PURCHASE_PUBLIC_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizAttemptPurchaseService.getConfirmation(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getConfirmation_when_purchase_belongs_to_other_session_throws() {
        QuizSession otherSession = new QuizSession(OTHER_SESSION_PUBLIC_ID, new QuizSpecification(List.of(42L)));
        QuizAttemptPurchase purchase = new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, otherSession);
        when(quizAttemptPurchaseRepository.findByPublicId(PURCHASE_PUBLIC_ID)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> quizAttemptPurchaseService.getConfirmation(SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
        verifyNoInteractions(quizAttemptPurchaseConfirmationRepository);
    }
}
