package com.pmpml.transit.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.Instant;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status; private String error; private String message; private String path;
    private Instant timestamp; private Map<String, String> validationErrors; private String correlationId;
}
