package com.droppa.DroppaUserService.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.droppa.DroppaUserService.repository.UserAccountRepository;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.dto.PersonDTO;
import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.DroppaUserService.enums.Role;
import com.droppa.DroppaUserService.enums.TokenType;
import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.Token;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.repository.PersonRepository;
import com.droppa.DroppaUserService.repository.TokenRepository;
import com.droppa.DroppaUserService.service.PartyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserAccountRepository userAccountRepository;
	private final PartyService partyService;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;

	public List<UserAccount> getAllUsers() {
		return userAccountRepository.findAll();
	}

	@Transactional
	public UserResponseDTO confirmEmailAccount(String email, String code) {

		UserAccount user = getUserByEmail(email);

		validateEmailConfirmation(user, code);

		user.setOtp("0");
		user.setConfirmed(true);
		user.setStatus(AccountStatus.ACTIVE);

		Token tokenData = tokenRepository.findByUserId(user.getId()).get();//.orElseThrow(() -> new TokenNotFoundException(user.getId()));

		return UserResponseDTO.builder().cellphone(user.getPerson().getCellphone())
				.surname(user.getPerson().getSurname()).userName(user.getPerson().getUserName())
				.token(tokenData.getToken()).walletBalance(user.getPerson().getWalletBalance())
				.email(user.getEmail()).build();

	}

	public UserAccount getUserByEmail(String email) {

		return userAccountRepository.findByEmail(email).orElseThrow(() -> new ClientException("Account not found"));

	}

	@Transactional
	public String requestPasswordReset(String email) {
		String otp = "";

		UserAccount userAccount = getUserByEmail(email);

		if (userAccount.getEmail().equals(email)) {
			otp = partyService.generateOTP(userAccount.getPerson().getCellphone());
			userAccount.setOtp(otp);
			userAccount.setStatus(AccountStatus.AWAITING_PWD_RESET);
		} else {
			throw new ClientException("User not found");
		}

		return otp;
	}

	@Transactional
	public UserAccount resetPassword(String otp, String username, String password) {
		UserAccount userAcc = getUserByEmail(username);
		if (userAcc.getStatus().equals(AccountStatus.AWAITING_PWD_RESET)) {
			if (userAcc.getOtp().equals(otp)) {
				userAcc.setOtp("0");
				userAcc.setPassword(passwordEncoder.encode(password));
				userAcc.setStatus(AccountStatus.ACTIVE);
			}
		}
		//not happy with the results
		return userAcc;
	}

	@Transactional
	public BigDecimal loadWallet(String username, BigDecimal amount) {

		UserAccount userAccount = getUserByEmail(username);
		validateAccountStatus(userAccount.getStatus());

		userAccount.getPerson().setWalletBalance(userAccount.getPerson().getWalletBalance().add(amount));
		return userAccount.getPerson().getWalletBalance();

	}
	
	private void validateAccountStatus(AccountStatus accountStatus) {

	    switch (accountStatus) {

	        case AWAITING_CONFIRMATION:
	            throw new ClientException(
	                    "Please confirm your account first.");

	        case AWAITING_PWD_RESET:
	            throw new ClientException(
	                    "You haven't yet confirmed your new password");

	        case SUSPENDED:
	            throw new ClientException(
	                    "Your account has been suspended please contact Droppa Clone for re-activation.");

	        default:
	            break;
	    }
	}
	
	private void validateEmailConfirmation(UserAccount userAccount,String otpCode) {
		
		if (userAccount.getStatus() == AccountStatus.ACTIVE) {
	        throw new ClientException("Account already active");
	    }

	    if (userAccount.getStatus() != AccountStatus.AWAITING_CONFIRMATION) {
	        throw new ClientException("Invalid account state");
	    }

	    if (!userAccount.getOtp().equals(otpCode)) {
	        throw new ClientException("Invalid OTP");
	    }

	    if (userAccount.getOtpExpiry().isBefore(LocalDateTime.now())) {
	        throw new ClientException("OTP expired");
	    }
	}

}
