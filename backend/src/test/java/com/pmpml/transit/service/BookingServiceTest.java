package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.BookTripRequest;
import com.pmpml.transit.dto.response.BookingResponse;
import com.pmpml.transit.dto.response.FareResponse;
import com.pmpml.transit.entity.*;
import com.pmpml.transit.enums.BookingStatus;
import com.pmpml.transit.enums.TripStatus;
import com.pmpml.transit.enums.VehicleType;
import com.pmpml.transit.exception.BusinessException;
import com.pmpml.transit.repository.BookingRepository;
import com.pmpml.transit.repository.TripRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock private BookingRepository bookingRepository;
    @Mock private TripRepository tripRepository;
    @Mock private FareService fareService;
    @Mock private TripService tripService;
    @Mock private StopService stopService;
    @Mock private UserService userService;

    private User testUser;
    private Trip testTrip;
    private Stop sourceStop;
    private Stop destStop;

    @BeforeEach
    void setup() {
        sourceStop = Stop.builder()
                .stopCode("STOP-A")
                .name("Pune Railway Station")
                .latitude(BigDecimal.valueOf(18.5286))
                .longitude(BigDecimal.valueOf(73.8750))
                .active(true)
                .build();
        sourceStop.setId(UUID.randomUUID());

        destStop = Stop.builder()
                .stopCode("STOP-B")
                .name("Swargate Bus Stand")
                .latitude(BigDecimal.valueOf(18.4922))
                .longitude(BigDecimal.valueOf(73.8872))
                .active(true)
                .build();
        destStop.setId(UUID.randomUUID());

        testUser = User.builder()
                .email("test@example.com")
                .fullName("Test User")
                .passwordHash("hashed")
                .enabled(true)
                .build();
        testUser.setId(UUID.randomUUID());

        Route route = Route.builder()
                .routeNumber("R1")
                .name("Pune Station - Swargate")
                .active(true)
                .build();
        route.setId(UUID.randomUUID());

        Bus bus = Bus.builder()
                .busNumber("PMPML-001")
                .name("Bus 001")
                .vehicleType(VehicleType.STANDARD)
                .capacity(45)
                .active(true)
                .build();
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
    @DisplayName("Create booking with valid data")
    void createBookingValid() {
        BookTripRequest request = new BookTripRequest();
        request.setTripId(testTrip.getId());
        request.setSourceStopId(sourceStop.getId());
        request.setDestinationStopId(destStop.getId());

        FareResponse fareResponse = FareResponse.builder()
                .fare(new BigDecimal("12.00"))
                .distanceKm(new BigDecimal("8.5"))
                .sourceStopName("Pune Railway Station")
                .destinationStopName("Swargate Bus Stand")
                .vehicleType("STANDARD")
                .build();

        when(tripRepository.findByIdForUpdate(testTrip.getId())).thenReturn(Optional.of(testTrip));
        when(fareService.calculateFare(sourceStop.getId(), destStop.getId(), testTrip.getId()))
                .thenReturn(fareResponse);
        when(stopService.findStop(sourceStop.getId())).thenReturn(sourceStop);
        when(stopService.findStop(destStop.getId())).thenReturn(destStop);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> {
                    Booking b = inv.getArgument(0);
                    b.setId(UUID.randomUUID());
                    return b;
                });

        BookingResponse result = bookingService.createBooking(request, UUID.randomUUID().toString(), testUser);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING_PAYMENT, result.getStatus());
        assertEquals(new BigDecimal("12.00"), result.getFareAmount());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Duplicate idempotency key returns existing booking")
    void duplicateIdempotencyKey() {
        BookTripRequest request = new BookTripRequest();
        request.setTripId(testTrip.getId());
        request.setSourceStopId(sourceStop.getId());
        request.setDestinationStopId(destStop.getId());

        String idempotencyKey = "idem-123";
        Booking existingBooking = Booking.builder()
                .idempotencyKey(idempotencyKey)
                .status(BookingStatus.CONFIRMED)
                .fareAmount(new BigDecimal("12.00"))
                .user(testUser)
                .trip(testTrip)
                .sourceStop(sourceStop)
                .destinationStop(destStop)
                .build();
        existingBooking.setId(UUID.randomUUID());

        when(bookingRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.of(existingBooking));

        BookingResponse result = bookingService.createBooking(request, idempotencyKey, testUser);

        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Booking with no available seats should fail")
    void bookingNoSeats() {
        testTrip.setAvailableSeats(0);

        BookTripRequest request = new BookTripRequest();
        request.setTripId(testTrip.getId());
        request.setSourceStopId(sourceStop.getId());
        request.setDestinationStopId(destStop.getId());

        when(tripService.findTrip(testTrip.getId())).thenReturn(testTrip);

        assertThrows(BusinessException.class,
                () -> bookingService.createBooking(request, UUID.randomUUID().toString(), testUser));
    }
}
