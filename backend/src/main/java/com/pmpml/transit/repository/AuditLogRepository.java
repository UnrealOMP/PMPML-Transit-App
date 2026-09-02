package com.pmpml.transit.repository;

import com.pmpml.transit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserIdOrderByTimestampDesc(UUID userId);
    Page<AuditLog> findByTimestampBetweenOrderByTimestampDesc(Instant start, Instant end, Pageable pageable);
}
