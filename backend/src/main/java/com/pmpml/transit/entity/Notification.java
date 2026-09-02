package com.pmpml.transit.entity;

import com.pmpml.transit.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private NotificationType type;
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, length = 2000) private String message;
    @Column(nullable = false) @Builder.Default private Boolean read = false;
    @Column(length = 500) private String metadataJson;
}
