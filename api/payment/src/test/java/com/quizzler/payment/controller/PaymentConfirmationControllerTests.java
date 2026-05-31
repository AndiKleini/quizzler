package com.quizzler.payment.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.quizzler.payment.client.ConfirmationWebhookClient;
import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.domain.PaymentCancellation;
import com.quizzler.payment.domain.PaymentConfirmation;
import com.quizzler.payment.dto.PaymentConfirmationDto;
import com.quizzler.payment.repository.PaymentCancellationRepository;
import com.quizzler.payment.repository.PaymentConfirmationRepository;
import com.quizzler.payment.repository.PaymentRepository;

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
public class PaymentConfirmationControllerTests {

    private static final String HTTP_EXAMPLE_COM_REDIRECT = "http://example.com/redirect";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS = "http://example.com/webhook/success";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_CANCEL = "http://example.com/webhook/cancel";  
    private static final String CONFIRMATION_URI = "/payment/{paymentId}/confirmation";
    private static final String PAYMENT_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final String TRANSACTION_ID = "txn-12345";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentConfirmationRepository paymentConfirmationRepository;

    @Autowired
    private PaymentCancellationRepository paymentCancellationRepository;

    @MockBean
    private ConfirmationWebhookClient confirmationWebhookClient;

    @BeforeEach
    void clearData() {
        paymentConfirmationRepository.deleteAll();
        paymentCancellationRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    public void confirmPayment_inserts_confirmation_for_payment(@Autowired WebTestClient webTestClient) {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
        paymentRepository.save(
            new Payment(
                PAYMENT_PUBLIC_ID, 
                TRANSACTION_ID,
                 1999, 
                 Instant.now(), 
                 HTTP_EXAMPLE_COM_REDIRECT,
                 HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS,
                 HTTP_EXAMPLE_COM_WEBHOOK_CANCEL));

        webTestClient.post().uri(CONFIRMATION_URI, PAYMENT_PUBLIC_ID).exchange()
                .expectStatus().isCreated()
                .expectBody(PaymentConfirmationDto.class)
                .value(dto -> {
                    assertThat(dto.getConfirmationId()).isNotBlank();
                    assertThat(dto.getPaymentId()).isEqualTo(PAYMENT_PUBLIC_ID);
                    assertThat(dto.getCreatedAt()).isAfterOrEqualTo(before);
                });

        assertThat(paymentConfirmationRepository.count()).isEqualTo(1);
    }

    @Test
    public void confirmPayment_when_payment_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        webTestClient.post().uri(CONFIRMATION_URI, PAYMENT_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void confirmPayment_when_payment_already_confirmed_returns_409(@Autowired WebTestClient webTestClient) {
        Payment payment = paymentRepository.save(
            new Payment(
                PAYMENT_PUBLIC_ID, 
                TRANSACTION_ID,
                 1999, 
                 Instant.now(), 
                 HTTP_EXAMPLE_COM_REDIRECT,
                 HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS,
                 HTTP_EXAMPLE_COM_WEBHOOK_CANCEL));

        paymentConfirmationRepository.save(
                new PaymentConfirmation(UUID.randomUUID().toString(), payment, Instant.now()));

        webTestClient.post().uri(CONFIRMATION_URI, PAYMENT_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void confirmPayment_when_payment_already_cancelled_returns_409(@Autowired WebTestClient webTestClient) {
        Payment payment = paymentRepository.save(
            new Payment(
                PAYMENT_PUBLIC_ID, 
                TRANSACTION_ID,
                 1999, 
                 Instant.now(), 
                 HTTP_EXAMPLE_COM_REDIRECT,
                 HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS,
                 HTTP_EXAMPLE_COM_WEBHOOK_CANCEL));
        paymentCancellationRepository.save(
                new PaymentCancellation(UUID.randomUUID().toString(), payment, Instant.now()));

        webTestClient.post().uri(CONFIRMATION_URI, PAYMENT_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }
}
