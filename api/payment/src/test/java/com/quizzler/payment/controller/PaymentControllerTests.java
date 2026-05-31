package com.quizzler.payment.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.quizzler.payment.dto.PaymentDto;
import com.quizzler.payment.dto.PaymentRequestDto;
import com.quizzler.payment.repository.PaymentCancellationRepository;
import com.quizzler.payment.repository.PaymentConfirmationRepository;
import com.quizzler.payment.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PaymentControllerTests {

    private static final String HTTP_EXAMPLE_COM_REDIRECT = "http://example.com/redirect";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS = "http://example.com/webhook/success";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_CANCEL = "http://example.com/webhook/cancel";  
    private static final String PAYMENT_URI = "/payment";
    private static final String TRANSACTION_ID = "txn-12345";
    private static final int PRICE = 1999;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentConfirmationRepository paymentConfirmationRepository;

    @Autowired
    private PaymentCancellationRepository paymentCancellationRepository;

    @BeforeEach
    void clearData() {
        paymentConfirmationRepository.deleteAll();
        paymentCancellationRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    public void createPayment_returns_payment_with_transaction_id_and_insertion_timestamp(@Autowired WebTestClient webTestClient) {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);

        webTestClient.post().uri(PAYMENT_URI)
                .bodyValue(
                    new PaymentRequestDto(
                        TRANSACTION_ID, PRICE, 
                        HTTP_EXAMPLE_COM_REDIRECT, 
                        HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS, 
                        HTTP_EXAMPLE_COM_WEBHOOK_CANCEL))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PaymentDto.class)
                .value(dto -> {
                    assertThat(dto).usingRecursiveComparison().
                        ignoringFields("paymentId", "createdAt").
                        isEqualTo(new PaymentDto(null, TRANSACTION_ID, PRICE, null));
                    assertThat(dto.getPaymentId()).isNotBlank();
                    assertThat(dto.getCreatedAt()).isAfterOrEqualTo(before);
                });
    }
}
