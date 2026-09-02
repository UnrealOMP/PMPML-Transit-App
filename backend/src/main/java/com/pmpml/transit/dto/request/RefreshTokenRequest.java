package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank private String refreshToken;
}
