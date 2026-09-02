package com.pmpml.transit.dto.response;

import com.pmpml.transit.enums.TripStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TripResponse {
    private UUID id; private UUID busId; private String busNumber; private UUID routeId; private String routeNumber;
    private Instant scheduledDeparture; private Instant scheduledArrival;
    private TripStatus status; private Integer availableSeats; private Boolean delayed;
}
