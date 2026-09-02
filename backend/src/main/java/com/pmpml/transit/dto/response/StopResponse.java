package com.pmpml.transit.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StopResponse {
    private UUID id; private String stopCode; private String name;
    private BigDecimal latitude; private BigDecimal longitude; private String address; private Boolean active;
}
