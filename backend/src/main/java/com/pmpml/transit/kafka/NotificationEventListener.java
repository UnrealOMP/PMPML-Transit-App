package com.pmpml.transit.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private static final String NOTIFICATION_TOPIC = "notification.requested";

    @KafkaListener(topics = NOTIFICATION_TOPIC, groupId = "notification-consumer")
    public void handleNotificationEvent(DomainEvent event) {
        log.info("Processing notification event: {} for aggregate: {}", event.getEventType(), event.getAggregateId());
    }
}
