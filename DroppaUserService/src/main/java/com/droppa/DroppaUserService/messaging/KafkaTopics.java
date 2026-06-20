package com.droppa.DroppaUserService.messaging;

public final class KafkaTopics {

    public static final String PAYMENT_COMMANDS = "payment-commands";
    public static final String PAYMENT_COMMANDS_DLT = "payment-commands-dlt";
    public static final String PAYMENT_EVENTS = "payment-events";
    public static final String NOTIFICATION_COMMANDS = "notification-commands";
    public static final String NOTIFICATION_COMMANDS_DLT = "notification-commands-dlt";

    private KafkaTopics() {
    }
}
