package com.quizzler.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.quizzler.payment.client.ConfirmationWebhookClient;
import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.domain.PaymentConfirmation;
import com.quizzler.payment.dto.PaymentConfirmationDto;
import com.quizzler.payment.repository.PaymentCancellationRepository;
import com.quizzler.payment.repository.PaymentConfirmationRepository;
import com.quizzler.payment.repository.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PaymentConfirmationServiceTests {

    private static final String HTTP_EXAMPLE_COM_REDIRECT = "http://example.com/redirect";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS = "http://example.com/webhook/success";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_CANCEL = "http://example.com/webhook/cancel";  
    private static final String PAYMENT_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final String TRANSACTION_ID = "txn-12345";

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentConfirmationRepository paymentConfirmationRepository;

    @Mock
    private PaymentCancellationRepository paymentCancellationRepository;

    @Mock
    private ConfirmationWebhookClient confirmationWebhookClient;

    @InjectMocks
    private PaymentConfirmationService paymentConfirmationService;

    @Test
    void confirmPayment_inserts_confirmation_with_insertion_timestamp() {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
        Payment payment = new Payment(
                PAYMENT_PUBLIC_ID,
                TRANSACTION_ID,
                1999,
                Instant.now(),
                HTTP_EXAMPLE_COM_REDIRECT,
                HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS,
                HTTP_EXAMPLE_COM_WEBHOOK_CANCEL);
        when(paymentRepository.findByPublicId(PAYMENT_PUBLIC_ID)).thenReturn(Optional.of(payment));
        when(paymentCancellationRepository.existsByPayment(payment)).thenReturn(false);
        when(paymentConfirmationRepository.saveAndFlush(any(PaymentConfirmation.class)))
                .thenAnswer(call -> call.getArgument(0));

        PaymentConfirmationDto dto = paymentConfirmationService.confirmPayment(PAYMENT_PUBLIC_ID);

        PaymentConfirmationDto expected =
                new PaymentConfirmationDto(dto.getConfirmationId(), PAYMENT_PUBLIC_ID, dto.getCreatedAt());
        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
        assertThat(dto.getConfirmationId()).isNotBlank();
        assertThat(dto.getCreatedAt()).isAfterOrEqualTo(before);
        verify(confirmationWebhookClient).notifyConfirmation(HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS);
    }

    @Test
    void confirmPayment_when_payment_not_found_throws() {
        when(paymentRepository.findByPublicId(PAYMENT_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentConfirmationService.confirmPayment(PAYMENT_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void confirmPayment_when_payment_already_cancelled_throws() {
        Payment payment = new Payment(
                PAYMENT_PUBLIC_ID, 
                TRANSACTION_ID, 
                1999, Instant.now(), 
                HTTP_EXAMPLE_COM_REDIRECT, 
                HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS, 
                HTTP_EXAMPLE_COM_WEBHOOK_CANCEL);
        when(paymentRepository.findByPublicId(PAYMENT_PUBLIC_ID)).thenReturn(Optional.of(payment));
        when(paymentCancellationRepository.existsByPayment(payment)).thenReturn(true);

        assertThatThrownBy(() -> paymentConfirmationService.confirmPayment(PAYMENT_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void confirmPayment_when_confirmation_insert_violates_unique_constraint_throws_conflict() {
        Payment payment = 
                new Payment(
                        PAYMENT_PUBLIC_ID, 
                        TRANSACTION_ID, 
                        1999, 
                        Instant.now(), 
                        HTTP_EXAMPLE_COM_REDIRECT, 
                        HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS, 
                        HTTP_EXAMPLE_COM_WEBHOOK_CANCEL);
        when(paymentRepository.findByPublicId(PAYMENT_PUBLIC_ID)).thenReturn(Optional.of(payment));
        when(paymentCancellationRepository.existsByPayment(payment)).thenReturn(false);
        when(paymentConfirmationRepository.saveAndFlush(any(PaymentConfirmation.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate payment_id"));

        assertThatThrownBy(() -> paymentConfirmationService.confirmPayment(PAYMENT_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }
}
