package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateRouteRequest {
    @NotBlank @Size(max = 20) private String routeNumber;
    @NotBlank @Size(max = 200) private String name;
    private String description;
    @NotEmpty private List<RouteStopEntry> stops;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RouteStopEntry {
        @NotNull private UUID stopId;
        @NotNull @Min(1) private Integer sequenceOrder;
        private BigDecimal distanceFromStartKm;
        private BigDecimal distanceToNextStopKm;
    }
}
