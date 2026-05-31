package com.quizzler.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.dto.PaymentDetailDto;
import com.quizzler.payment.dto.PaymentDto;
import com.quizzler.payment.dto.PaymentRequestDto;
import com.quizzler.payment.repository.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {

    private static final String HTTP_EXAMPLE_COM_REDIRECT = "http://example.com/redirect";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS = "http://example.com/webhook/success";
    private static final String HTTP_EXAMPLE_COM_WEBHOOK_CANCEL = "http://example.com/webhook/cancel";
    private static final String TRANSACTION_ID = "txn-12345";
    private static final int PRICE = 1999;
    private static final String PAYMENT_PUBLIC_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_persists_new_payment_with_transaction_id_price_and_insertion_timestamp() {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(call -> call.getArgument(0));
        PaymentDto dto = paymentService.createPayment(
            new PaymentRequestDto(
                TRANSACTION_ID, 
                PRICE, 
                HTTP_EXAMPLE_COM_REDIRECT, 
                HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS, 
                HTTP_EXAMPLE_COM_WEBHOOK_CANCEL));

        PaymentDto expected = new PaymentDto(dto.getPaymentId(), TRANSACTION_ID, PRICE, dto.getCreatedAt());

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
        assertThat(dto.getPaymentId()).isNotBlank();
        assertThat(dto.getCreatedAt()).isAfterOrEqualTo(before);

        ArgumentCaptor<Payment> saved = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(saved.capture());
        assertThat(saved.getValue().getTransactionId()).isEqualTo(TRANSACTION_ID);
        assertThat(saved.getValue().getPrice()).isEqualTo(PRICE);
        assertThat(saved.getValue().getPublicId()).isEqualTo(dto.getPaymentId());
    }

    @Test
    void createPayment_persists_new_payment_with_request_data() {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(call -> call.getArgument(0));
        
        PaymentDto dto = paymentService.createPayment(
            new PaymentRequestDto(
                TRANSACTION_ID, 
                PRICE, 
                HTTP_EXAMPLE_COM_REDIRECT, 
                HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS, 
                HTTP_EXAMPLE_COM_WEBHOOK_CANCEL));

        PaymentDto expected = new PaymentDto(dto.getPaymentId(), TRANSACTION_ID, PRICE, dto.getCreatedAt());
        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
        assertThat(dto.getPaymentId()).isNotBlank();
        assertThat(dto.getCreatedAt()).isAfterOrEqualTo(before);
        Payment expectedToBeSaved = new Payment(
            dto.getPaymentId(), 
            TRANSACTION_ID, 
            PRICE,
            null,
            HTTP_EXAMPLE_COM_REDIRECT,
            HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS,
            HTTP_EXAMPLE_COM_WEBHOOK_CANCEL);
        ArgumentCaptor<Payment> saved = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(saved.capture());
        assertThat(saved.getValue()).
        usingRecursiveComparison().
            ignoringFields( "createdAt").isEqualTo(
                expectedToBeSaved);
        assertThat(saved.getValue().getCreatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void getPayment_when_payment_exists_returns_price_and_redirect_url() {
        Payment payment = new Payment(
                PAYMENT_PUBLIC_ID,
                TRANSACTION_ID,
                PRICE,
                Instant.now(),
                HTTP_EXAMPLE_COM_REDIRECT,
                HTTP_EXAMPLE_COM_WEBHOOK_SUCCESS,
                HTTP_EXAMPLE_COM_WEBHOOK_CANCEL);
        when(paymentRepository.findByPublicId(PAYMENT_PUBLIC_ID)).thenReturn(Optional.of(payment));

        PaymentDetailDto dto = paymentService.getPayment(PAYMENT_PUBLIC_ID);

        assertThat(dto).usingRecursiveComparison()
                .isEqualTo(new PaymentDetailDto(PRICE, HTTP_EXAMPLE_COM_REDIRECT));
    }

    @Test
    void getPayment_when_payment_not_found_throws() {
        when(paymentRepository.findByPublicId(PAYMENT_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(PAYMENT_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
