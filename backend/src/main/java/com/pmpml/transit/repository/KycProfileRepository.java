package com.pmpml.transit.repository;

import com.pmpml.transit.entity.KycProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface KycProfileRepository extends JpaRepository<KycProfile, UUID> {
    Optional<KycProfile> findByUserId(UUID userId);
}
