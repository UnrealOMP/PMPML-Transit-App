package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Ticket;
import com.pmpml.transit.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    Optional<Ticket> findByVerificationToken(String verificationToken);
    Optional<Ticket> findByBookingId(UUID bookingId);
    Page<Ticket> findByUserIdOrderByIssuedAtDesc(UUID userId, Pageable pageable);
    Page<Ticket> findByUserIdAndStatusOrderByIssuedAtDesc(UUID userId, TicketStatus status, Pageable pageable);
}
