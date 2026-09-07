package com.pmpml.transit.controller;


import com.pmpml.transit.dto.request.CreateBusRequest;
import com.pmpml.transit.dto.response.BusResponse;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Buses", description = "Bus management")
public class BusController {
    private final BusService busService;

    @PostMapping("/admin/buses")
    @Operation(summary = "Create a new bus (Admin)")
    public ResponseEntity<BusResponse> createBus(@Valid @RequestBody CreateBusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
        .body(busService.createBus(request));
    }

    @GetMapping("/buses/{id}")
    @Operation(summary = "Get bus by ID")
    public ResponseEntity<BusResponse> getBus(@PathVariable UUID id) {
        return ResponseEntity.ok(busService.getBus(id));
    }

    @GetMapping("/buses")
    @Operation(summary = "List all active buses")
    public ResponseEntity<PageResponse<BusResponse>> listBuses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(busService.getAllBuses(page, size));
    }

    @DeleteMapping("/admin/buses/{id}")
    @Operation(summary = "Deactivate bus (Admin)")
    public ResponseEntity<Void> deleteBus(@PathVariable UUID id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}
