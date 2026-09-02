package com.pmpml.transit.dto.response;

import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FareResponse {
    private BigDecimal fare; private BigDecimal distanceKm;
    private String sourceStopName; private String destinationStopName; private String vehicleType;
}
