package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Booking;
import com.pmpml.transit.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByBookingReference(String bookingReference);
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);
    Page<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
}
