package com.droppa.clone.droppa.services;

import java.util.List;
import java.util.Optional;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.PersonDTO;
import com.droppa.clone.droppa.dto.UserResponseDTO;
import com.droppa.clone.droppa.enums.AccountStatus;
import com.droppa.clone.droppa.enums.Role;
import com.droppa.clone.droppa.enums.TokenType;
import com.droppa.clone.droppa.models.Person;
import com.droppa.clone.droppa.models.Token;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.repositories.PersonRepository;
import com.droppa.clone.droppa.repositories.TokenRepository;
import com.droppa.clone.droppa.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserAccountRepository userAccountRepository;
	private final PartyService partyService;
	private final PersonRepository personRepository;
	private final JwtService jwtService;
	private AuthenticationService authService;
	private final TokenRepository tokenRepository;

	public List<UserAccount> getAllUsers() {
		return userAccountRepository.findAll();
	}

	@Transactional
	public UserResponseDTO confirmEmail(String email, int code) {

		String message = "User " + "'" + email + "'" + " was not found";
		UserAccount user = userAccountRepository.findByEmail(email)
				.orElseThrow(() -> new ClientException("Account not found"));

		if (user.getStatus().equals(AccountStatus.ACTIVE)) {
			message = "Account already active";
			throw new ClientException(message);
		}

		if (user.getOtp() == code) {

			user.setOtp(0);
			user.setConfirmed(true);
			user.setStatus(AccountStatus.ACTIVE);

			message = "Account Activated";

			Token tokenData = tokenRepository.findByUserId(user.getId()).get();

			return UserResponseDTO.builder().celphoneNumber(user.getPerson().getCellphone())
					.surname(user.getPerson().getSurname()).userName(user.getPerson().getUserName())
					.token(tokenData.getToken()).myBookings(null).walletBalance(user.getPerson().getWalletBalance())
					.userId(user.getEmail()).build();
		} else {
			message = "invalid otp";
			throw new ClientException(message);
		}

	}

	public UserAccount getUserByEmail(String email) {

		Optional<UserAccount> userOptional = userAccountRepository.findByEmail(email);

		if (userOptional.isPresent()) {
			UserAccount user = userOptional.get();
			return user;
		} else {
			throw new ClientException("User not fond");
		}

	}

	@Transactional
	public int requestPasswordReset(String email) {
		int otp = 0;

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
	public UserAccount resetPassword(int otp, String username, String password) {
		UserAccount userAcc = getUserByEmail(username);
		if (userAcc.getStatus().equals(AccountStatus.AWAITING_PWD_RESET)) {
			if (userAcc.getOtp() == otp) {
				userAcc.setOtp(0);
				userAcc.setPassword(password);
				userAcc.setStatus(AccountStatus.ACTIVE);
			}
		}
		return userAcc;
	}

	@Transactional
	public double loadWallet(String username, double amount) {

		UserAccount userAccount = getUserByEmail(username);
		if (userAccount.getStatus().equals(AccountStatus.ACTIVE)) {
			userAccount.getPerson().setWalletBalance(userAccount.getPerson().getWalletBalance() + amount);
			return userAccount.getPerson().getWalletBalance();
		} else {
			if (userAccount.getStatus().equals(AccountStatus.AWAITING_CONFIRMATION)) {
				throw new ClientException("Please confirm your account first.");
			} else if (userAccount.getStatus().equals(AccountStatus.AWAITING_PWD_RESET)) {
				throw new ClientException("You haven't set confirmed your new password");
			} else {
				throw new ClientException(
						"Your account has been suspended please contact Droppa Clone for re-activation.");
			}
		}

	}

	private void saveUserToken(UserAccount user, String jwtToken) {
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}

//	  private void revokeAllUserTokens(UserAccount user) {
//		    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
//		    if (validUserTokens.isEmpty())
//		      return;
//		    validUserTokens.forEach(token -> {
//		      token.setExpired(true);
//		      token.setRevoked(true);
//		    });
//		    tokenRepository.saveAll(validUserTokens);
//		  }

}
