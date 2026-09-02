package com.pmpml.transit.dto.response;

import com.pmpml.transit.enums.UserRole;
import com.pmpml.transit.enums.KycStatus;
import lombok.*;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private UUID id; private String email; private String fullName; private String phoneNumber;
    private Set<UserRole> roles; private KycStatus kycStatus; private Instant createdAt;
}
