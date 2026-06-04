package com.droppa.DroppaBookingService.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;


import com.droppa.DroppaBookingService.exceptions.PromoCodeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.droppa.DroppaBookingService.dto.EmailDetails;
import com.droppa.DroppaBookingService.dto.PromoCodeDTO;
import com.droppa.DroppaBookingService.entity.PromoCode;
import com.droppa.DroppaBookingService.repository.PromoCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyService {

	private final PromoCodeRepository promoCodeRepository;
	private static final SecureRandom secureRandom = new SecureRandom();
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
	private final EmailServiceImp emailServiceImp;
	
	
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


	public String generateTrackNumber() {
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
			// Days.daysBetween(start, end).getDays();
			if (LocalDate.now().isAfter(promoOptional.get().getExpiration()))
				throw new PromoCodeException("This promo code has EXPIRED");
			if (promoOptional.get().getNumberOfTimesUsed() < promoOptional.get().getPromoCount()) {
				discountedPrice = promoData.bookingPrice - promoOptional.get().getDiscountPrice();
				promoOptional.get().setNumberOfTimesUsed(promoOptional.get().getNumberOfTimesUsed() + 1);
				return discountedPrice;
			} else {
				throw new PromoCodeException("This Promo Code has reached maximum use");
			}
		} else {
			throw new PromoCodeException("Invalid promo code");
		}
	}
}
