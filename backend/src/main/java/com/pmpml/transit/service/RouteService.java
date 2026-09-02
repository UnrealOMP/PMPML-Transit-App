package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.CreateRouteRequest;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.RouteResponse;
import com.pmpml.transit.entity.Route;
import com.pmpml.transit.entity.RouteStop;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.RouteRepository;
import com.pmpml.transit.repository.RouteStopRepository;
import com.pmpml.transit.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;

    @Transactional
    public RouteResponse createRoute(CreateRouteRequest request) {
        Route route = Route.builder().routeNumber(request.getRouteNumber()).name(request.getName()).description(request.getDescription()).build();
        route = routeRepository.save(route);
        int size = request.getStops().size();
        for (var entry : request.getStops()) {
            RouteStop rs = RouteStop.builder().route(route)
                    .stop(stopRepository.findById(entry.getStopId()).orElseThrow())
                    .sequenceOrder(entry.getSequenceOrder())
                    .distanceFromStartKm(entry.getDistanceFromStartKm())
                    .distanceToNextStopKm(entry.getDistanceToNextStopKm())
                    .isFirstStop(entry.getSequenceOrder() == 1)
                    .isLastStop(entry.getSequenceOrder() == size).build();
            routeStopRepository.save(rs);
        }
        return getRoute(route.getId());
    }

    public RouteResponse getRoute(UUID id) {
        Route route = routeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        var stops = routeStopRepository.findByRouteIdOrderBySequenceOrderAsc(id).stream()
                .map(rs -> RouteResponse.RouteStopEntry.builder().stopId(rs.getStop().getId()).stopName(rs.getStop().getName()).sequenceOrder(rs.getSequenceOrder()).distanceFromStartKm(rs.getDistanceFromStartKm()).distanceToNextStopKm(rs.getDistanceToNextStopKm()).build()).toList();
        return RouteResponse.builder().id(route.getId()).routeNumber(route.getRouteNumber()).name(route.getName()).description(route.getDescription()).active(route.getActive()).stops(stops).build();
    }

    public Route findRouteEntity(UUID id) { return routeRepository.findById(id).orElseThrow(); }

    public PageResponse<RouteResponse> getAllRoutes(int page, int size) {
        Page<Route> routes = routeRepository.findByActiveTrue(PageRequest.of(page, size));
        return PageResponse.<RouteResponse>builder().content(routes.getContent().stream().map(r -> getRoute(r.getId())).toList()).page(page).size(size).totalElements(routes.getTotalElements()).totalPages(routes.getTotalPages()).build();
    }
}
