package com.pmpml.transit.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RouteResponse {
    private UUID id; private String routeNumber; private String name; private String description; private Boolean active;
    private List<RouteStopEntry> stops;
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RouteStopEntry {
        private UUID stopId; private String stopName; private Integer sequenceOrder;
        private BigDecimal distanceFromStartKm; private BigDecimal distanceToNextStopKm;
    }
}
