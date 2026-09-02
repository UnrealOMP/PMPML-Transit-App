package com.pmpml.transit.repository;

import com.pmpml.transit.entity.Stop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StopRepository extends JpaRepository<Stop, UUID> {
    Optional<Stop> findByStopCode(String stopCode);
    Page<Stop> findByActiveTrue(Pageable pageable);
}
