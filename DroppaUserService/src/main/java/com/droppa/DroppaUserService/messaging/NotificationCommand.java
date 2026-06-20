package com.droppa.DroppaUserService.messaging;

import java.time.Instant;

public record NotificationCommand(
        String eventId,
        String recipient,
        String subject,
        String message,
        String correlationId,
        Instant occurredAt
) {
}
