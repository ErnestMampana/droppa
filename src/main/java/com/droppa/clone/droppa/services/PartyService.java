package com.droppa.clone.droppa.services;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.PromoCodeDTO;
import com.droppa.clone.droppa.models.PromoCode;
import com.droppa.clone.droppa.repositories.PromoCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyService {

	private final PromoCodeRepository promoCodeRepository;
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

	public String generatePromoCode() {
		final int max = 999;
		final int min = 100;
		Random random = new Random();
		int ran = random.nextInt((max - min) + 1) + min;
		return "PR" + ran + "CD";
	}

	@Transactional
	public double applyPromocode(PromoCodeDTO promoData) {
		double discountedPrice = 0.0;
		Optional<PromoCode> promoOptional = promoCodeRepository.findByPromoCode(promoData.promoCode);
		if (promoOptional.isPresent()) {
			if (promoOptional.get().getExpiration().isAfter(LocalDate.of(2023, 03, 18)))
				throw new ClientException("This promo code has EXPIRED");
			if (promoOptional.get().getNumberOfTimesUsed() < promoOptional.get().getPromoCount()) {
				discountedPrice = promoData.bookingPrice - promoOptional.get().getDiscountPrice();
				promoOptional.get().setNumberOfTimesUsed(promoOptional.get().getNumberOfTimesUsed() + 1);
				return discountedPrice;
			} else {
				throw new ClientException("This Promo Code has reached maximum use");
			}
		} else {
			throw new ClientException("Invalid promo code");
		}
	}
}
