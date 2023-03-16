package com.droppa.clone.droppa.services;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

@Service
public class PartyService {
	private static final Logger logger = Logger.getLogger(PartyService.class.getName());
	private static final SecureRandom secureRandom = new SecureRandom();
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

	public int generateOTP(String mobileNumber) {
		final int max = 99999;
		final int min = 10000;
		Random random = new Random();
		int otp = random.nextInt((max - min) + 1) + min;
		logger.info("==================== OTP " + otp + " sent to mobile number " + mobileNumber);
		return otp;
	}

	public String randomChars(int length) {
		String candidateChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

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
}
