package com.droppa.DroppaBookingService.exceptions;

public class BookingAccessDeniedException extends RuntimeException {

    public BookingAccessDeniedException(String message) {
        super(message);
    }
}
