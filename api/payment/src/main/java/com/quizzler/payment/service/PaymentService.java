package com.quizzler.payment.service;

import java.time.Instant;
import java.util.UUID;

import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.dto.PaymentDto;
import com.quizzler.payment.dto.PaymentRequestDto;
import com.quizzler.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentDto createPayment(PaymentRequestDto request) {
        Payment payment = 
        new Payment(
            UUID.randomUUID().toString(), 
            request.getTransactionId(), 
            request.getPrice(), 
            Instant.now(),
            request.getRedirectUrl(),
            request.getWebhookSuccessUrl(),
            request.getWebhookCancelUrl());
        Payment saved = paymentRepository.save(payment);
        return new PaymentDto(
            saved.getPublicId(), 
            saved.getTransactionId(),
            saved.getPrice(), 
            saved.getCreatedAt());
    }
}
