package com.pmpml.location;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<LocationRecord, UUID> {
    Optional<LocationRecord> findFirstByBusIdOrderByRecordedAtDesc(UUID busId);
}
