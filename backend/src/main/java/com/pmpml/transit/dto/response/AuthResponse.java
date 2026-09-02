package com.pmpml.transit.dto.response;

import lombok.*;
import java.util.Set;
import com.pmpml.transit.enums.UserRole;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String userId;
    private String email;
    private Set<UserRole> roles;
}
