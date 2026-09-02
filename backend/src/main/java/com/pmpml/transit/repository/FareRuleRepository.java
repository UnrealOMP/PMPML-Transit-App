package com.pmpml.transit.repository;

import com.pmpml.transit.entity.FareRule;
import com.pmpml.transit.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface FareRuleRepository extends JpaRepository<FareRule, UUID> {
    Optional<FareRule> findFirstByVehicleTypeAndDistanceFromKmLessThanEqualAndDistanceToKmGreaterThanEqualAndActiveTrueOrderByVersionNumberDesc(VehicleType vehicleType, BigDecimal distanceFrom, BigDecimal distanceTo);
}
