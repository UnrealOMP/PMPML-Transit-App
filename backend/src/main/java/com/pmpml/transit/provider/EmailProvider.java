package com.pmpml.transit.provider;

public interface EmailProvider {
    void sendEmail(String to, String subject, String body);
}
