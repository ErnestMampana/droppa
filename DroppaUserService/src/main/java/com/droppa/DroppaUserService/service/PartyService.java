package com.droppa.DroppaUserService.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

import org.joda.time.Days;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.droppa.DroppaUserService.dto.EmailDetails;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyService {


	private static final SecureRandom secureRandom = new SecureRandom();
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
	private final EmailServiceImp emailServiceImp;

//	public int generateOTP(String email) {
//		final int max = 99999;
//		final int min = 10000;
//		Random random = new Random();
//		int otp = random.nextInt((max - min) + 1) + min;
//
//		String message = "Welcome to DroppClone.\nUse the code below to activate your account.\nCode : " + otp;
//
//		EmailDetails mailDetails = EmailDetails.builder().recipient(email).msgBody(message).subject("Promo Code")
//				.build();
//
//		//emailServiceImp.sendSimpleMail(mailDetails);
//
//		logger.info("==================== OTP " + otp + " sent to email " + email);
//		return otp;
//	}
	
	public String generateOTP(String email) {

	    SecureRandom secureRandom = new SecureRandom();

	    int otp = 10000 + secureRandom.nextInt(90000);

	    String otpCode = String.valueOf(otp);

	    String message = "Welcome to DroppClone.\n"
	            + "Use the code below to activate your account.\n"
	            + "Code: " + otpCode;

	    EmailDetails mailDetails = EmailDetails.builder()
	            .recipient(email)
	            .msgBody(message)
	            .subject("Account Verification Code")
	            .build();

	    emailServiceImp.sendSimpleMail(mailDetails);

	    log.info("OTP generated and sent to email {}", email);

	    return otpCode;
	}

	public String randomChars(int length) {
		String candidateChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		emailServiceImp.sendSimpleMail(null);
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
		}
		return sb.toString();
	}

	public String generateToken() {
		String token;
		byte[] randomBytes = new byte[97];
		secureRandom.nextBytes(randomBytes);
		token = base64Encoder.encodeToString(randomBytes);
		System.out.println("========================= : : " + token);
		return token;

	}

	public String generateTracknumber() {
		final int max = 999999;
		final int min = 100009;
		Random random = new Random();
		int ran = random.nextInt((max - min) + 1) + min;
		return "GAU" + ran;
	}

	public String generatePromoCode() {
		final int max = 999;
		final int min = 100;
		Random random = new Random();
		int ran = random.nextInt((max - min) + 1) + min;
		return "PR" + ran + "CD";
	}


}
