package com.pmpml.transit.repository;

import com.pmpml.transit.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BusLocationRepository extends JpaRepository<BusLocation, UUID> {
    List<BusLocation> findByBusIdOrderByRecordedAtDesc(UUID busId);
    List<BusLocation> findByTripIdOrderByRecordedAtDesc(UUID tripId);
}
