package com.pmpml.transit.entity;

import com.pmpml.transit.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50) private String ticketNumber;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id", nullable = false) private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false) private Trip trip;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "bus_id", nullable = false) private Bus bus;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "route_id", nullable = false) private Route route;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "source_stop_id", nullable = false) private Stop sourceStop;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "destination_stop_id", nullable = false) private Stop destinationStop;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal fare;
    @Column(nullable = false) private Instant issuedAt;
    @Column(nullable = false) private Instant expiresAt;
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default private TicketStatus status = TicketStatus.ACTIVE;
    @Column(nullable = false, unique = true, length = 256) private String verificationToken;
}
