package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.CreateTripRequest;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.TripResponse;
import com.pmpml.transit.entity.Trip;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final BusService busService;
    private final RouteService routeService;

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        Trip trip = Trip.builder().bus(busService.findBus(request.getBusId())).route(routeService.findRouteEntity(request.getRouteId())).scheduledDeparture(request.getScheduledDeparture()).scheduledArrival(request.getScheduledArrival()).availableSeats(request.getAvailableSeats()).build();
        return mapToResponse(tripRepository.save(trip));
    }

    public TripResponse getTrip(UUID id) { return mapToResponse(findTrip(id)); }

    public PageResponse<TripResponse> getAllTrips(int page, int size) {
        Page<Trip> trips = tripRepository.findAll(PageRequest.of(page, size));
        return PageResponse.<TripResponse>builder().content(trips.getContent().stream().map(this::mapToResponse).toList()).page(page).size(size).totalElements(trips.getTotalElements()).totalPages(trips.getTotalPages()).build();
    }

    public Trip findTrip(UUID id) { return tripRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found")); }

    private TripResponse mapToResponse(Trip t) {
        return TripResponse.builder().id(t.getId()).busId(t.getBus().getId()).busNumber(t.getBus().getBusNumber()).routeId(t.getRoute().getId()).routeNumber(t.getRoute().getRouteNumber()).scheduledDeparture(t.getScheduledDeparture()).scheduledArrival(t.getScheduledArrival()).status(t.getStatus()).availableSeats(t.getAvailableSeats()).delayed(t.getDelayed()).build();
    }
}
