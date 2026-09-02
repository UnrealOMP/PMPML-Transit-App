package com.pmpml.transit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmpml.transit.dto.request.*;
import com.pmpml.transit.enums.VehicleType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Bus, Stop, Route admin CRUD endpoints.
 * Verifies RBAC enforcement: admin-only vs public access.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String passengerToken;
    private static java.util.UUID createdBusId;
    private static java.util.UUID createdStopId;
    private static java.util.UUID createdRouteId;

    @BeforeAll
    void loginAsAdmin() throws Exception {
        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail("admin-test-integration@pmpml.gov.in");
        regReq.setPassword("Admin@123456");
        regReq.setFullName("Test Admin");
        regReq.setPhoneNumber("9111111111");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)));

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin-test-integration@pmpml.gov.in");
        loginReq.setPassword("Admin@123456");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        adminToken = objectMapper.readTree(body).get("accessToken").asText();
    }

    @BeforeAll
    void loginAsPassenger() throws Exception {
        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail("passenger-test-integration@example.com");
        regReq.setPassword("Pass@123456");
        regReq.setFullName("Test Passenger");
        regReq.setPhoneNumber("9222222222");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)));

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("passenger-test-integration@example.com");
        loginReq.setPassword("Pass@123456");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        passengerToken = objectMapper.readTree(body).get("accessToken").asText();
    }

    // ==================== BUS TESTS ====================

    @Test
    @Order(1)
    @DisplayName("Admin can create a bus")
    void adminCreateBus() throws Exception {
        CreateBusRequest request = new CreateBusRequest();
        request.setBusNumber("INTG-TEST-001");
        request.setName("Integration Test Bus");
        request.setVehicleType(VehicleType.STANDARD);
        request.setCapacity(40);
        request.setLicensePlate("MH-12-TEST-001");

        mockMvc.perform(post("/api/v1/admin/buses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.busNumber").value("INTG-TEST-001"))
                .andDo(result -> {
                    createdBusId = java.util.UUID.fromString(
                        objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
                });
    }

    @Test
    @Order(2)
    @DisplayName("Passenger cannot create a bus (RBAC)")
    void passengerCannotCreateBus() throws Exception {
        CreateBusRequest request = new CreateBusRequest();
        request.setBusNumber("INTG-TEST-002");
        request.setName("Should Fail Bus");
        request.setVehicleType(VehicleType.STANDARD);
        request.setCapacity(40);

        mockMvc.perform(post("/api/v1/admin/buses")
                        .header("Authorization", "Bearer " + passengerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @DisplayName("Get bus by ID")
    void getBusById() throws Exception {
        mockMvc.perform(get("/api/v1/buses/" + createdBusId)
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.busNumber").value("INTG-TEST-001"));
    }

    // ==================== STOP TESTS ====================

    @Test
    @Order(10)
    @DisplayName("Admin can create a stop")
    void adminCreateStop() throws Exception {
        CreateStopRequest request = new CreateStopRequest();
        request.setStopCode("INTG-S01");
        request.setName("Integration Test Stop");
        request.setLatitude(new java.math.BigDecimal("18.5200"));
        request.setLongitude(new java.math.BigDecimal("73.8600"));
        request.setAddress("Test Address, Pune");

        mockMvc.perform(post("/api/v1/admin/stops")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Stop"))
                .andDo(result -> {
                    createdStopId = java.util.UUID.fromString(
                        objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
                });
    }

    @Test
    @Order(11)
    @DisplayName("Get stop by ID")
    void getStopById() throws Exception {
        mockMvc.perform(get("/api/v1/stops/" + createdStopId)
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Stop"));
    }

    // ==================== ROUTE TESTS ====================

    @Test
    @Order(20)
    @DisplayName("Admin can create a route")
    void adminCreateRoute() throws Exception {
        CreateRouteRequest request = new CreateRouteRequest();
        request.setRouteNumber("INTG-R1");
        request.setName("Integration Test Route");
        request.setDescription("Test route for integration tests");

        mockMvc.perform(post("/api/v1/admin/routes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.routeNumber").value("INTG-R1"))
                .andDo(result -> {
                    createdRouteId = java.util.UUID.fromString(
                        objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
                });
    }

    @Test
    @Order(21)
    @DisplayName("Get route by ID")
    void getRouteById() throws Exception {
        mockMvc.perform(get("/api/v1/routes/" + createdRouteId)
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeNumber").value("INTG-R1"));
    }

    // ==================== UNAUTHORIZED ACCESS ====================

    @Test
    @Order(30)
    @DisplayName("Unauthenticated user cannot access bus endpoint")
    void unauthenticatedGetBus() throws Exception {
        mockMvc.perform(get("/api/v1/buses/" + createdBusId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(31)
    @DisplayName("Non-existent bus returns 404")
    void getNonExistentBus() throws Exception {
        java.util.UUID fakeId = java.util.UUID.randomUUID();
        mockMvc.perform(get("/api/v1/buses/" + fakeId)
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isNotFound());
    }
}
