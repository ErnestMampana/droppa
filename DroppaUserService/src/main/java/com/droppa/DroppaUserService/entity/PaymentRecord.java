package com.droppa.DroppaUserService.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.droppa.DroppaUserService.messaging.PaymentRequested;
import com.droppa.DroppaUserService.messaging.PaymentResult;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payment_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String requestEventId;

    @Column(nullable = false, unique = true)
    private String resultEventId;

    @Column(nullable = false)
    private String bookingId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    private String reason;

    private Instant processedAt;

    public static PaymentRecord completed(PaymentRequested request) {
        return create(request, "COMPLETED", null);
    }

    public static PaymentRecord failed(PaymentRequested request, String reason) {
        return create(request, "FAILED", reason);
    }

    private static PaymentRecord create(PaymentRequested request, String status, String reason) {
        PaymentRecord record = new PaymentRecord();
        record.requestEventId = request.eventId();
        record.resultEventId = UUID.randomUUID().toString();
        record.bookingId = request.bookingId();
        record.userEmail = request.userEmail();
        record.amount = request.amount();
        record.status = status;
        record.reason = reason;
        record.processedAt = Instant.now();
        return record;
    }

    public PaymentResult toResult() {
        return new PaymentResult(
                resultEventId,
                requestEventId,
                bookingId,
                userEmail,
                status,
                reason,
                processedAt
        );
    }
}
