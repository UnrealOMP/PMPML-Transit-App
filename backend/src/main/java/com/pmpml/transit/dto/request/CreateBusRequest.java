package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import com.pmpml.transit.enums.VehicleType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateBusRequest {
    @NotBlank @Size(max = 20) private String busNumber;
    @NotBlank @Size(max = 100) private String name;
    @NotNull private VehicleType vehicleType;
    @NotNull @Min(1) private Integer capacity;
    private String licensePlate;
    private String manufacturer;
    private Integer manufacturingYear;
}
