package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.BookTripRequest;
import com.pmpml.transit.dto.response.BookingResponse;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.repository.UserRepository;
import com.pmpml.transit.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Bus ticket booking")
public class BookingController {
    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookTripRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication auth, HttpServletRequest httpRequest) {
        String key = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(bookingService.createBooking(request, key, user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(bookingService.getBooking(id, user));
    }

    @GetMapping
    @Operation(summary = "Get user bookings")
    public ResponseEntity<PageResponse<BookingResponse>> getUserBookings(Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(bookingService.getUserBookings(user, page, size));
    }
}
