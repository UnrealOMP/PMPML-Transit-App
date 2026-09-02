package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class KycSubmitRequest {
    @NotBlank private String providerName;
    @NotBlank private String providerReference;
}
