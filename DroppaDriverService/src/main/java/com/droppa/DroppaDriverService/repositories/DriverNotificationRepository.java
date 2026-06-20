package com.droppa.DroppaDriverService.repositories;

import java.util.List;

import com.droppa.DroppaDriverService.entity.DriverNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverNotificationRepository extends JpaRepository<DriverNotification, Long> {

    List<DriverNotification> findByDriverEmailOrderByCreatedAtDesc(String driverEmail);
}
