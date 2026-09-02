package com.pmpml.transit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "route_stops", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"route_id", "stop_id"}),
    @UniqueConstraint(columnNames = {"route_id", "sequence_order"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteStop extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "route_id", nullable = false) private Route route;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "stop_id", nullable = false) private Stop stop;
    @Column(nullable = false) private Integer sequenceOrder;
    @Column(precision = 10, scale = 2) private BigDecimal distanceFromStartKm;
    @Column(precision = 10, scale = 2) private BigDecimal distanceToNextStopKm;
    @Column(nullable = false) @Builder.Default private Boolean isFirstStop = false;
    @Column(nullable = false) @Builder.Default private Boolean isLastStop = false;
}
