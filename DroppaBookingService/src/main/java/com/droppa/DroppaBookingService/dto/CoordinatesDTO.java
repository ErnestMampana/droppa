/**
 * 
 */
package com.droppa.DroppaBookingService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */

@Builder
public record CoordinatesDTO(String pickupCoordinates, String dropOffCoordinates) {

}
