package com.droppa.DroppaBookingService.messaging;

public final class KafkaTopics {

    public static final String BOOKING_EVENTS = "booking-events";
    public static final String PAYMENT_COMMANDS = "payment-commands";
    public static final String PAYMENT_EVENTS = "payment-events";
    public static final String PAYMENT_EVENTS_DLT = "payment-events-dlt";
    public static final String NOTIFICATION_COMMANDS = "notification-commands";

    private KafkaTopics() {
    }
}
