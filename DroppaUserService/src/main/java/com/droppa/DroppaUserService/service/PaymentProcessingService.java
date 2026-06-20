package com.droppa.DroppaUserService.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.droppa.DroppaUserService.entity.PaymentRecord;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.exception.UserNotFoundException;
import com.droppa.DroppaUserService.messaging.PaymentRequested;
import com.droppa.DroppaUserService.repository.PaymentRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentRecordRepository paymentRecordRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void process(PaymentRequested request) {
        paymentRecordRepository.findByRequestEventId(request.eventId())
                .ifPresentOrElse(
                        record -> eventPublisher.publishEvent(record.toResult()),
                        () -> processNewPayment(request)
                );
    }

    private void processNewPayment(PaymentRequested request) {
        PaymentRecord record;

        try {
            if (!"WALLET".equalsIgnoreCase(request.paymentType())) {
                throw new ClientException("Unsupported payment type: " + request.paymentType());
            }

            UserAccount user = userService.getUserByEmail(request.userEmail());
            user.ensureIsHealthy();
            user.getPerson().debit(request.amount());
            record = PaymentRecord.completed(request);
        } catch (ClientException | UserNotFoundException ex) {
            record = PaymentRecord.failed(request, ex.getMessage());
        }

        paymentRecordRepository.save(record);
        eventPublisher.publishEvent(record.toResult());
    }
}
