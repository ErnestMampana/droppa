package com.droppa.DroppaUserService.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.droppa.DroppaUserService.service.PaymentProcessingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentCommandConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentProcessingService paymentProcessingService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMMANDS, groupId = "user-service-payment-commands")
    public void consume(String payload) throws JsonProcessingException {
        paymentProcessingService.process(objectMapper.readValue(payload, PaymentRequested.class));
    }
}
