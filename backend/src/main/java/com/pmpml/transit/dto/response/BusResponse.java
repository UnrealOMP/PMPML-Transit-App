package com.pmpml.transit.dto.response;

import com.pmpml.transit.enums.VehicleType;
import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusResponse {
    private UUID id; private String busNumber; private String name; private VehicleType vehicleType;
    private Integer capacity; private Boolean active; private String licensePlate; private Boolean hasGps;
}
