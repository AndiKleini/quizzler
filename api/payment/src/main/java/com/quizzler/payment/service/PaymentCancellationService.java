package com.quizzler.payment.service;

import java.time.Instant;
import java.util.UUID;

import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.domain.PaymentCancellation;
import com.quizzler.payment.dto.PaymentCancellationDto;
import com.quizzler.payment.repository.PaymentCancellationRepository;
import com.quizzler.payment.repository.PaymentConfirmationRepository;
import com.quizzler.payment.repository.PaymentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentCancellationService {

    private final PaymentRepository paymentRepository;
    private final PaymentConfirmationRepository paymentConfirmationRepository;
    private final PaymentCancellationRepository paymentCancellationRepository;

    public PaymentCancellationService(PaymentRepository paymentRepository,
                                      PaymentConfirmationRepository paymentConfirmationRepository,
                                      PaymentCancellationRepository paymentCancellationRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentConfirmationRepository = paymentConfirmationRepository;
        this.paymentCancellationRepository = paymentCancellationRepository;
    }

    @Transactional
    public PaymentCancellationDto cancelPayment(String paymentId) {
        Payment payment = paymentRepository.findByPublicId(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Payment " + paymentId + " not found"));
        if (paymentConfirmationRepository.existsByPayment(payment)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Payment " + paymentId + " is already confirmed");
        }

        // A duplicate cancellation is rejected by the unique payment_id constraint at insert time,
        // so we let the database enforce it rather than issuing an extra existence query.
        try {
            PaymentCancellation saved = paymentCancellationRepository.saveAndFlush(
                    new PaymentCancellation(UUID.randomUUID().toString(), payment, Instant.now()));
            return new PaymentCancellationDto(saved.getPublicId(), payment.getPublicId(), saved.getCreatedAt());
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Payment " + paymentId + " is already cancelled", ex);
        }
    }
}
