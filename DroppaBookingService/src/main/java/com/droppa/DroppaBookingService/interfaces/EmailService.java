/**
 * 
 */
package com.droppa.DroppaBookingService.interfaces;

import com.droppa.DroppaBookingService.dto.EmailDetails;

/**
 * @author Ernest Mampana
 *
 */
public interface EmailService {
	String sendSimpleMail(EmailDetails details);

	String sendMailWithAttachment(EmailDetails details);
}
