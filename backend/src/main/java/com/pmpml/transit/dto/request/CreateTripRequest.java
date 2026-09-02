package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateTripRequest {
    @NotNull private UUID busId;
    @NotNull private UUID routeId;
    @NotNull private Instant scheduledDeparture;
    @NotNull private Instant scheduledArrival;
    @NotNull @Min(0) private Integer availableSeats;
}
