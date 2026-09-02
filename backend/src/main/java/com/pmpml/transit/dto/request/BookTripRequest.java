package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BookTripRequest {
    @NotNull private UUID tripId;
    @NotNull private UUID sourceStopId;
    @NotNull private UUID destinationStopId;
}
