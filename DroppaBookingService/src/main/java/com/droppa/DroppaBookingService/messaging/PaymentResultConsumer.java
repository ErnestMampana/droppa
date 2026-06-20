package com.droppa.DroppaBookingService.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.droppa.DroppaBookingService.service.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component

@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS, groupId = "booking-service-payment-results")
    public void consume(String payload) throws JsonProcessingException {
        bookingService.handlePaymentResult(objectMapper.readValue(payload, PaymentResult.class));
    }
}
