package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Trip;
import com.pmpml.transit.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

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

    @Modifying
    @Transactional
    @Query(value = """
            WITH numbered_trips AS (
                SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS position
                FROM trips
            )
            UPDATE trips t
            SET scheduled_departure = NOW() + (n.position * INTERVAL '30 minutes'),
                scheduled_arrival = NOW() + (n.position * INTERVAL '30 minutes') + INTERVAL '50 minutes',
                status = 'SCHEDULED'
            FROM numbered_trips n
            WHERE t.id = n.id
            """, nativeQuery = true)
    void refreshDemoTripSchedule();
}
