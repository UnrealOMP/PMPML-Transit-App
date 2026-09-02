package com.pmpml.transit.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EtaResponse {
    private UUID busId; private UUID tripId; private UUID targetStopId;
    private Instant estimatedArrival; private int estimatedMinutes;
    private int stopsRemaining; private double distanceRemainingKm;
}
