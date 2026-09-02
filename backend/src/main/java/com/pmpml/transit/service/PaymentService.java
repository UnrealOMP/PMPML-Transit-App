package com.pmpml.transit.service;

import com.pmpml.transit.entity.Booking;
import com.pmpml.transit.entity.Payment;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.enums.BookingStatus;
import com.pmpml.transit.enums.PaymentStatus;
import com.pmpml.transit.exception.BusinessException;
import com.pmpml.transit.exception.ConflictException;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.BookingRepository;
import com.pmpml.transit.repository.PaymentRepository;
import com.pmpml.transit.provider.PaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentProvider paymentProvider;
    private final TicketService ticketService;

    @Transactional
    public Payment initiatePayment(UUID bookingId, String idempotencyKey, User user) {
        var existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) return existing.get();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        ensureOwner(booking, user);
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) throw new BusinessException("Booking is not in pending payment state");
        booking.setStatus(BookingStatus.PAYMENT_PROCESSING);
        bookingRepository.save(booking);
        Payment payment = Payment.builder().paymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()).booking(booking).amount(booking.getFareAmount()).currency("INR").idempotencyKey(idempotencyKey).build();
        var result = paymentProvider.initiatePayment(payment.getPaymentReference(), payment.getAmount(), payment.getCurrency(), Map.of("bookingRef", booking.getBookingReference()));
        payment.setProviderTransactionId(result.providerTransactionId());
        payment.setProviderName("MOCK");
        payment.setStatus(PaymentStatus.PROCESSING);
        payment = paymentRepository.save(payment);
        log.info("Payment initiated: {} for booking: {}", payment.getPaymentReference(), booking.getBookingReference());
        return payment;
    }

    public Payment getPayment(UUID id, User user) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        ensureOwner(payment.getBooking(), user);
        return payment;
    }

    @Transactional
    public Payment verifyPayment(UUID paymentId, User user) {
        Payment payment = getPayment(paymentId, user);
        if (payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.FAILED) return payment;
        var result = paymentProvider.verifyPayment(payment.getProviderTransactionId());
        if ("SUCCESS".equals(result.status())) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(java.time.Instant.now());
            payment.getBooking().setStatus(BookingStatus.CONFIRMED);
            ticketService.generateTicket(payment.getBooking());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(result.failureReason());
            payment.getBooking().setStatus(BookingStatus.FAILED);
        }
        paymentRepository.save(payment);
        bookingRepository.save(payment.getBooking());
        return payment;
    }

    private void ensureOwner(Booking booking, User user) {
        if (!booking.getUser().getId().equals(user.getId())) throw new ResourceNotFoundException("Payment not found");
    }
}
