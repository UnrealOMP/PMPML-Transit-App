package com.pmpml.transit.service;

import com.pmpml.transit.dto.response.TicketVerifyResponse;
import com.pmpml.transit.entity.*;
import com.pmpml.transit.enums.TicketStatus;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.TicketRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketVerificationTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    private Ticket makeTicket(String ticketNumber, TicketStatus status, String verificationToken, Instant expiresAt) {
        User user = User.builder().fullName("Test User").email("test@test.com").passwordHash("h").enabled(true).build();
        user.setId(UUID.randomUUID());

        Stop source = Stop.builder().stopCode("S1").name("Pune Station").latitude(BigDecimal.ONE).longitude(BigDecimal.ONE).active(true).build();
        source.setId(UUID.randomUUID());

        Stop dest = Stop.builder().stopCode("S2").name("Swargate").latitude(BigDecimal.ONE).longitude(BigDecimal.ONE).active(true).build();
        dest.setId(UUID.randomUUID());

        Bus bus = Bus.builder().busNumber("B001").name("Bus 1").vehicleType(com.pmpml.transit.enums.VehicleType.STANDARD).capacity(45).active(true).build();
        bus.setId(UUID.randomUUID());

        Route route = Route.builder().routeNumber("R1").name("Route 1").active(true).build();
        route.setId(UUID.randomUUID());

        Booking booking = Booking.builder().user(user).fareAmount(new BigDecimal("12.00")).idempotencyKey("ik").build();
        booking.setId(UUID.randomUUID());

        Ticket ticket = Ticket.builder()
                .ticketNumber(ticketNumber)
                .booking(booking)
                .user(user)
                .fare(new BigDecimal("12.00"))
                .bus(bus)
                .route(route)
                .sourceStop(source)
                .destinationStop(dest)
                .status(status)
                .verificationToken(verificationToken)
                .expiresAt(expiresAt)
                .issuedAt(Instant.now())
                .build();
        ticket.setId(UUID.randomUUID());
        return ticket;
    }

    @Test
    @DisplayName("Verify a valid, active ticket")
    void verifyValidTicket() {
        Ticket ticket = makeTicket("TKT-001", TicketStatus.ACTIVE, "token-valid", Instant.now().plus(2, ChronoUnit.HOURS));

        when(ticketRepository.findByVerificationToken("token-valid"))
                .thenReturn(Optional.of(ticket));

        TicketVerifyResponse result = ticketService.verifyTicket("token-valid", "conductor-123");

        assertEquals("VALID", result.getResult());
        verify(ticketRepository).save(ticketCaptor.capture());
        assertEquals(TicketStatus.USED, ticketCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Verify an expired ticket")
    void verifyExpiredTicket() {
        Ticket ticket = makeTicket("TKT-002", TicketStatus.ACTIVE, "token-expired", Instant.now().minus(1, ChronoUnit.HOURS));

        when(ticketRepository.findByVerificationToken("token-expired"))
                .thenReturn(Optional.of(ticket));

        TicketVerifyResponse result = ticketService.verifyTicket("token-expired", "conductor-123");

        assertEquals("EXPIRED", result.getResult());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Verify an already used ticket")
    void verifyAlreadyUsedTicket() {
        Ticket ticket = makeTicket("TKT-003", TicketStatus.USED, "token-used", Instant.now().plus(2, ChronoUnit.HOURS));

        when(ticketRepository.findByVerificationToken("token-used"))
                .thenReturn(Optional.of(ticket));

        TicketVerifyResponse result = ticketService.verifyTicket("token-used", "conductor-123");

        assertEquals("USED", result.getResult());
    }

    @Test
    @DisplayName("Verify a cancelled ticket")
    void verifyCancelledTicket() {
        Ticket ticket = makeTicket("TKT-004", TicketStatus.CANCELLED, "token-cancelled", Instant.now().plus(2, ChronoUnit.HOURS));

        when(ticketRepository.findByVerificationToken("token-cancelled"))
                .thenReturn(Optional.of(ticket));

        TicketVerifyResponse result = ticketService.verifyTicket("token-cancelled", "conductor-123");

        assertEquals("CANCELLED", result.getResult());
    }

    @Test
    @DisplayName("Verify a non-existent ticket throws exception")
    void verifyNonExistentTicket() {
        when(ticketRepository.findByVerificationToken("nonexistent-token"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.verifyTicket("nonexistent-token", "conductor-123"));
    }
}
