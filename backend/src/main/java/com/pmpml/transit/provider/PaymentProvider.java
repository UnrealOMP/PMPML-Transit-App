package com.pmpml.transit.provider;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentProvider {
    PaymentResult initiatePayment(String paymentReference, BigDecimal amount, String currency, Map<String, String> metadata);
    PaymentResult verifyPayment(String providerTransactionId);
    PaymentResult refundPayment(String providerTransactionId, BigDecimal amount);

    record PaymentResult(String providerTransactionId, String status, String rawResponse, String failureReason) {}
}
