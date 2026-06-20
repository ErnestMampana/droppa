package com.droppa.DroppaUserService.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.droppa.DroppaUserService.dto.EmailDetails;
import com.droppa.DroppaUserService.service.EmailServiceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCommandConsumer {

    private final ObjectMapper objectMapper;
    private final EmailServiceImp emailService;

    @KafkaListener(topics = KafkaTopics.NOTIFICATION_COMMANDS, groupId = "user-service-notifications")
    public void consume(String payload) throws JsonProcessingException {
        NotificationCommand command = objectMapper.readValue(payload, NotificationCommand.class);
        EmailDetails details = EmailDetails.builder()
                .recipient(command.recipient())
                .subject(command.subject())
                .msgBody(command.message())
                .build();

        String result = emailService.sendSimpleMail(details);
        if (result.startsWith("Mail skipped")) {
            log.warn("Notification for {} was not sent: {}", command.recipient(), result);
            return;
        }

        if (result.startsWith("Error")) {
            throw new IllegalStateException("Notification delivery failed: " + result);
        }
    }
}
