package com.quizzler.payment.repository;

import java.util.Optional;

import com.quizzler.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPublicId(String publicId);
}
