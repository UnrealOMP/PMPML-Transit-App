package com.pmpml.transit.kafka;

import com.pmpml.transit.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducer {
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final AppProperties appProperties;

    public void publish(String topic, DomainEvent event) {
        kafkaTemplate.send(topic, event.getAggregateId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) log.error("Failed to publish event {} to topic {}", event.getEventType(), topic, ex);
                    else log.debug("Published event {} to topic {}", event.getEventType(), topic);
                });
    }
}
