package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.FareCalculateRequest;
import com.pmpml.transit.dto.response.FareResponse;
import com.pmpml.transit.service.FareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/fares")
@RequiredArgsConstructor
@Tag(name = "Fares", description = "Fare calculation")
public class FareController {
    private final FareService fareService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate fare for a route")
    public ResponseEntity<FareResponse> calculateFare(@Valid @RequestBody FareCalculateRequest request) {
        return ResponseEntity.ok(fareService.calculateFare(request.getSourceStopId(), request.getDestinationStopId(), request.getTripId()));
    }
}
