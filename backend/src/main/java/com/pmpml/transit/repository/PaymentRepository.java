package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Payment;
import com.pmpml.transit.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    Optional<Payment> findByBookingId(UUID bookingId);
    List<Payment> findByStatusIn(List<PaymentStatus> statuses);
}
