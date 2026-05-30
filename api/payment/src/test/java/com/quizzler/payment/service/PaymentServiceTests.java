package com.quizzler.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.dto.PaymentDto;
import com.quizzler.payment.repository.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {

    private static final String TRANSACTION_ID = "txn-12345";
    private static final int PRICE = 1999;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_persists_new_payment_with_transaction_id_price_and_insertion_timestamp() {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(call -> call.getArgument(0));
        PaymentDto dto = paymentService.createPayment(TRANSACTION_ID, PRICE);

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
}
