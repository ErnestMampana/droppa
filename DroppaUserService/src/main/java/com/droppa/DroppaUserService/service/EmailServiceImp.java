/**
 * 
 */
package com.droppa.DroppaUserService.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.droppa.DroppaUserService.dto.EmailDetails;
import com.droppa.DroppaUserService.interfaces.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImp implements EmailService {
	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sender;

	@Override
	public String sendSimpleMail(EmailDetails details) {
		if (sender == null || sender.isBlank()) {
			log.warn("Mail skipped because spring.mail.username is not configured");
			return "Mail skipped: spring.mail.username is not configured";
		}

		// Try block to check for exceptions
		try {

			// Creating a simple mail message
			SimpleMailMessage mailMessage = new SimpleMailMessage();

			// Setting up necessary details
			mailMessage.setFrom(sender);
			mailMessage.setTo(details.getRecipient());
			mailMessage.setText(details.getMsgBody());
			mailMessage.setSubject(details.getSubject());

			// Sending the mail
			javaMailSender.send(mailMessage);
			return "Mail Sent Successfully...";
		}

		// Catch block to handle the exceptions
		catch (Exception e) {
			log.warn("Failed to send mail to {}", details == null ? null : details.getRecipient(), e);
			return "Error while Sending Mail: " + e.getMessage();
		}
	}

	@Override
	public String sendMailWithAttachment(EmailDetails details) {
		if (sender == null || sender.isBlank()) {
			log.warn("Mail with attachment skipped because spring.mail.username is not configured");
			return "Mail skipped: spring.mail.username is not configured";
		}

		// Creating a mime message
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;

		try {

			// Setting multipart as true for attachments to
			// be send
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(sender);
			mimeMessageHelper.setTo(details.getRecipient());
			mimeMessageHelper.setText(details.getMsgBody());
			mimeMessageHelper.setSubject(details.getSubject());

			// Adding the attachment
			FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

			mimeMessageHelper.addAttachment(file.getFilename(), file);

			// Sending the mail
			javaMailSender.send(mimeMessage);
			return "Mail sent Successfully";
		}

		// Catch block to handle MessagingException
		catch (MessagingException e) {

			// Display message when exception occurred
			log.warn("Failed to send mail with attachment to {}", details == null ? null : details.getRecipient(), e);
			return "Error while sending mail: " + e.getMessage();
		}
	}

}
