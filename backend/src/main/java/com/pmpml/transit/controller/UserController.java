package com.pmpml.transit.controller;

import com.pmpml.transit.dto.request.KycSubmitRequest;
import com.pmpml.transit.dto.response.UserResponse;
import com.pmpml.transit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and KYC")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getProfile(auth));
    }

    @PostMapping("/kyc")
    @Operation(summary = "Submit KYC verification")
    public ResponseEntity<UserResponse> submitKyc(Authentication auth, @Valid @RequestBody KycSubmitRequest request) {
        return ResponseEntity.ok(userService.submitKyc(auth, request.getProviderName(), request.getProviderReference()));
    }
}
