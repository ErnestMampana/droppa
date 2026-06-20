package com.droppa.DroppaUserService.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.droppa.DroppaUserService.entity.PaymentRecord;
import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.messaging.PaymentRequested;
import com.droppa.DroppaUserService.messaging.PaymentResult;
import com.droppa.DroppaUserService.repository.PaymentRecordRepository;

@ExtendWith(MockitoExtension.class)
class PaymentProcessingServiceTest {

    @Mock
    private PaymentRecordRepository paymentRecordRepository;
    @Mock
    private UserService userService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentProcessingService paymentProcessingService;

    @Test
    void debitsWalletAndPublishesCompletedResult() {
        PaymentRequested request = request("request-1");
        UserAccount user = mock(UserAccount.class);
        Person person = mock(Person.class);

        when(paymentRecordRepository.findByRequestEventId("request-1")).thenReturn(Optional.empty());
        when(userService.getUserByEmail("customer@example.com")).thenReturn(user);
        when(user.getPerson()).thenReturn(person);

        paymentProcessingService.process(request);

        verify(person).debit(new BigDecimal("725.00"));
        verify(paymentRecordRepository).save(any(PaymentRecord.class));
        verify(eventPublisher).publishEvent(any(PaymentResult.class));
    }

    @Test
    void doesNotDebitWalletAgainForDuplicateCommand() {
        PaymentRequested request = request("request-1");
        PaymentRecord existingRecord = mock(PaymentRecord.class);
        PaymentResult existingResult = new PaymentResult(
                "result-1",
                "request-1",
                "booking-1",
                "customer@example.com",
                "COMPLETED",
                null,
                Instant.now()
        );

        when(paymentRecordRepository.findByRequestEventId("request-1"))
                .thenReturn(Optional.of(existingRecord));
        when(existingRecord.toResult()).thenReturn(existingResult);

        paymentProcessingService.process(request);

        verify(userService, never()).getUserByEmail(any());
        verify(paymentRecordRepository, never()).save(any());
        verify(eventPublisher).publishEvent(existingResult);
    }

    private PaymentRequested request(String eventId) {
        return new PaymentRequested(
                eventId,
                "booking-1",
                "customer@example.com",
                new BigDecimal("725.00"),
                "WALLET",
                null,
                Instant.now()
        );
    }
}
