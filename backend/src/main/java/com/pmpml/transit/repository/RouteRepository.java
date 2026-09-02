package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, UUID> {
    Optional<Route> findByRouteNumber(String routeNumber);
    Page<Route> findByActiveTrue(Pageable pageable);
}
