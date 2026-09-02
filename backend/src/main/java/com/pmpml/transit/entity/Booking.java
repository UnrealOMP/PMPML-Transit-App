package com.pmpml.transit.entity;

import com.pmpml.transit.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50) private String bookingReference;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id", nullable = false) private Trip trip;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "source_stop_id", nullable = false) private Stop sourceStop;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "destination_stop_id", nullable = false) private Stop destinationStop;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal fareAmount;
    @Column(nullable = false, unique = true, length = 50) private String idempotencyKey;
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default private BookingStatus status = BookingStatus.PENDING_PAYMENT;
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private Payment payment;
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private Ticket ticket;
    private Integer seatNumber;
}
