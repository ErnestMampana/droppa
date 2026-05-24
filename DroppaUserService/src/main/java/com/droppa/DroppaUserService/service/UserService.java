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

		user.confirmEmail(code);

		Token tokenData = tokenRepository.findByUserId(user.getId()).get();//.orElseThrow(() -> new TokenNotFoundException(user.getId()));

		return buildUserResponse(user, tokenData.getToken());

	}

	public UserAccount getUserByEmail(String email) {

		return userAccountRepository.findByEmail(email).orElseThrow(() -> new ClientException("Account not found"));

	}
	
//	public UserAccount getActiveUser(String email) {
//
//	    UserAccount user = getUserByEmail(email);
//	    user.ensureCanLogin();
//
//	    return user;
//	}

	@Transactional
	public String requestPasswordReset(String email) {
		String otp = "";

		UserAccount userAccount = getUserByEmail(email);

			otp = partyService.generateOTP(userAccount.getPerson().getCellphone());
			userAccount.requestPasswordReset(otp);

		return otp;
	}

	@Transactional
	public UserAccount resetPassword(String otp, String username, String password) {
		UserAccount userAcc = getUserByEmail(username);
		userAcc.resetPassword(otp, passwordEncoder.encode(password));				
		//not happy with the results
		return userAcc;
	}

	@Transactional
	public BigDecimal loadWallet(String username, BigDecimal amount) {

		UserAccount userAccount = getUserByEmail(username);
		userAccount.loadWallet(amount);

		return userAccount.getPerson().getWalletBalance();

	}
	

	
	private UserResponseDTO buildUserResponse(UserAccount user,String token) {
		return UserResponseDTO.builder().cellphone(user.getPerson().getCellphone())
				.surname(user.getPerson().getSurname()).userName(user.getPerson().getUserName())
				.token(token).walletBalance(user.getPerson().getWalletBalance())
				.email(user.getEmail()).build();
	}
	

}
