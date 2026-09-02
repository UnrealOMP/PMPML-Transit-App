package com.pmpml.transit.entity;

import com.pmpml.transit.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50) private String paymentReference;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id", nullable = false) private Booking booking;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 3) private String currency = "INR";
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default private PaymentStatus status = PaymentStatus.INITIATED;
    @Column(length = 200) private String providerTransactionId;
    @Column(length = 100) private String providerName;
    private Instant paidAt;
    private Instant refundedAt;
    @Column(length = 500) private String failureReason;
    @Column(nullable = false, unique = true, length = 50) private String idempotencyKey;
    @Column(nullable = false) @Builder.Default private Integer retryCount = 0;
}
