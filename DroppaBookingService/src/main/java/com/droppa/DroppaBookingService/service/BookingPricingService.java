package com.droppa.DroppaBookingService.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.droppa.DroppaBookingService.configs.BookingPricingProperties;
import com.droppa.DroppaBookingService.dto.BookingDTO;
import com.droppa.DroppaBookingService.exceptions.BookingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingPricingService {

    private final BookingPricingProperties pricingProperties;

    public BigDecimal calculatePrice(BookingDTO booking) {
        if (booking == null || booking.vehicle() == null) {
            throw new BookingException("Vehicle type is required to calculate booking price");
        }

        BigDecimal basePrice = pricingProperties.getVehicleBasePrices().get(booking.vehicle());

        if (basePrice == null) {
            throw new BookingException("No price configured for vehicle type " + booking.vehicle());
        }
        if (pricingProperties.getPricePerAdditionalLoad() == null
                || pricingProperties.getPricePerLabour() == null) {
            throw new BookingException("Booking pricing configuration is incomplete");
        }

        BigDecimal additionalLoadPrice = pricingProperties.getPricePerAdditionalLoad()
                .multiply(BigDecimal.valueOf(booking.loads() - 1L));
        BigDecimal labourPrice = pricingProperties.getPricePerLabour()
                .multiply(BigDecimal.valueOf(booking.labours()));

        return basePrice
                .add(additionalLoadPrice)
                .add(labourPrice)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
