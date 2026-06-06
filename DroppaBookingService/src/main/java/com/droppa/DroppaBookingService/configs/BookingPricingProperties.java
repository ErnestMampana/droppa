package com.droppa.DroppaBookingService.configs;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.droppa.DroppaBookingService.enums.VehicleType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "booking.pricing")
public class BookingPricingProperties {

    private Map<VehicleType, BigDecimal> vehicleBasePrices = new EnumMap<>(VehicleType.class);
    private BigDecimal pricePerAdditionalLoad;
    private BigDecimal pricePerLabour;
}
