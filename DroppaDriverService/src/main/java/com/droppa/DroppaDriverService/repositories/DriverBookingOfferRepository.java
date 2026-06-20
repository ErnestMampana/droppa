package com.droppa.DroppaDriverService.repositories;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.droppa.DroppaDriverService.entity.DriverBookingOffer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverBookingOfferRepository extends JpaRepository<DriverBookingOffer, Long> {

    Optional<DriverBookingOffer> findByBookingId(String bookingId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from DriverBookingOffer o where o.bookingId = :bookingId")
    Optional<DriverBookingOffer> findByBookingIdForUpdate(@Param("bookingId") String bookingId);

    @Query("""
            select o from DriverBookingOffer o
            where lower(o.driverId) = lower(:driverId)
            and o.status in :statuses
            """)
    List<DriverBookingOffer> findDriverAssignments(
            @Param("driverId") String driverId,
            @Param("statuses") Collection<String> statuses);

    @Query("""
            select o from DriverBookingOffer o
            where o.status = 'AWAITING_DRIVER'
            and (o.driverId is null or o.driverId = '')
            and (o.lastNotificationAt is null or o.lastNotificationAt <= :cutoff)
            """)
    List<DriverBookingOffer> findOpenOffersReadyForNotification(@Param("cutoff") Instant cutoff);
}
