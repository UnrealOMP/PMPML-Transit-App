package com.pmpml.transit.provider;

public interface KycProvider {
    KycResult verify(String providerReference, String providerName);
    record KycResult(String status, String reference, String rejectionReason) {}
}
