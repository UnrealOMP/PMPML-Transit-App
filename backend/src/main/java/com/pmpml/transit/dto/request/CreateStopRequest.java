package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateStopRequest {
    @NotBlank @Size(max = 20) private String stopCode;
    @NotBlank @Size(max = 200) private String name;
    @NotNull private BigDecimal latitude;
    @NotNull private BigDecimal longitude;
    private String address;
}
