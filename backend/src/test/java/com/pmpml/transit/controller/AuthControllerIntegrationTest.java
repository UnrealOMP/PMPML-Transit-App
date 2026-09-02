package com.pmpml.transit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmpml.transit.dto.request.LoginRequest;
import com.pmpml.transit.dto.request.RegisterRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Authentication endpoints.
 * Uses MockMvc with full Spring context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String testEmail = "integration-test@example.com";
    private static String testPassword = "SecureP@ssw0rd!";

    @Test
    @Order(1)
    @DisplayName("Register a new user successfully")
    void registerNewUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setFullName("Test User");
        request.setPhoneNumber("9000000001");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(testEmail));
    }

    @Test
    @Order(2)
    @DisplayName("Duplicate registration should fail")
    void registerDuplicateUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setFullName("Test User");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("Register with invalid email should fail")
    void registerWithInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("not-an-email");
        request.setPassword(testPassword);
        request.setFullName("Test User");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("Register with weak password should fail")
    void registerWithWeakPassword() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("weak@example.com");
        request.setPassword("123");
        request.setFullName("Weak Pass");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("Login with valid credentials")
    void loginValidCredentials() throws Exception {
        // First register
        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail("login-test@example.com");
        regReq.setPassword(testPassword);
        regReq.setFullName("Login Tester");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        // Then login
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("login-test@example.com");
        loginReq.setPassword(testPassword);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("Login with wrong password should fail")
    void loginWrongPassword() throws Exception {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("login-test@example.com");
        loginReq.setPassword("WrongPassword123!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @DisplayName("Login with non-existent user should fail")
    void loginNonExistentUser() throws Exception {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("nonexistent@example.com");
        loginReq.setPassword(testPassword);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    @DisplayName("Refresh token should work")
    void refreshToken() throws Exception {
        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail("refresh-test@example.com");
        regReq.setPassword(testPassword);
        regReq.setFullName("Refresh Tester");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("refresh-test@example.com");
        loginReq.setPassword(testPassword);

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String refreshToken = objectMapper.readTree(loginResponse).get("refreshToken").asText();

        // Refresh the token
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
