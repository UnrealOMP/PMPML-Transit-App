package com.pmpml.transit.entity;

import com.pmpml.transit.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "fare_rules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FareRule extends BaseEntity {
    @Enumerated(EnumType.STRING) @Column(nullable = false) private VehicleType vehicleType;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal minimumFare;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal distanceFromKm;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal distanceToKm;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal fare;
    @Column(nullable = false) @Builder.Default private Boolean active = true;
    private Instant effectiveFrom;
    private Instant effectiveTo;
    @Column(nullable = false) @Builder.Default private Integer versionNumber = 1;
}
