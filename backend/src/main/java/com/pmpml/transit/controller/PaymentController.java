package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.PaymentWebhookRequest;
import com.pmpml.transit.dto.response.PaymentResponse;
import com.pmpml.transit.entity.Payment;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.repository.UserRepository;
import com.pmpml.transit.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Initiate payment for a booking")
    public ResponseEntity<PaymentResponse> initiatePayment(@RequestParam UUID bookingId, @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, Authentication auth) {
        String key = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Payment payment = paymentService.initiatePayment(bookingId, key, user);
        return ResponseEntity.ok(mapToResponse(payment));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment status")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID paymentId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(mapToResponse(paymentService.getPayment(paymentId, user)));
    }

    @PostMapping("/{paymentId}/verify")
    @Operation(summary = "Verify payment")
    public ResponseEntity<PaymentResponse> verifyPayment(@PathVariable UUID paymentId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(mapToResponse(paymentService.verifyPayment(paymentId, user)));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Payment webhook callback")
    public ResponseEntity<Map<String, String>> paymentWebhook(@Valid @RequestBody PaymentWebhookRequest request) {
        return ResponseEntity.ok(Map.of("status", "received"));
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder().id(p.getId()).paymentReference(p.getPaymentReference()).bookingId(p.getBooking().getId()).amount(p.getAmount()).currency(p.getCurrency()).status(p.getStatus()).providerTransactionId(p.getProviderTransactionId()).paidAt(p.getPaidAt()).build();
    }
}
