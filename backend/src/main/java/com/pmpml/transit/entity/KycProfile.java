package com.pmpml.transit.entity;

import com.pmpml.transit.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "kyc_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KycProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false, unique = true) private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default private KycStatus status = KycStatus.NOT_STARTED;
    @Column(length = 200) private String providerReference;
    @Column(length = 100) private String providerName;
    private Instant verificationTimestamp;
    @Column(length = 500) private String rejectionReason;
}
