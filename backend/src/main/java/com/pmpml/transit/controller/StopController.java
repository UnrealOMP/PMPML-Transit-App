package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.CreateStopRequest;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.StopResponse;
import com.pmpml.transit.service.StopService;
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
@Tag(name = "Stops", description = "Stop management")
public class StopController {
    private final StopService stopService;

    @PostMapping("/admin/stops")
    @Operation(summary = "Create a new stop (Admin)")
    public ResponseEntity<StopResponse> createStop(@Valid @RequestBody CreateStopRequest request) {
        return ResponseEntity.ok(stopService.createStop(request));
    }

    @GetMapping("/stops/{id}")
    @Operation(summary = "Get stop by ID")
    public ResponseEntity<StopResponse> getStop(@PathVariable UUID id) {
        return ResponseEntity.ok(stopService.getStop(id));
    }

    @GetMapping("/stops")
    @Operation(summary = "List all active stops")
    public ResponseEntity<PageResponse<StopResponse>> listStops(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(stopService.getAllStops(page, size));
    }
}
