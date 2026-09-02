package com.pmpml.transit.dto.response;

import com.pmpml.transit.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingResponse {
    private UUID id; private String bookingReference; private UUID tripId;
    private String sourceStopName; private String destinationStopName;
    private BigDecimal fareAmount; private BookingStatus status; private Instant createdAt;
}
