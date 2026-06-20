package com.droppa.DroppaUserService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.DroppaUserService.entity.PaymentRecord;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByRequestEventId(String requestEventId);
}
