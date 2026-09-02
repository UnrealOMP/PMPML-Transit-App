package com.pmpml.transit.provider;

public interface SmsProvider {
    void sendSms(String phoneNumber, String message);
}
