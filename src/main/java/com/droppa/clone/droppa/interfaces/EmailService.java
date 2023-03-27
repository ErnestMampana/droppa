/**
 * 
 */
package com.droppa.clone.droppa.interfaces;

import com.droppa.clone.droppa.dto.EmailDetails;

/**
 * @author Ernest Mampana
 *
 */
public interface EmailService {
	String sendSimpleMail(EmailDetails details);

	String sendMailWithAttachment(EmailDetails details);
}
