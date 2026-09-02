package com.pmpml.transit.repository;

import com.pmpml.transit.entity.TicketVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TicketVerificationRepository extends JpaRepository<TicketVerification, UUID> {
    List<TicketVerification> findByTicketId(UUID ticketId);
}
