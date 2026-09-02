package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Bus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusRepository extends JpaRepository<Bus, UUID> {
    Optional<Bus> findByBusNumber(String busNumber);
    Page<Bus> findByActiveTrue(Pageable pageable);
    List<Bus> findByHasGpsTrueAndActiveTrue();
}
