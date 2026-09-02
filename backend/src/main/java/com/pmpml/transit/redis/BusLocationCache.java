package com.pmpml.transit.redis;

import com.pmpml.transit.dto.response.BusLocationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusLocationCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "bus:location:";
    public void put(UUID busId, BusLocationResponse location) {
        redisTemplate.opsForValue().set(PREFIX + busId, location, Duration.ofMinutes(30));
    }
    public BusLocationResponse get(UUID busId) {
        Object obj = redisTemplate.opsForValue().get(PREFIX + busId);
        if (obj instanceof BusLocationResponse loc) return loc;
        return null;
    }
}
