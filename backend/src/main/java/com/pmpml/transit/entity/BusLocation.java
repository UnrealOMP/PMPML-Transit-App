package com.pmpml.transit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "bus_locations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusLocation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "bus_id", nullable = false) private Bus bus;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "trip_id") private Trip trip;
    @Column(nullable = false, precision = 10, scale = 7) private BigDecimal latitude;
    @Column(nullable = false, precision = 10, scale = 7) private BigDecimal longitude;
    @Column(precision = 6, scale = 2) private BigDecimal speed;
    @Column(precision = 5, scale = 2) private BigDecimal heading;
    @Column(nullable = false) private Instant recordedAt;
    @Column(nullable = false) @Builder.Default private Boolean stale = false;
}
