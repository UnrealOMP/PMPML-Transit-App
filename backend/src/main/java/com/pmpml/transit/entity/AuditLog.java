package com.pmpml.transit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog extends BaseEntity {
    @Column(nullable = false) private UUID userId;
    @Column(nullable = false, length = 100) private String action;
    @Column(nullable = false, length = 100) private String entityName;
    @Column(nullable = false) private UUID entityId;
    @Column(length = 2000) private String details;
    @Column(length = 50) private String ipAddress;
    @Column(nullable = false) private Instant timestamp;
    @Column(length = 200) private String correlationId;
}
