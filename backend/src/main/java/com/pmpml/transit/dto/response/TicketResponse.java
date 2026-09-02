package com.pmpml.transit.dto.response;

import com.pmpml.transit.enums.TicketStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketResponse {
    private UUID id; private String ticketNumber; private UUID bookingId;
    private String busNumber; private String routeNumber;
    private String sourceStopName; private String destinationStopName;
    private BigDecimal fare; private Instant issuedAt; private Instant expiresAt;
    private TicketStatus status; private String qrCodeBase64;
}
