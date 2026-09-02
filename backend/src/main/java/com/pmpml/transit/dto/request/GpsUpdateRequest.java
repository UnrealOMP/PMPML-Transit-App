package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GpsUpdateRequest {
    @NotNull private UUID busId;
    private UUID tripId;
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private BigDecimal latitude;
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private BigDecimal longitude;
    private BigDecimal speed;
    private BigDecimal heading;
    @NotNull @PastOrPresent private Instant timestamp;
}
