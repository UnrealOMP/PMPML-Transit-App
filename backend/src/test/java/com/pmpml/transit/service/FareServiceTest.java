package com.pmpml.transit.service;

import com.pmpml.transit.dto.response.FareResponse;
import com.pmpml.transit.entity.*;
import com.pmpml.transit.enums.TripStatus;
import com.pmpml.transit.enums.VehicleType;
import com.pmpml.transit.exception.BusinessException;
import com.pmpml.transit.repository.FareRuleRepository;
import com.pmpml.transit.repository.RouteStopRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareServiceTest {

    @InjectMocks
    private FareService fareService;

    @Mock private FareRuleRepository fareRuleRepository;
    @Mock private RouteStopRepository routeStopRepository;
    @Mock private TripService tripService;

    private Trip testTrip;
    private UUID routeId;

    private Stop makeStop(String code, String name) {
        Stop s = Stop.builder().stopCode(code).name(name).active(true).build();
        s.setId(UUID.randomUUID());
        return s;
    }

    @BeforeEach
    void setup() {
        routeId = UUID.randomUUID();
        Route route = Route.builder().routeNumber("R1").name("Test Route").active(true).build();
        route.setId(routeId);

        Bus bus = Bus.builder().busNumber("TEST-001").name("Test Bus").vehicleType(VehicleType.STANDARD).capacity(45).active(true).build();
        bus.setId(UUID.randomUUID());

        testTrip = Trip.builder()
                .route(route)
                .bus(bus)
                .scheduledDeparture(Instant.now().plusSeconds(3600))
                .scheduledArrival(Instant.now().plusSeconds(5400))
                .status(TripStatus.SCHEDULED)
                .availableSeats(40)
                .build();
        testTrip.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Calculate fare for standard bus, short distance")
    void calculateFareStandardShort() {
        Stop source = makeStop("S1", "Stop A");
        Stop dest = makeStop("S3", "Stop C");
        Stop other = makeStop("S2", "Stop B");

        RouteStop rs1 = RouteStop.builder().stop(source).sequenceOrder(1).distanceFromStartKm(BigDecimal.ZERO)
                .distanceToNextStopKm(BigDecimal.valueOf(2.5)).isFirstStop(true).build();
        RouteStop rs2 = RouteStop.builder().stop(other).sequenceOrder(2).distanceFromStartKm(BigDecimal.valueOf(2.5))
                .distanceToNextStopKm(BigDecimal.valueOf(2.0)).build();
        RouteStop rs3 = RouteStop.builder().stop(dest).sequenceOrder(3).distanceFromStartKm(BigDecimal.valueOf(4.5))
                .isLastStop(true).build();

        when(tripService.findTrip(testTrip.getId())).thenReturn(testTrip);
        when(routeStopRepository.findByRouteIdOrderBySequenceOrderAsc(routeId))
                .thenReturn(Arrays.asList(rs1, rs2, rs3));

        FareRule fareRule = FareRule.builder()
                .fare(new BigDecimal("8.00"))
                .minimumFare(new BigDecimal("5.00"))
                .vehicleType(VehicleType.STANDARD)
                .distanceFromKm(new BigDecimal("2.0"))
                .distanceToKm(new BigDecimal("5.0"))
                .active(true)
                .versionNumber(1)
                .build();

        when(fareRuleRepository.findFirstByVehicleTypeAndDistanceFromKmLessThanEqualAndDistanceToKmGreaterThanEqualAndActiveTrueOrderByVersionNumberDesc(
                eq(VehicleType.STANDARD), eq(new BigDecimal("4.5")), eq(new BigDecimal("4.5"))))
                .thenReturn(Optional.of(fareRule));

        FareResponse response = fareService.calculateFare(source.getId(), dest.getId(), testTrip.getId());

        assertNotNull(response);
        assertEquals(new BigDecimal("8.00"), response.getFare());
        assertEquals("STANDARD", response.getVehicleType());
    }

    @Test
    @DisplayName("Calculate fare returns distance-based fallback when no rule matches")
    void calculateFareFallbackPricing() {
        Stop source = makeStop("S1", "Stop A");
        Stop dest = makeStop("S2", "Stop B");

        RouteStop rs1 = RouteStop.builder().stop(source).sequenceOrder(1).distanceFromStartKm(BigDecimal.ZERO)
                .distanceToNextStopKm(BigDecimal.valueOf(15.0)).isFirstStop(true).build();
        RouteStop rs2 = RouteStop.builder().stop(dest).sequenceOrder(2).distanceFromStartKm(BigDecimal.valueOf(15.0))
                .isLastStop(true).build();

        when(tripService.findTrip(testTrip.getId())).thenReturn(testTrip);
        when(routeStopRepository.findByRouteIdOrderBySequenceOrderAsc(routeId))
                .thenReturn(Arrays.asList(rs1, rs2));
        when(fareRuleRepository.findFirstByVehicleTypeAndDistanceFromKmLessThanEqualAndDistanceToKmGreaterThanEqualAndActiveTrueOrderByVersionNumberDesc(
                any(), any(), any()))
                .thenReturn(Optional.empty());

        FareResponse response = fareService.calculateFare(source.getId(), dest.getId(), testTrip.getId());

        assertNotNull(response);
        // Fallback: distance * 5 per km = 15 * 5 = 75
        assertEquals(0, new BigDecimal("75").compareTo(response.getFare()));
    }

    @Test
    @DisplayName("Calculate fare with invalid source/destination should fail")
    void calculateFareInvalidRoute() {
        Stop source = makeStop("X1", "Unknown Stop");
        Stop dest = makeStop("X2", "Unknown Dest");
        Stop randomStop = makeStop("R1", "Random Stop");

        RouteStop rs1 = RouteStop.builder().stop(randomStop).sequenceOrder(1).distanceFromStartKm(BigDecimal.ZERO)
                .isFirstStop(true).build();

        when(tripService.findTrip(testTrip.getId())).thenReturn(testTrip);
        when(routeStopRepository.findByRouteIdOrderBySequenceOrderAsc(routeId))
                .thenReturn(Arrays.asList(rs1));

        assertThrows(BusinessException.class,
                () -> fareService.calculateFare(source.getId(), dest.getId(), testTrip.getId()));
    }

    @Test
    @DisplayName("Calculate fare with reverse source/destination should fail")
    void calculateFareReverseOrder() {
        Stop source = makeStop("S1", "Stop A");
        Stop dest = makeStop("S2", "Stop B");

        RouteStop rs1 = RouteStop.builder().stop(source).sequenceOrder(1).distanceFromStartKm(BigDecimal.ZERO)
                .distanceToNextStopKm(BigDecimal.valueOf(5.0)).isFirstStop(true).build();
        RouteStop rs2 = RouteStop.builder().stop(dest).sequenceOrder(2).distanceFromStartKm(BigDecimal.valueOf(5.0))
                .isLastStop(true).build();

        when(tripService.findTrip(testTrip.getId())).thenReturn(testTrip);
        when(routeStopRepository.findByRouteIdOrderBySequenceOrderAsc(routeId))
                .thenReturn(Arrays.asList(rs1, rs2));

        // Source comes after destination in route order
        assertThrows(BusinessException.class,
                () -> fareService.calculateFare(dest.getId(), source.getId(), testTrip.getId()));
    }
}
