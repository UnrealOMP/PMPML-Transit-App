package com.pmpml.transit.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketVerifyResponse {
    private String result; private String ticketNumber; private String passengerName;
    private String busNumber; private String routeNumber;
    private String sourceStop; private String destinationStop; private String message;
}
