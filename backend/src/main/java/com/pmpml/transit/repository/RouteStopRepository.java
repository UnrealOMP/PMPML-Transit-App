package com.pmpml.transit.repository;

import com.pmpml.transit.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RouteStopRepository extends JpaRepository<RouteStop, UUID> {
    List<RouteStop> findByRouteIdOrderBySequenceOrderAsc(UUID routeId);
    List<RouteStop> findByStopId(UUID stopId);
}
