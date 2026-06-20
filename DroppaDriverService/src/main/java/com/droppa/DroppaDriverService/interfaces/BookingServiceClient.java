package com.droppa.DroppaDriverService.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.droppa.DroppaDriverService.dto.BookingClientResponse;

@FeignClient(name = "booking-service", url = "${services.booking.url}")
public interface BookingServiceClient {

    @PutMapping("/api/v1/booking/accept/{bookingId}")
    BookingClientResponse acceptBooking(
            @PathVariable("bookingId") String bookingId,
            @RequestHeader("X-User-Email") String driverEmail);

    @PutMapping("/api/v1/booking/startDelivery/{bookingId}")
    BookingClientResponse startDelivery(
            @PathVariable("bookingId") String bookingId,
            @RequestHeader("X-User-Email") String driverEmail);

    @PutMapping("/api/v1/booking/completeBooking/{bookingId}")
    BookingClientResponse completeBooking(
            @PathVariable("bookingId") String bookingId,
            @RequestHeader("X-User-Email") String driverEmail);
}
