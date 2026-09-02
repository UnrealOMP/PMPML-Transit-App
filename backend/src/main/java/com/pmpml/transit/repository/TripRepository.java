package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Trip;
import com.pmpml.transit.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trip t where t.id = :id")
    java.util.Optional<Trip> findByIdForUpdate(UUID id);
    List<Trip> findByRouteIdAndStatus(UUID routeId, TripStatus status);
    List<Trip> findByBusIdAndStatus(UUID busId, TripStatus status);
    Page<Trip> findByRouteIdAndScheduledDepartureBetween(UUID routeId, Instant start, Instant end, Pageable pageable);
}
