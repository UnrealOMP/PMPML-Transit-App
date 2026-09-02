package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.CreateTripRequest;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.TripResponse;
import com.pmpml.transit.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Trip management")
public class TripController {
    private final TripService tripService;

    @PostMapping("/admin/trips")
    @Operation(summary = "Create a new trip (Admin)")
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        return ResponseEntity.ok(tripService.createTrip(request));
    }

    @GetMapping("/trips/{id}")
    @Operation(summary = "Get trip by ID")
    public ResponseEntity<TripResponse> getTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.getTrip(id));
    }

    @GetMapping("/trips")
    @Operation(summary = "List all trips")
    public ResponseEntity<PageResponse<TripResponse>> listTrips(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tripService.getAllTrips(page, size));
    }
}
