package com.pmpml.transit.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaymentWebhookRequest {
    @NotBlank private String paymentReference;
    @NotBlank private String status;
    private String providerTransactionId;
    private String failureReason;
}
