package com.pmpml.transit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "ticket_verifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketVerification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ticket_id", nullable = false) private Ticket ticket;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "verifier_id", nullable = false) private User verifier;
    @Column(nullable = false, length = 20) private String result;
    @Column(length = 500) private String notes;
    @Column(nullable = false) private Instant verifiedAt;
}
