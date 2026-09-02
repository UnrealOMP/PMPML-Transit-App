package com.pmpml.transit.entity;

import com.pmpml.transit.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100) private String email;
    @Column(nullable = false) private String passwordHash;
    @Column(nullable = false, length = 100) private String fullName;
    @Column(length = 20) private String phoneNumber;
    @Column(nullable = false) @Builder.Default private Boolean enabled = true;
    @Column(nullable = false) @Builder.Default private Boolean accountLocked = false;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) @Column(name = "role") @Builder.Default
    private Set<UserRole> roles = new HashSet<>();
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private KycProfile kycProfile;
}
