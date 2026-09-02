package com.pmpml.transit.service;

import com.pmpml.transit.dto.response.FareResponse;
import com.pmpml.transit.entity.RouteStop;
import com.pmpml.transit.entity.Stop;
import com.pmpml.transit.entity.Trip;
import com.pmpml.transit.exception.BusinessException;
import com.pmpml.transit.repository.FareRuleRepository;
import com.pmpml.transit.repository.RouteStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FareService {
    private final FareRuleRepository fareRuleRepository;
    private final RouteStopRepository routeStopRepository;
    private final TripService tripService;

    public FareResponse calculateFare(UUID sourceStopId, UUID destinationStopId, UUID tripId) {
        Trip trip = tripService.findTrip(tripId);
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderBySequenceOrderAsc(trip.getRoute().getId());
        int sourceIdx = -1, destIdx = -1;
        for (int i = 0; i < routeStops.size(); i++) {
            UUID stopId = routeStops.get(i).getStop().getId();
            if (stopId.equals(sourceStopId)) sourceIdx = i;
            if (stopId.equals(destinationStopId)) destIdx = i;
        }
        if (sourceIdx == -1 || destIdx == -1 || sourceIdx >= destIdx) throw new BusinessException("Invalid source/destination for this route");
        BigDecimal totalDistance = BigDecimal.ZERO;
        for (int i = sourceIdx; i < destIdx; i++) {
            if (routeStops.get(i).getDistanceToNextStopKm() != null) totalDistance = totalDistance.add(routeStops.get(i).getDistanceToNextStopKm());
        }
        var fareRule = fareRuleRepository.findFirstByVehicleTypeAndDistanceFromKmLessThanEqualAndDistanceToKmGreaterThanEqualAndActiveTrueOrderByVersionNumberDesc(trip.getBus().getVehicleType(), totalDistance, totalDistance);
        final BigDecimal dist = totalDistance;
        BigDecimal fare = fareRule.map(fr -> fr.getFare()).orElseGet(() -> dist.multiply(new BigDecimal("5")));
        Stop source = routeStops.get(sourceIdx).getStop();
        Stop dest = routeStops.get(destIdx).getStop();
        return FareResponse.builder().fare(fare).distanceKm(totalDistance).sourceStopName(source.getName()).destinationStopName(dest.getName()).vehicleType(trip.getBus().getVehicleType().name()).build();
    }
}
