package com.pmpml.transit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    // All NewTopic beans are auto-registered by Spring Boot's KafkaAdmin auto-config
    // which reads bootstrap-servers from spring.kafka.bootstrap-servers (or env override)
    @Bean
    public NewTopic userRegisteredTopic(@Value("${app.kafka.topics.user-registered}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic bookingCreatedTopic(@Value("${app.kafka.topics.booking-created}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic paymentInitiatedTopic(@Value("${app.kafka.topics.payment-initiated}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic paymentSucceededTopic(@Value("${app.kafka.topics.payment-succeeded}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic paymentFailedTopic(@Value("${app.kafka.topics.payment-failed}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic ticketGeneratedTopic(@Value("${app.kafka.topics.ticket-generated}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic ticketVerifiedTopic(@Value("${app.kafka.topics.ticket-verified}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic busLocationUpdatedTopic(@Value("${app.kafka.topics.bus-location-updated}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic tripStartedTopic(@Value("${app.kafka.topics.trip-started}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic tripCompletedTopic(@Value("${app.kafka.topics.trip-completed}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }

    @Bean
    public NewTopic notificationRequestedTopic(@Value("${app.kafka.topics.notification-requested}") String name) {
        return new NewTopic(name, 1, (short) 1);
    }
}
