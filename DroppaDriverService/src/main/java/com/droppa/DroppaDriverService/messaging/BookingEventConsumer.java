package com.droppa.DroppaDriverService.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.droppa.DroppaDriverService.services.DriverBookingOfferService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final ObjectMapper objectMapper;
    private final DriverBookingOfferService bookingOfferService;

    @KafkaListener(topics = KafkaTopics.BOOKING_EVENTS, groupId = "driver-service-booking-events")
    public void consume(String payload) throws JsonProcessingException {
        bookingOfferService.handleBookingEvent(objectMapper.readValue(payload, BookingEvent.class));
    }
}
