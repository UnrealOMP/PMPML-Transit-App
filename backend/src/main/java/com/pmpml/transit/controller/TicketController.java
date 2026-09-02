package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.TicketVerifyRequest;
import com.pmpml.transit.dto.response.PageResponse;
import com.pmpml.transit.dto.response.TicketResponse;
import com.pmpml.transit.dto.response.TicketVerifyResponse;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.repository.UserRepository;
import com.pmpml.transit.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Ticket management and verification")
public class TicketController {
    private final TicketService ticketService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable UUID id, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(ticketService.getTicket(id, user));
    }

    @GetMapping
    @Operation(summary = "Get user tickets")
    public ResponseEntity<PageResponse<TicketResponse>> getUserTickets(Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(ticketService.getUserTickets(user, page, size));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('CONDUCTOR', 'ADMIN')")
    @Operation(summary = "Verify a ticket (Conductor)")
    public ResponseEntity<TicketVerifyResponse> verifyTicket(@Valid @RequestBody TicketVerifyRequest request, Authentication auth) {
        return ResponseEntity.ok(ticketService.verifyTicket(request.getVerificationToken(), auth.getName()));
    }
}
