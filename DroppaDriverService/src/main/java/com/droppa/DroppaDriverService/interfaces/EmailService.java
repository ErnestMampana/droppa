/**
 * 
 */
package com.droppa.DroppaDriverService.interfaces;

import com.droppa.DroppaDriverService.dto.EmailDetails;

/**
 * @author Ernest Mampana
 *
 */
public interface EmailService {
	String sendSimpleMail(EmailDetails details);

	String sendMailWithAttachment(EmailDetails details);
}
