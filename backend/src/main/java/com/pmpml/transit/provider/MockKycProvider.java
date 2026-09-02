package com.pmpml.transit.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockKycProvider implements KycProvider {
    @Override
    public KycResult verify(String providerReference, String providerName) {
        log.info("MOCK: KYC verification for {} at {}", providerReference, providerName);
        return new KycResult("VERIFIED", providerReference, null);
    }
}
