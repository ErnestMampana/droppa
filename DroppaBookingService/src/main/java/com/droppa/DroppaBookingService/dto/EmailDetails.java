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
public record EmailDetails (

	 String recipient,
     String msgBody,
     String subject,
     String attachment
){}
