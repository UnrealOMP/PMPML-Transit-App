package com.pmpml.transit.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockSmsProvider implements SmsProvider {
    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("MOCK SMS to={}", phoneNumber);
    }
}
