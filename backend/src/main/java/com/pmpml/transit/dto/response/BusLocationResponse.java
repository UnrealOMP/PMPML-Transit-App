package com.pmpml.transit.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusLocationResponse {
    private UUID busId; private String busNumber; private UUID tripId; private UUID routeId;
    private BigDecimal latitude; private BigDecimal longitude;
    private BigDecimal speed; private BigDecimal heading; private Instant timestamp;
}
