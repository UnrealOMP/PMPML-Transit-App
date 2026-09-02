package com.pmpml.location;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class LocationController {
    private final LocationRepository repository;
    public LocationController(LocationRepository repository) { this.repository = repository; }

    @GetMapping("/buses/{busId}/location")
    public LocationResponse getLatest(@PathVariable UUID busId) {
        LocationRecord record = repository.findFirstByBusIdOrderByRecordedAtDesc(busId)
                .orElseThrow(() -> new LocationNotFoundException());
        return LocationResponse.from(record);
    }

    @PostMapping("/gps/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public ResponseEntity<LocationResponse> update(@Valid @RequestBody GpsUpdate request) {
        LocationRecord record = new LocationRecord();
        record.setId(UUID.randomUUID()); record.setBusId(request.busId()); record.setBusNumber(request.busNumber());
        record.setLatitude(request.latitude()); record.setLongitude(request.longitude()); record.setSpeed(request.speed());
        record.setHeading(request.heading()); record.setRecordedAt(request.timestamp());
        return ResponseEntity.accepted().body(LocationResponse.from(repository.save(record)));
    }

    public record GpsUpdate(@NotNull UUID busId, @NotNull String busNumber,
                            @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
                            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
                            BigDecimal speed, BigDecimal heading, @NotNull @PastOrPresent Instant timestamp) { }
    public record LocationResponse(UUID busId, String busNumber, BigDecimal latitude, BigDecimal longitude,
                                   BigDecimal speed, BigDecimal heading, Instant timestamp) {
        static LocationResponse from(LocationRecord record) { return new LocationResponse(record.getBusId(), record.getBusNumber(), record.getLatitude(), record.getLongitude(), record.getSpeed(), record.getHeading(), record.getRecordedAt()); }
    }
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    static class LocationNotFoundException extends RuntimeException { }
}
