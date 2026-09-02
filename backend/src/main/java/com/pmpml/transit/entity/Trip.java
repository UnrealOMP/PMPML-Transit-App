package com.pmpml.transit.entity;

import com.pmpml.transit.enums.TripStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "trips")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "bus_id", nullable = false) private Bus bus;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "route_id", nullable = false) private Route route;
    @Column(nullable = false) private Instant scheduledDeparture;
    @Column(nullable = false) private Instant scheduledArrival;
    private Instant actualDeparture;
    private Instant actualArrival;
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default private TripStatus status = TripStatus.SCHEDULED;
    @Column(nullable = false) @Builder.Default private Integer availableSeats = 0;
    @Column(nullable = false) @Builder.Default private Boolean delayed = false;
}
