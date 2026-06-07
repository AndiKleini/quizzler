package com.quizzler.payment.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.quizzler.payment.domain.Payment;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PaymentControllerTests {

    private static final String PRODUCT_ID = "1111-5555-6666";
    private static final String HTTP_EXAMPLE_COM_REDIRECT = "http://example.com/redirect";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS = "http://example.com/webhook/success";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_CANCEL = "http://example.com/webhook/cancel";  
    private static final String PAYMENT_URI = "/payment";
    private static final String PAYMENT_DETAIL_URI = "/payment/{paymentId}";
    private static final String PAYMENT_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
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
                        TRANSACTION_ID, 
                        PRICE, 
                        PRODUCT_ID,
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

    @Test
    public void getPayment_returns_only_price_and_redirect_url(@Autowired WebTestClient webTestClient) {
        paymentRepository.save(
                new Payment(
                        PAYMENT_PUBLIC_ID,
                        TRANSACTION_ID,
                        PRICE,
                        PRODUCT_ID,
                        Instant.now(),
                        HTTP_EXAMPLE_COM_REDIRECT,
                        HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS,
                        HTTP_EXAMPLE_COM_WEBHOOK_CANCEL));

        webTestClient.get().uri(PAYMENT_DETAIL_URI, PAYMENT_PUBLIC_ID).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(PRICE)
                .jsonPath("$.redirectUrl").isEqualTo(HTTP_EXAMPLE_COM_REDIRECT)
                .jsonPath("$.transactionId").doesNotExist()
                .jsonPath("$.paymentId").doesNotExist()
                .jsonPath("$.webhookSuccessUrl").doesNotExist()
                .jsonPath("$.webhookCancelUrl").doesNotExist()
                .jsonPath("$.createdAt").doesNotExist();
    }

    @Test
    public void getPayment_when_payment_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        webTestClient.get().uri(PAYMENT_DETAIL_URI, PAYMENT_PUBLIC_ID).exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }
}
