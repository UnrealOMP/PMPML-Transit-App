package com.pmpml.transit.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.payment.provider", havingValue = "mock", matchIfMissing = true)
public class MockPaymentProvider implements PaymentProvider {

    @Override
    public PaymentResult initiatePayment(String paymentReference, BigDecimal amount, String currency, Map<String, String> metadata) {
        log.info("MOCK: Initiating payment {} for amount {} {}", paymentReference, amount, currency);
        String txnId = "MOCK-TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(txnId, "PROCESSING", "mock-response", null);
    }

    @Override
    public PaymentResult verifyPayment(String providerTransactionId) {
        log.info("MOCK: Verifying payment {}", providerTransactionId);
        return new PaymentResult(providerTransactionId, "SUCCESS", "mock-verified", null);
    }

    @Override
    public PaymentResult refundPayment(String providerTransactionId, BigDecimal amount) {
        log.info("MOCK: Refunding {} for amount {}", providerTransactionId, amount);
        String refundId = "MOCK-REFUND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(refundId, "REFUNDED", "mock-refund", null);
    }
}
