package com.pmpml.transit.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockEmailProvider implements EmailProvider {
    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("MOCK EMAIL to={} subject={}", to, subject);
    }
}
