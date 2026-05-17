/**
 * 
 */
package com.droppa.DroppaUserService.interfaces;

import com.droppa.DroppaUserService.dto.EmailDetails;

/**
 * @author Ernest Mampana
 *
 */
public interface EmailService {
	String sendSimpleMail(EmailDetails details);

	String sendMailWithAttachment(EmailDetails details);
}
