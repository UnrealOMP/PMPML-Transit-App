package com.pmpml.transit.service;

import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.TicketResponse;
import com.pmpml.transit.dto.response.TicketVerifyResponse;
import com.pmpml.transit.entity.Booking;
import com.pmpml.transit.entity.Ticket;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.enums.BookingStatus;
import com.pmpml.transit.enums.TicketStatus;
import com.pmpml.transit.exception.BusinessException;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.BookingRepository;
import com.pmpml.transit.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Ticket generateTicket(Booking booking) {
        if (booking.getStatus() != BookingStatus.CONFIRMED) throw new BusinessException("Booking not confirmed");
        var existing = ticketRepository.findByBookingId(booking.getId());
        if (existing.isPresent()) return existing.get();
        String ticketNumber = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String verificationToken = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Ticket ticket = Ticket.builder().ticketNumber(ticketNumber).booking(booking).user(booking.getUser()).trip(booking.getTrip()).bus(booking.getTrip().getBus()).route(booking.getTrip().getRoute()).sourceStop(booking.getSourceStop()).destinationStop(booking.getDestinationStop()).fare(booking.getFareAmount()).issuedAt(now).expiresAt(booking.getTrip().getScheduledArrival()).verificationToken(verificationToken).build();
        ticket = ticketRepository.save(ticket);
        log.info("Ticket generated: {} for booking: {}", ticketNumber, booking.getBookingReference());
        return ticket;
    }

    public TicketResponse getTicket(UUID id, User user) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if (!ticket.getUser().getId().equals(user.getId())) throw new ResourceNotFoundException("Ticket not found");
        return mapToResponse(ticket);
    }

    public PageResponse<TicketResponse> getUserTickets(User user, int page, int size) {
        Page<Ticket> tickets = ticketRepository.findByUserIdOrderByIssuedAtDesc(user.getId(), PageRequest.of(page, size));
        return PageResponse.<TicketResponse>builder().content(tickets.getContent().stream().map(this::mapToResponse).toList()).page(page).size(size).totalElements(tickets.getTotalElements()).totalPages(tickets.getTotalPages()).build();
    }

    @Transactional
    public TicketVerifyResponse verifyTicket(String verificationToken, String verifierId) {
        Ticket ticket = ticketRepository.findByVerificationToken(verificationToken).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if (ticket.getExpiresAt().isBefore(Instant.now())) return TicketVerifyResponse.builder().result("EXPIRED").message("Ticket has expired").build();
        if (ticket.getStatus() == TicketStatus.USED) return TicketVerifyResponse.builder().result("USED").ticketNumber(ticket.getTicketNumber()).message("Ticket already used").build();
        if (ticket.getStatus() == TicketStatus.CANCELLED) return TicketVerifyResponse.builder().result("CANCELLED").ticketNumber(ticket.getTicketNumber()).message("Ticket cancelled").build();
        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);
        return TicketVerifyResponse.builder().result("VALID").ticketNumber(ticket.getTicketNumber()).passengerName(ticket.getUser().getFullName()).busNumber(ticket.getBus().getBusNumber()).routeNumber(ticket.getRoute().getRouteNumber()).sourceStop(ticket.getSourceStop().getName()).destinationStop(ticket.getDestinationStop().getName()).message("Ticket verified successfully").build();
    }

    private TicketResponse mapToResponse(Ticket t) {
        return TicketResponse.builder().id(t.getId()).ticketNumber(t.getTicketNumber()).bookingId(t.getBooking().getId()).busNumber(t.getBus().getBusNumber()).routeNumber(t.getRoute().getRouteNumber()).sourceStopName(t.getSourceStop().getName()).destinationStopName(t.getDestinationStop().getName()).fare(t.getFare()).issuedAt(t.getIssuedAt()).expiresAt(t.getExpiresAt()).status(t.getStatus()).build();
    }
}
