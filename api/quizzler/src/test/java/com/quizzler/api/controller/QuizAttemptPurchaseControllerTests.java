package com.quizzler.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import com.quizzler.api.client.PaymentApiClient;
import com.quizzler.api.domain.QuizAttemptPurchase;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.QuizAttemptPurchaseDto;
import com.quizzler.api.repository.QuestionRepository;
import com.quizzler.api.repository.QuizAttemptPurchaseRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import com.quizzler.api.repository.QuizSpecificationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class QuizAttemptPurchaseControllerTests {

    private static final String PURCHASE_URI = "/session/{publicId}/quiz-attempt-purchase";
    private static final String PAYMENT_URI = "/session/{publicId}/quiz-attempt-purchase/{purchaseId}/payment";
    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    private static final String PURCHASE_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final String PAYMENT_ID = "22222222-3333-4444-5555-666666666666";

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizSpecificationRepository quizSpecificationRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    @Autowired
    private QuizAttemptPurchaseRepository quizAttemptPurchaseRepository;

    @MockBean
    private PaymentApiClient paymentApiClient;

    private QuizSpecification seededSpecification;

    @BeforeEach
    void seedTestData() {
        quizAttemptPurchaseRepository.deleteAll();
        quizSessionRepository.deleteAll();
        quizSpecificationRepository.deleteAll();
        questionRepository.deleteAll();

        Long questionId = questionRepository.save(new SinglePickQuestion("Title", "Text")).getId();
        seededSpecification = quizSpecificationRepository.save(new QuizSpecification(List.of(questionId)));
    }

    @Test
    public void createPurchase_returns_purchase_for_session(@Autowired WebTestClient webTestClient) {
        quizSessionRepository.save(new QuizSession(SESSION_PUBLIC_ID, seededSpecification));

        webTestClient.post().uri(PURCHASE_URI, SESSION_PUBLIC_ID).exchange()
                .expectStatus().isCreated()
                .expectBody(QuizAttemptPurchaseDto.class)
                .value(dto -> {
                    assertThat(dto.getPurchaseId()).isNotBlank();
                    assertThat(dto).
                        usingRecursiveComparison().
                        ignoringFields("purchaseId").
                        isEqualTo(new QuizAttemptPurchaseDto(dto.getPurchaseId(), SESSION_PUBLIC_ID, 200));
                });
    }

    @Test
    public void createPurchase_when_session_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        webTestClient.post().uri(PURCHASE_URI, SESSION_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void initiatePayment_creates_payment_for_purchase_and_returns_payment_id(@Autowired WebTestClient webTestClient) {
        QuizSession session = quizSessionRepository.save(new QuizSession(SESSION_PUBLIC_ID, seededSpecification));
        quizAttemptPurchaseRepository.save(new QuizAttemptPurchase(PURCHASE_PUBLIC_ID, session));
        when(paymentApiClient.createPayment(eq(PURCHASE_PUBLIC_ID), eq(200), anyString(), anyString(), anyString()))
                .thenReturn(PAYMENT_ID);

        webTestClient.post().uri(PAYMENT_URI, SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.paymentId").isEqualTo(PAYMENT_ID);
    }

    @Test
    public void initiatePayment_when_purchase_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        quizSessionRepository.save(new QuizSession(SESSION_PUBLIC_ID, seededSpecification));

        webTestClient.post().uri(PAYMENT_URI, SESSION_PUBLIC_ID, PURCHASE_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }
}
