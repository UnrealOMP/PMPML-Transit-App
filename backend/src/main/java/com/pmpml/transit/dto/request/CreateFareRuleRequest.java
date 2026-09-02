package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import com.pmpml.transit.enums.VehicleType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateFareRuleRequest {
    @NotNull private VehicleType vehicleType;
    @NotNull private BigDecimal minimumFare;
    @NotNull private BigDecimal distanceFromKm;
    @NotNull private BigDecimal distanceToKm;
    @NotNull private BigDecimal fare;
    private Instant effectiveFrom;
    private Instant effectiveTo;
}
