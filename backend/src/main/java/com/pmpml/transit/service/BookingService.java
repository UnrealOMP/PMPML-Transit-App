package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.BookTripRequest;
import com.pmpml.transit.dto.response.BookingResponse;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.entity.Booking;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.enums.BookingStatus;
import com.pmpml.transit.exception.BusinessException;
import com.pmpml.transit.exception.ConflictException;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final com.pmpml.transit.repository.TripRepository tripRepository;
    private final FareService fareService;
    private final TripService tripService;
    private final StopService stopService;
    private final UserService userService;

    @Transactional
    public BookingResponse createBooking(BookTripRequest request, String idempotencyKey, User user) {
        var existing = bookingRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Duplicate booking request with idempotency key: {}", idempotencyKey);
            return mapToResponse(existing.get());
        }
        var trip = tripRepository.findByIdForUpdate(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        if (trip.getAvailableSeats() <= 0) throw new BusinessException("No seats available");
        if (trip.getScheduledDeparture().isBefore(java.time.Instant.now())) throw new BusinessException("Trip has already departed");
        var fareResponse = fareService.calculateFare(request.getSourceStopId(), request.getDestinationStopId(), request.getTripId());
        var sourceStop = stopService.findStop(request.getSourceStopId());
        var destStop = stopService.findStop(request.getDestinationStopId());
        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        Booking booking = Booking.builder()
                .bookingReference("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user).trip(trip).sourceStop(sourceStop).destinationStop(destStop)
                .fareAmount(fareResponse.getFare()).idempotencyKey(idempotencyKey).build();
        booking = bookingRepository.save(booking);
        log.info("Booking created: {} for user: {}", booking.getBookingReference(), user.getEmail());
        return mapToResponse(booking);
    }

    public BookingResponse getBooking(UUID id, User user) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(user.getId())) throw new ResourceNotFoundException("Booking not found");
        return mapToResponse(booking);
    }

    public PageResponse<BookingResponse> getUserBookings(User user, int page, int size) {
        Page<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
        return PageResponse.<BookingResponse>builder().content(bookings.getContent().stream().map(this::mapToResponse).toList()).page(page).size(size).totalElements(bookings.getTotalElements()).totalPages(bookings.getTotalPages()).build();
    }

    private BookingResponse mapToResponse(Booking b) {
        return BookingResponse.builder().id(b.getId()).bookingReference(b.getBookingReference()).tripId(b.getTrip().getId()).sourceStopName(b.getSourceStop().getName()).destinationStopName(b.getDestinationStop().getName()).fareAmount(b.getFareAmount()).status(b.getStatus()).createdAt(b.getCreatedAt()).build();
    }
}
