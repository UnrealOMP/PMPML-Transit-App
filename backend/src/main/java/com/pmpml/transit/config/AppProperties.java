package com.pmpml.transit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Location location = new Location();
    private KafkaTopics kafka = new KafkaTopics();
    @Data public static class Jwt { private String secret; private long accessTokenExpirationMs; private long refreshTokenExpirationMs; }
    @Data public static class Cors { private String allowedOrigins; }
    @Data public static class Location { private int updateIntervalSeconds; private int updateDistanceMeters; }
    @Data public static class KafkaTopics {
        private String userRegistered, bookingCreated, paymentInitiated, paymentSucceeded, paymentFailed, ticketGenerated, ticketVerified, busLocationUpdated, tripStarted, tripCompleted, notificationRequested;
    }
}
