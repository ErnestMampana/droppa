package com.droppa.DroppaBookingService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.droppa.DroppaBookingService.configs.BookingPricingProperties;
import com.droppa.DroppaBookingService.dto.BookingDTO;
import com.droppa.DroppaBookingService.enums.VehicleType;
import com.droppa.DroppaBookingService.exceptions.BookingException;

class BookingPricingServiceTest {

    private BookingPricingService pricingService;

    @BeforeEach
    void setUp() {
        BookingPricingProperties properties = new BookingPricingProperties();
        properties.setVehicleBasePrices(Map.of(VehicleType.ONE_TON, new BigDecimal("500.00")));
        properties.setPricePerAdditionalLoad(new BigDecimal("100.00"));
        properties.setPricePerLabour(new BigDecimal("150.00"));
        pricingService = new BookingPricingService(properties);
    }

    @Test
    void calculatesPriceFromServerTariffs() {
        BookingDTO booking = validBooking(3, 2);

        BigDecimal price = pricingService.calculatePrice(booking);

        assertEquals(new BigDecimal("1000.00"), price);
    }

    @Test
    void rejectsBookingWithoutAtLeastOneLoad() {
        BookingDTO booking = validBooking(0, 0);

        BookingException exception = assertThrows(
                BookingException.class,
                () -> pricingService.calculatePrice(booking));

        assertEquals("Booking must have at least one load", exception.getMessage());
    }

    private BookingDTO validBooking(int loads, int labours) {
        return BookingDTO.builder()
                .pickupadress("Pickup")
                .dropoffadress("Dropoff")
                .date(LocalDate.now().plusDays(1))
                .vehicle(VehicleType.ONE_TON)
                .pickUpName("Pickup contact")
                .pickUpCellphone("0712345678")
                .dropOffName("Dropoff contact")
                .dropOffPhone("0787654321")
                .paymentType("WALLET")
                .loads(loads)
                .labours(labours)
                .itemsToBeDelivered("Furniture")
                .time("10:00")
                .build();
    }
}
