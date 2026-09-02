package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.GpsUpdateRequest;
import com.pmpml.transit.dto.response.BusLocationResponse;
import com.pmpml.transit.entity.Bus;
import com.pmpml.transit.entity.BusLocation;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.BusLocationRepository;
import com.pmpml.transit.repository.BusRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {
    private final BusLocationRepository busLocationRepository;
    private final BusRepository busRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final String BUS_LOCATION_KEY = "bus:location:";
    private static final double MIN_DISTANCE_METERS = 100;
    private static final int MIN_INTERVAL_SECONDS = 10;

    public void processGpsUpdate(GpsUpdateRequest request) {
        Bus bus = busRepository.findById(request.getBusId()).orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        String key = BUS_LOCATION_KEY + bus.getId();
        BusLocationResponse cachedLoc = getBusLocationFromRedis(key);
        if (cachedLoc != null) {
            long timeDiff = Instant.now().getEpochSecond() - cachedLoc.getTimestamp().getEpochSecond();
            double dist = haversine(cachedLoc.getLatitude().doubleValue(), cachedLoc.getLongitude().doubleValue(), request.getLatitude().doubleValue(), request.getLongitude().doubleValue());
            if (timeDiff < MIN_INTERVAL_SECONDS && dist < MIN_DISTANCE_METERS) {
                log.debug("Skipping GPS update for bus {} - too frequent/close", bus.getBusNumber());
                return;
            }
        }
        BusLocation location = BusLocation.builder().bus(bus).latitude(request.getLatitude()).longitude(request.getLongitude()).speed(request.getSpeed()).heading(request.getHeading()).recordedAt(request.getTimestamp()).build();
        busLocationRepository.save(location);
        BusLocationResponse response = BusLocationResponse.builder().busId(bus.getId()).busNumber(bus.getBusNumber()).tripId(request.getTripId()).latitude(request.getLatitude()).longitude(request.getLongitude()).speed(request.getSpeed()).heading(request.getHeading()).timestamp(request.getTimestamp()).build();
        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(30));
        messagingTemplate.convertAndSend("/topic/bus." + bus.getId(), response);
        log.debug("GPS update processed for bus {}", bus.getBusNumber());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public BusLocationResponse getLatestLocation(UUID busId) {
        String key = BUS_LOCATION_KEY + busId;
        BusLocationResponse response = getBusLocationFromRedis(key);
        if (response != null) return response;
        return busLocationRepository.findByBusIdOrderByRecordedAtDesc(busId).stream()
                .findFirst()
                .map(this::mapLocation)
                .orElseThrow(() -> new ResourceNotFoundException("No location data for bus"));
    }

    @SuppressWarnings("unchecked")
    private BusLocationResponse getBusLocationFromRedis(String key) {
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof BusLocationResponse loc) return loc;
        if (cached instanceof Map<?, ?> map) {
            return objectMapper.convertValue(map, BusLocationResponse.class);
        }
        return null;
    }

    private BusLocationResponse mapLocation(BusLocation location) {
        return BusLocationResponse.builder()
                .busId(location.getBus().getId())
                .busNumber(location.getBus().getBusNumber())
                .tripId(location.getTrip() == null ? null : location.getTrip().getId())
                .latitude(location.getLatitude()).longitude(location.getLongitude())
                .speed(location.getSpeed()).heading(location.getHeading())
                .timestamp(location.getRecordedAt()).build();
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}
