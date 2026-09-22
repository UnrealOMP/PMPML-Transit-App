package com.pmpml.transit.service;

import com.pmpml.transit.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoTripScheduleInitializer {

    private final TripRepository tripRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void refreshDemoTripSchedule() {
        tripRepository.refreshDemoTripSchedule();
    }
}