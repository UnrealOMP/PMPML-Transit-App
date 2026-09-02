package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.GpsUpdateRequest;
import com.pmpml.transit.dto.response.BusLocationResponse;
import com.pmpml.transit.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Location", description = "Real-time bus GPS tracking")
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/gps/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    @Operation(summary = "Submit GPS update from an authorised bus device or operations feed")
    public ResponseEntity<Void> submitGpsUpdate(@Valid @RequestBody GpsUpdateRequest request) {
        locationService.processGpsUpdate(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/buses/{busId}/location")
    @Operation(summary = "Get latest bus location")
    public ResponseEntity<BusLocationResponse> getBusLocation(@PathVariable UUID busId) {
        return ResponseEntity.ok(locationService.getLatestLocation(busId));
    }
}
