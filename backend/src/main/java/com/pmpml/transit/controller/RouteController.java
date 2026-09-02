package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.CreateRouteRequest;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.RouteResponse;
import com.pmpml.transit.service.RouteService;
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
@Tag(name = "Routes", description = "Route management")
public class RouteController {
    private final RouteService routeService;

    @PostMapping("/admin/routes")
    @Operation(summary = "Create a new route (Admin)")
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody CreateRouteRequest request) {
        return ResponseEntity.ok(routeService.createRoute(request));
    }

    @GetMapping("/routes/{id}")
    @Operation(summary = "Get route by ID")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable UUID id) {
        return ResponseEntity.ok(routeService.getRoute(id));
    }

    @GetMapping("/routes")
    @Operation(summary = "List all active routes")
    public ResponseEntity<PageResponse<RouteResponse>> listRoutes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(routeService.getAllRoutes(page, size));
    }
}
