package com.pmpml.transit.dto.response;

import com.pmpml.transit.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse {
    private UUID id; private String paymentReference; private UUID bookingId;
    private BigDecimal amount; private String currency; private PaymentStatus status;
    private String providerTransactionId; private Instant paidAt;
}
