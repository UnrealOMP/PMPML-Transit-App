package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank @Size(min = 2, max = 100) private String fullName;
    @Pattern(regexp = "^\\+?[0-9]{10,15}$") private String phoneNumber;
}
